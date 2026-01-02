package com.maharental.maharental_fix.katalog

import android.content.Intent
import android.os.Build
import android.os.Bundle
import androidx.appcompat.app.AlertDialog // Import AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.maharental.maharental_fix.Kendaraan
import com.maharental.maharental_fix.R
import com.maharental.maharental_fix.databinding.ActivityDetailKendaraanBinding
import com.maharental.maharental_fix.fragment.PesanMobilActivity
import java.text.NumberFormat
import java.util.Locale

class DetailKendaraanActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDetailKendaraanBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailKendaraanBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Tombol Back
        binding.btnBack.setOnClickListener {
            finish()
        }

        // Ambil data kendaraan
        val kendaraan = if (Build.VERSION.SDK_INT >= 33) {
            intent.getParcelableExtra("EXTRA_KENDARAAN", Kendaraan::class.java)
        } else {
            @Suppress("DEPRECATION")
            intent.getParcelableExtra("EXTRA_KENDARAAN")
        }

        // Tampilkan Data
        if (kendaraan != null) {
            binding.apply {
                tvDetailNama.text = kendaraan.nama
                tvDetailTipe.text = kendaraan.tipe

                val formatRupiah = NumberFormat.getCurrencyInstance(Locale("id", "ID"))
                tvDetailHarga.text = "${formatRupiah.format(kendaraan.harga)} / hari"

                Glide.with(this@DetailKendaraanActivity)
                    .load(kendaraan.gambar)
                    .placeholder(R.drawable.ic_launcher_background)
                    .into(ivDetailGambar)

                if (kendaraan.deskripsi.isNotEmpty()) {
                    tvDetailDeskripsi.text = kendaraan.deskripsi
                } else {
                    tvDetailDeskripsi.text = "Tidak ada deskripsi untuk kendaraan ini."
                }

                // --- LOGIKA TOMBOL PESAN SEKARANG (MODIFIKASI) ---
                btnPesanSekarang.setOnClickListener {
                    cekDanPesanKendaraan(kendaraan)
                }
            }
        }
    }

    // Fungsi untuk mengecek tipe kendaraan dan menampilkan dialog jika perlu
    private fun cekDanPesanKendaraan(kendaraan: Kendaraan) {
        // Cek apakah tipe mengandung kata "Motor" (Huruf besar/kecil tidak masalah)
        // Jika data kategori ada di field lain (misal: kendaraan.kategori), ganti 'kendaraan.tipe' dengan 'kendaraan.kategori'
        if (kendaraan.tipe.contains("Motor", ignoreCase = true)) {
            // Jika Motor, langsung pindah
            bukaHalamanPesan(kendaraan, "Lepas Kunci") // Default motor biasanya lepas kunci
        } else {
            // Jika Mobil (Bukan Motor), tampilkan pilihan
            tampilkanPilihanSewa(kendaraan)
        }
    }

    private fun tampilkanPilihanSewa(kendaraan: Kendaraan) {
        val opsiSewa = arrayOf("Lepas Kunci", "Dengan Driver")

        val builder = AlertDialog.Builder(this)
        builder.setTitle("Pilih Opsi Sewa")
        builder.setItems(opsiSewa) { dialog, which ->
            val pilihan = opsiSewa[which]
            bukaHalamanPesan(kendaraan, pilihan)
        }
        builder.show()
    }

    private fun bukaHalamanPesan(kendaraan: Kendaraan, opsi: String) {
        val intentBooking = Intent(this@DetailKendaraanActivity, PesanMobilActivity::class.java)
        intentBooking.putExtra("EXTRA_KENDARAAN", kendaraan)
        intentBooking.putExtra("EXTRA_OPSI_SEWA", opsi) // Mengirim pilihan ke halaman pesan
        startActivity(intentBooking)
    }
}