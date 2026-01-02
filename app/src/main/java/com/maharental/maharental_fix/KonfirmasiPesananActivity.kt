package com.maharental.maharental_fix

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.maharental.maharental_fix.databinding.ActivityKonfirmasiPesananBinding

class KonfirmasiPesananActivity : AppCompatActivity() {

    private lateinit var binding: ActivityKonfirmasiPesananBinding

    // Launcher untuk membuka galeri
    private val getImageAction = registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
        uri?.let {
            binding.ivKtp.setImageURI(it) // Menampilkan gambar yang dipilih ke ImageView
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityKonfirmasiPesananBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Klik gambar KTP untuk buka galeri
        binding.ivKtp.setOnClickListener {
            getImageAction.launch("image/*")
        }

        binding.btnSubmit.setOnClickListener {
            // Validasi sederhana: pastikan checkbox sudah dicentang
            if (binding.cbAgreement.isChecked) {
                val intent = Intent(this, Konfir_PesanMobilActivity::class.java)
                startActivity(intent)
            } else {
                Toast.makeText(this, "Silakan centang persetujuan terlebih dahulu", Toast.LENGTH_SHORT).show()
            }
        }

        binding.btnVerify.setOnClickListener {
            Toast.makeText(this, "Nomor HP Sedang Diverifikasi...", Toast.LENGTH_SHORT).show()
        }
    }
}