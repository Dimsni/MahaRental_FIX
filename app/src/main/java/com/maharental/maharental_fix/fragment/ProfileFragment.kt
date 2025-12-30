package com.maharental.maharental_fix.fragment

import android.app.Activity
import android.app.AlertDialog
import android.app.Dialog
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Base64
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.UserProfileChangeRequest
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import com.maharental.maharental_fix.Login.LoginActivity
import com.maharental.maharental_fix.R
import com.maharental.maharental_fix.databinding.FragmentProfileBinding
import java.io.ByteArrayOutputStream

class ProfileFragment : Fragment() {

    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding!!

    // Firebase
    private lateinit var auth: FirebaseAuth
    private lateinit var db: FirebaseFirestore

    // Variabel gambar
    private var imageUri: Uri? = null

    // Penanda status foto (untuk logika tombol Simpan)
    private var isPhotoRemoved = false

    // Launcher Galeri
    private val pickImage = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val data: Intent? = result.data
            imageUri = data?.data

            // Update UI dan reset status hapus
            binding.ivProfile.setImageURI(imageUri)
            isPhotoRemoved = false
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentProfileBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        auth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()

        loadUserProfile()

        // Ganti listener klik langsung openGallery menjadi showPhotoOptions
        binding.cardProfile.setOnClickListener { showPhotoOptions() }
        binding.ivProfile.setOnClickListener { showPhotoOptions() }

        binding.btnSave.setOnClickListener { saveUserProfile() }
        binding.btnLogout.setOnClickListener { logoutUser() }
    }

    // --- FITUR BARU: MENU OPSI FOTO ---
    private fun showPhotoOptions() {
        val options = arrayOf("Ubah Foto", "Hapus Foto", "Lihat Foto", "Batal")
        val builder = AlertDialog.Builder(requireContext())
        builder.setTitle("Foto Profil")
        builder.setItems(options) { dialog, which ->
            when (which) {
                0 -> openGallery() // Ubah
                1 -> deletePhotoAction() // Hapus
                2 -> showFullPhoto() // Lihat
                3 -> dialog.dismiss() // Batal
            }
        }
        builder.show()
    }

    private fun openGallery() {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        pickImage.launch(intent)
    }

    private fun deletePhotoAction() {
        // Hanya mengubah tampilan UI dan status, belum menghapus di DB (tunggu klik Simpan)
        binding.ivProfile.setImageResource(R.drawable.baseline_account_circle_24)
        imageUri = null
        isPhotoRemoved = true
        Toast.makeText(context, "Foto dihapus (Klik Simpan untuk menerapkan)", Toast.LENGTH_SHORT).show()
    }

    private fun showFullPhoto() {
        // Ambil gambar saat ini dari ImageView
        val drawable = binding.ivProfile.drawable
        if (drawable == null || (drawable.constantState == resources.getDrawable(R.drawable.baseline_account_circle_24, null).constantState)) {
            Toast.makeText(context, "Belum ada foto profil", Toast.LENGTH_SHORT).show()
            return
        }

        // Tampilkan Dialog Full Screen
        val dialog = Dialog(requireContext())
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        // Buat ImageView secara programatis agar tidak perlu layout XML baru
        val imageView = ImageView(context)
        imageView.setImageDrawable(drawable)
        imageView.layoutParams = ViewGroup.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.MATCH_PARENT
        )
        imageView.adjustViewBounds = true

        dialog.setContentView(imageView)
        // Agar dialog bisa diclose dengan klik gambar
        imageView.setOnClickListener { dialog.dismiss() }

        // Set ukuran dialog lebar & tinggi
        dialog.window?.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
        dialog.show()
    }
    // -----------------------------------

    private fun loadUserProfile() {
        val user = auth.currentUser
        if (user != null) {
            binding.etEmail.setText(user.email)
            binding.etNama.setText(user.displayName)

            val userId = user.uid
            db.collection("users").document(userId).get()
                .addOnSuccessListener { document ->
                    if (document.exists()) {
                        val phone = document.getString("phone")
                        val photoBase64 = document.getString("photoBase64")

                        binding.etPhone.setText(phone)
                        binding.etNama.setText(document.getString("nama") ?: user.displayName)

                        if (!photoBase64.isNullOrEmpty()) {
                            try {
                                val imageBytes = Base64.decode(photoBase64, Base64.DEFAULT)
                                Glide.with(this)
                                    .asBitmap()
                                    .load(imageBytes)
                                    .placeholder(R.drawable.baseline_account_circle_24)
                                    .into(binding.ivProfile)
                            } catch (e: Exception) {
                                e.printStackTrace()
                            }
                        }
                    }
                }
        }
    }

    private fun saveUserProfile() {
        binding.btnSave.isEnabled = false
        binding.btnSave.text = "Menyimpan..."

        val user = auth.currentUser
        val userId = user?.uid ?: return
        val newName = binding.etNama.text.toString().trim()
        val newPhone = binding.etPhone.text.toString().trim()

        if (newName.isEmpty()) {
            binding.etNama.error = "Nama harus diisi"
            resetButton()
            return
        }

        val userMap = hashMapOf<String, Any>(
            "nama" to newName,
            "phone" to newPhone
        )

        // LOGIKA PENYIMPANAN FOTO
        if (isPhotoRemoved) {
            // Jika user memilih hapus foto, kirim string kosong ke Firestore
            userMap["photoBase64"] = ""
        } else if (imageUri != null) {
            // Jika user memilih foto baru, konversi dan simpan
            val base64Image = uriToBase64(imageUri!!)
            if (base64Image != null) {
                userMap["photoBase64"] = base64Image
            }
        }
        // Jika tidak hapus dan tidak pilih baru (imageUri null), biarkan data lama di DB

        val profileUpdates = UserProfileChangeRequest.Builder()
            .setDisplayName(newName)
            .build()

        user.updateProfile(profileUpdates).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                db.collection("users").document(userId)
                    .set(userMap, SetOptions.merge())
                    .addOnSuccessListener {
                        Toast.makeText(context, "Profil Berhasil Diupdate!", Toast.LENGTH_SHORT).show()
                        resetButton()
                        // Reset flag setelah berhasil simpan
                        isPhotoRemoved = false
                        imageUri = null
                    }
                    .addOnFailureListener { e ->
                        Toast.makeText(context, "Gagal simpan: ${e.message}", Toast.LENGTH_SHORT).show()
                        resetButton()
                    }
            } else {
                Toast.makeText(context, "Gagal update Auth", Toast.LENGTH_SHORT).show()
                resetButton()
            }
        }
    }

    private fun uriToBase64(uri: Uri): String? {
        return try {
            val inputStream = requireContext().contentResolver.openInputStream(uri)
            val bitmap = BitmapFactory.decodeStream(inputStream)
            val scaledBitmap = Bitmap.createScaledBitmap(bitmap, 500, 500, true)
            val byteArrayOutputStream = ByteArrayOutputStream()
            scaledBitmap.compress(Bitmap.CompressFormat.JPEG, 50, byteArrayOutputStream)
            val byteArray = byteArrayOutputStream.toByteArray()
            Base64.encodeToString(byteArray, Base64.DEFAULT)
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    private fun resetButton() {
        if (_binding != null) {
            binding.btnSave.isEnabled = true
            binding.btnSave.text = "Simpan Profil"
        }
    }

    private fun logoutUser() {
        auth.signOut()
        val intent = Intent(requireContext(), LoginActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}