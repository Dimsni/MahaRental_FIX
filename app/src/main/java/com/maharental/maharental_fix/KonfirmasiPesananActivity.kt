package com.maharental.maharental_fix

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.util.Base64
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.maharental.maharental_fix.databinding.ActivityKonfirmasiPesananBinding
import java.io.ByteArrayOutputStream

class KonfirmasiPesananActivity : AppCompatActivity() {

    private lateinit var binding: ActivityKonfirmasiPesananBinding

    // Inisialisasi Firebase
    private val auth = FirebaseAuth.getInstance()
    private val db = FirebaseFirestore.getInstance()

    // Variabel untuk menyimpan URI gambar yang dipilih
    private var imageUri: Uri? = null

    // Launcher untuk membuka galeri
    private val getImageAction = registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
        uri?.let {
            imageUri = it
            binding.ivKtp.setImageURI(it) // Menampilkan preview
            binding.tvPlaceholderKtp.alpha = 0f // Sembunyikan teks placeholder jika ada
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityKonfirmasiPesananBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Klik area KTP untuk buka galeri
        binding.ivKtp.setOnClickListener {
            getImageAction.launch("image/*")
        }

        binding.btnSubmit.setOnClickListener {
            validateAndSubmit()
        }
    }

    private fun validateAndSubmit() {
        val nik = binding.etNik.text.toString().trim()
        val email = binding.etEmail.text.toString().trim()
        val phone = binding.etNoHp.text.toString().trim()

        // Validasi Input
        if (nik.isEmpty() || email.isEmpty() || phone.isEmpty()) {
            Toast.makeText(this, "Harap lengkapi NIK, Email, dan No HP", Toast.LENGTH_SHORT).show()
            return
        }

        if (imageUri == null) {
            Toast.makeText(this, "Harap upload foto KTP", Toast.LENGTH_SHORT).show()
            return
        }

        if (!binding.cbAgreement.isChecked) {
            Toast.makeText(this, "Silakan centang persetujuan terlebih dahulu", Toast.LENGTH_SHORT).show()
            return
        }

        // Tampilkan loading (opsional)
        binding.btnSubmit.isEnabled = false
        binding.btnSubmit.text = "Memproses..."

        // 1. Konversi Gambar ke Base64
        val ktpBase64 = uriToBase64(imageUri!!)

        // 2. Siapkan Data untuk Firebase
        val userId = auth.currentUser?.uid ?: "guest"
        val bookingData = hashMapOf(
            "userId" to userId,
            "nik" to nik,
            "email" to email,
            "phone" to phone,
            "ktp_image_base64" to (ktpBase64 ?: ""),
            "timestamp" to System.currentTimeMillis()
        )

        // 3. Simpan ke Firestore (Koleksi 'verifikasi_ktp' atau 'users')
        // Disini kita simpan ke koleksi 'verifikasi_ktp' agar data booking rapi
        db.collection("verifikasi_ktp").document(userId)
            .set(bookingData)
            .addOnSuccessListener {
                Toast.makeText(this, "Data KTP Berhasil Diverifikasi", Toast.LENGTH_SHORT).show()

                // Lanjut ke halaman berikutnya dengan membawa data intent sebelumnya
                val intent = Intent(this, Konfir_PesanMobilActivity::class.java)
                intent.putExtras(this.intent)
                startActivity(intent)
                finish()
            }
            .addOnFailureListener { e ->
                binding.btnSubmit.isEnabled = true
                binding.btnSubmit.text = "Submit Pesanan"
                Toast.makeText(this, "Gagal simpan data: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }

    // Fungsi konversi URI ke Base64 (Sama dengan ProfileFragment agar konsisten)
    private fun uriToBase64(uri: Uri): String? {
        return try {
            val inputStream = contentResolver.openInputStream(uri)
            val bitmap = BitmapFactory.decodeStream(inputStream)

            // Kompresi agar tidak terlalu berat saat masuk ke Firestore (maks 1MB per dokumen)
            val scaledBitmap = Bitmap.createScaledBitmap(bitmap, 600, 400, true)
            val byteArrayOutputStream = ByteArrayOutputStream()
            scaledBitmap.compress(Bitmap.CompressFormat.JPEG, 60, byteArrayOutputStream)
            val byteArray = byteArrayOutputStream.toByteArray()
            Base64.encodeToString(byteArray, Base64.DEFAULT)
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
}