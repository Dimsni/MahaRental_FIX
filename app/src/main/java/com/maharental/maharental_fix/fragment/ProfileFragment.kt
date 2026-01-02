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
    private var currentPhotoBase64: String? = null // Menyimpan string gambar saat ini dari database
    private var isPhotoRemoved: Boolean = false // Penanda apakah foto dihapus user

    // Launcher Galeri
    private val pickImage = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val data: Intent? = result.data
            imageUri = data?.data
            binding.ivProfile.setImageURI(imageUri) // Preview gambar
            isPhotoRemoved = false // Reset status hapus jika user memilih foto baru
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

        // Inisialisasi Firebase
        auth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()

        loadUserProfile()

        // Ubah Listener agar memunculkan Dialog Pilihan, bukan langsung galeri
        binding.cardProfile.setOnClickListener { showImageOptions() }
        binding.ivProfile.setOnClickListener { showImageOptions() }

        binding.btnSave.setOnClickListener { saveUserProfile() }
        binding.btnLogout.setOnClickListener { logoutUser() }
    }

    // 1. Fungsi Menampilkan Pilihan Menu
    private fun showImageOptions() {
        val options = arrayOf("Lihat Foto", "Ubah Foto", "Hapus Foto", "Keluar")

        val builder = AlertDialog.Builder(requireContext())
        builder.setTitle("Foto Profil")
        builder.setItems(options) { dialog, which ->
            when (which) {
                0 -> showFullImage() // Lihat Foto
                1 -> openGallery()   // Ubah Foto
                2 -> deletePhoto()   // Hapus Foto
                3 -> dialog.dismiss() // Keluar
            }
        }
        builder.show()
    }

    // 2. Fungsi Melihat Foto Full Screen
    private fun showFullImage() {
        val dialog = Dialog(requireContext())
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setContentView(R.layout.dialog_customer_service) // Kita bisa reuse layout dialog kosong atau buat ImageView dinamis

        // Membuat ImageView secara programatis untuk dialog ini agar simpel
        val imageView = ImageView(requireContext())
        imageView.layoutParams = ViewGroup.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.MATCH_PARENT
        )
        imageView.adjustViewBounds = true
        imageView.scaleType = ImageView.ScaleType.FIT_CENTER

        // Logika load gambar ke dialog
        if (imageUri != null) {
            imageView.setImageURI(imageUri)
        } else if (!currentPhotoBase64.isNullOrEmpty() && !isPhotoRemoved) {
            try {
                val imageBytes = Base64.decode(currentPhotoBase64, Base64.DEFAULT)
                Glide.with(this).load(imageBytes).into(imageView)
            } catch (e: Exception) {
                imageView.setImageResource(R.drawable.baseline_account_circle_24)
            }
        } else {
            imageView.setImageResource(R.drawable.baseline_account_circle_24)
            Toast.makeText(context, "Tidak ada foto profil", Toast.LENGTH_SHORT).show()
        }

        dialog.setContentView(imageView)
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.window?.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, 800) // Tinggi dialog
        dialog.show()
    }

    private fun openGallery() {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        pickImage.launch(intent)
    }

    // 3. Fungsi Menghapus Foto (Secara Visual dulu)
    private fun deletePhoto() {
        binding.ivProfile.setImageResource(R.drawable.baseline_account_circle_24)
        imageUri = null
        isPhotoRemoved = true
        Toast.makeText(context, "Foto dihapus. Klik 'Simpan Profil' untuk menerapkan.", Toast.LENGTH_SHORT).show()
    }

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
                        currentPhotoBase64 = document.getString("photoBase64") // Simpan ke variabel global

                        binding.etPhone.setText(phone)
                        binding.etNama.setText(document.getString("nama") ?: user.displayName)

                        if (!currentPhotoBase64.isNullOrEmpty()) {
                            try {
                                val imageBytes = Base64.decode(currentPhotoBase64, Base64.DEFAULT)
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
                .addOnFailureListener {
                    Toast.makeText(context, "Gagal memuat data", Toast.LENGTH_SHORT).show()
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

        val profileUpdates = UserProfileChangeRequest.Builder()
            .setDisplayName(newName)
            .build()

        user.updateProfile(profileUpdates).addOnCompleteListener { task ->
            if (task.isSuccessful) {

                // LOGIKA UPDATE FOTO
                if (isPhotoRemoved) {
                    // Jika user memilih hapus foto, set field photoBase64 menjadi string kosong
                    userMap["photoBase64"] = ""
                    currentPhotoBase64 = null
                } else if (imageUri != null) {
                    // Jika user memilih foto baru
                    val base64Image = uriToBase64(imageUri!!)
                    if (base64Image != null) {
                        userMap["photoBase64"] = base64Image
                        currentPhotoBase64 = base64Image
                    }
                }

                db.collection("users").document(userId)
                    .set(userMap, SetOptions.merge())
                    .addOnSuccessListener {
                        Toast.makeText(context, "Profil Berhasil Diupdate!", Toast.LENGTH_SHORT).show()
                        resetButton()
                        isPhotoRemoved = false // Reset flag
                        imageUri = null        // Reset uri agar tidak disimpan ulang jika tombol ditekan lagi
                    }
                    .addOnFailureListener { e ->
                        Toast.makeText(context, "Gagal simpan ke database: ${e.message}", Toast.LENGTH_SHORT).show()
                        resetButton()
                    }
            } else {
                Toast.makeText(context, "Gagal update profil Auth", Toast.LENGTH_SHORT).show()
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