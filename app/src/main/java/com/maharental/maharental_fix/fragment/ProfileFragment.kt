package com.maharental.maharental_fix.fragment

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Base64
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
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

    // Launcher Galeri
    private val pickImage = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val data: Intent? = result.data
            imageUri = data?.data
            binding.ivProfile.setImageURI(imageUri) // Preview gambar
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

        // Inisialisasi Firebase (Tanpa Storage)
        auth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()

        loadUserProfile()

        // Event Klik
        binding.cardProfile.setOnClickListener { openGallery() }
        binding.ivProfile.setOnClickListener { openGallery() }
        binding.btnSave.setOnClickListener { saveUserProfile() }
        binding.btnLogout.setOnClickListener { logoutUser() }
    }

    private fun openGallery() {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        pickImage.launch(intent)
    }

    private fun loadUserProfile() {
        val user = auth.currentUser
        if (user != null) {
            binding.etEmail.setText(user.email)
            binding.etNama.setText(user.displayName)

            val userId = user.uid
            // Ambil data dari Firestore
            db.collection("users").document(userId).get()
                .addOnSuccessListener { document ->
                    if (document.exists()) {
                        val phone = document.getString("phone")
                        val photoBase64 = document.getString("photoBase64") // Ambil string gambar

                        binding.etPhone.setText(phone)
                        binding.etNama.setText(document.getString("name") ?: user.displayName)

                        // Jika ada data gambar Base64, decode dan tampilkan dengan Glide
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

        // Siapkan Map data untuk Firestore
        val userMap = hashMapOf<String, Any>(
            "name" to newName,
            "phone" to newPhone
        )

        // 1. Update Auth (Nama Display)
        val profileUpdates = UserProfileChangeRequest.Builder()
            .setDisplayName(newName)
            .build()

        user.updateProfile(profileUpdates).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                // 2. Cek apakah ada gambar baru yang dipilih
                if (imageUri != null) {
                    // Konversi Gambar ke Base64 String
                    val base64Image = uriToBase64(imageUri!!)
                    if (base64Image != null) {
                        userMap["photoBase64"] = base64Image
                    }
                }

                // 3. Simpan ke Firestore
                db.collection("users").document(userId)
                    .set(userMap, SetOptions.merge()) // Gunakan merge agar data lain tidak hilang
                    .addOnSuccessListener {
                        Toast.makeText(context, "Profil Berhasil Diupdate!", Toast.LENGTH_SHORT).show()
                        resetButton()
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

    // Fungsi Pembantu: Ubah Uri -> Bitmap -> Kompres -> Base64 String
    private fun uriToBase64(uri: Uri): String? {
        return try {
            val inputStream = requireContext().contentResolver.openInputStream(uri)
            val bitmap = BitmapFactory.decodeStream(inputStream)

            // Kompres gambar agar tidak terlalu besar untuk Firestore (Max 1MB per dokumen)
            // Resize dulu jika terlalu besar (opsional tapi disarankan)
            val scaledBitmap = Bitmap.createScaledBitmap(bitmap, 500, 500, true)

            val byteArrayOutputStream = ByteArrayOutputStream()
            // Kompres ke JPEG dengan kualitas 50%
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