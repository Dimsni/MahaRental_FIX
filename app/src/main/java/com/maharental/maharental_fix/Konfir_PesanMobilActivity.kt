package com.maharental.maharental_fix

import android.os.Build
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.maharental.maharental_fix.databinding.ActivityKonfirPesanMobilBinding
import java.text.NumberFormat
import java.util.Locale

class Konfir_PesanMobilActivity : AppCompatActivity() {

    private lateinit var binding: ActivityKonfirPesanMobilBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityKonfirPesanMobilBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Tombol Kembali
        // Asumsi ada tombol back di XML, jika tidak ada bisa dihapus baris ini
        // binding.btnBack.setOnClickListener { finish() }

        // --- AMBIL DATA DARI HALAMAN BOOKING ---
        val kendaraan = if (Build.VERSION.SDK_INT >= 33) {
            intent.getParcelableExtra("EXTRA_KENDARAAN", Kendaraan::class.java)
        } else {
            @Suppress("DEPRECATION")
            intent.getParcelableExtra("EXTRA_KENDARAAN")
        }

        val opsiSewa = intent.getStringExtra("EXTRA_OPSI_SEWA")
        val lokasiAmbil = intent.getStringExtra("EXTRA_LOKASI_AMBIL")
        val lokasiKembali = intent.getStringExtra("EXTRA_LOKASI_KEMBALI")
        val tanggalAmbil = intent.getStringExtra("EXTRA_TANGGAL_AMBIL")
        val tanggalKembali = intent.getStringExtra("EXTRA_TANGGAL_KEMBALI")

        // --- TAMPILKAN DATA (CONTOH) ---
        // Sesuaikan ID di bawah ini (tvNamaMobil, tvHarga, dll) dengan ID di XML Anda
        if (kendaraan != null) {
            // Contoh menampilkan nama mobil jika ada TextView dengan id tvNamaKendaraan
            // binding.tvNamaKendaraan.text = kendaraan.nama

            // Format Harga
            val formatRupiah = NumberFormat.getCurrencyInstance(Locale("id", "ID"))
            // binding.tvTotalHarga.text = formatRupiah.format(kendaraan.harga)
        }

        // Tampilkan info sewa lainnya
        // binding.tvTanggalSewa.text = "$tanggalAmbil s/d $tanggalKembali"
        // binding.tvLokasi.text = lokasiAmbil

        // --- LOGIKA TOMBOL KONFIRMASI / BAYAR ---
        // binding.btnKonfirmasi.setOnClickListener {
        // Lanjut ke pembayaran atau simpan ke Firebase
        // }
    }
}