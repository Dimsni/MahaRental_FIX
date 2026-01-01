package com.maharental.maharental_fix.katalog

import android.content.Intent // Pastikan ini diimport
import android.os.Build
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.maharental.maharental_fix.Kendaraan
import com.maharental.maharental_fix.R
import com.maharental.maharental_fix.databinding.ActivityDetailKendaraanBinding
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

                // --- LOGIKA TOMBOL PESAN SEKARANG ---
                btnPesanSekarang.setOnClickListener {
                    // PENTING: Ganti 'BookingActivity::class.java' dengan nama activity teman Anda!
                    // Contoh: val intent = Intent(this@DetailKendaraanActivity, BookingActivity::class.java)

                    /* KODE UNTUK PINDAH (Hapus tanda komentar di bawah jika class teman sudah ada)

                    val intentBooking = Intent(this@DetailKendaraanActivity, BookingActivity::class.java)
                    intentBooking.putExtra("EXTRA_KENDARAAN", kendaraan) // Kirim data kendaraan ke halaman booking
                    startActivity(intentBooking)
                    */

                    // Sementara pakai Toast dulu agar tidak error
                    Toast.makeText(this@DetailKendaraanActivity, "Menuju halaman Booking...", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
}