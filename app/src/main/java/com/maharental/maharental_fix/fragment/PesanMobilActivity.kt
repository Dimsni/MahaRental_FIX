package com.maharental.maharental_fix.fragment

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.datepicker.MaterialDatePicker
import com.maharental.maharental_fix.Kendaraan
import com.maharental.maharental_fix.Konfir_PesanMobilActivity // Pastikan import ini ada
import com.maharental.maharental_fix.databinding.ActivityPesanMobilBinding
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class PesanMobilActivity : AppCompatActivity() {

    private lateinit var binding: ActivityPesanMobilBinding
    private var kendaraan: Kendaraan? = null
    private var opsiSewa: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPesanMobilBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // 1. Ambil Data dari Intent (Halaman Sebelumnya)
        opsiSewa = intent.getStringExtra("EXTRA_OPSI_SEWA")
        kendaraan = if (Build.VERSION.SDK_INT >= 33) {
            intent.getParcelableExtra("EXTRA_KENDARAAN", Kendaraan::class.java)
        } else {
            @Suppress("DEPRECATION")
            intent.getParcelableExtra("EXTRA_KENDARAAN")
        }

        // Setup Tampilan Awal
        if (opsiSewa != null) {
            binding.tvSubtitle.text = opsiSewa?.uppercase(Locale.getDefault())
        }

        if (kendaraan != null) {
            val tipeLower = kendaraan!!.tipe.lowercase()
            val kategoriLabel = when {
                tipeLower.contains("motor") -> "Motor"
                tipeLower.contains("minibus") -> "Minibus"
                else -> "Mobil"
            }
            binding.tvBookingTitle.text = "Booking $kategoriLabel"
        }

        setupLocationDropdowns()
        setupDatePickers()
        setupButtons()
    }

    private fun setupLocationDropdowns() {
        val locations = listOf(
            "UAD KAMPUS 1", "UAD KAMPUS 2", "UAD KAMPUS 3", "UAD KAMPUS 4"
        )
        val adapter = ArrayAdapter(this, android.R.layout.simple_dropdown_item_1line, locations)
        binding.actvLokasiPengambilan.setAdapter(adapter)
        binding.actvLokasiPengembalian.setAdapter(adapter)
    }

    private fun setupDatePickers() {
        binding.etTanggalPengambilan.setOnClickListener { showRangeDatePicker() }
        binding.etTanggalPengembalian.setOnClickListener { showRangeDatePicker() }
    }

    private fun showRangeDatePicker() {
        val datePicker = MaterialDatePicker.Builder.dateRangePicker()
            .setTitleText("Pilih Tanggal Sewa")
            .build()

        datePicker.show(supportFragmentManager, "DATE_RANGE_PICKER")

        datePicker.addOnPositiveButtonClickListener { selection ->
            val sdf = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
            val tanggalAmbil = sdf.format(Date(selection.first))
            val tanggalKembali = sdf.format(Date(selection.second))

            binding.etTanggalPengambilan.setText(tanggalAmbil)
            binding.etTanggalPengembalian.setText(tanggalKembali)
        }
    }

    // --- LOGIKA TOMBOL CHECKOUT (SAMBUNG KE KONFIRMASI) ---
    private fun setupButtons() {
        binding.btnCariMobil.setOnClickListener {

            val lokasiAmbil = binding.actvLokasiPengambilan.text.toString()
            val lokasiKembali = binding.actvLokasiPengembalian.text.toString()
            val tanggalAmbil = binding.etTanggalPengambilan.text.toString()
            val tanggalKembali = binding.etTanggalPengembalian.text.toString()

            // Validasi Input
            if (lokasiAmbil.isEmpty() || lokasiKembali.isEmpty() ||
                tanggalAmbil.isEmpty() || tanggalKembali.isEmpty()) {
                Toast.makeText(this, "Lengkapi semua data terlebih dahulu", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Pindah ke Halaman Konfirmasi
            val intentKonfirmasi = Intent(this, Konfir_PesanMobilActivity::class.java)

            // Kirim Data Booking
            intentKonfirmasi.putExtra("EXTRA_KENDARAAN", kendaraan)
            intentKonfirmasi.putExtra("EXTRA_OPSI_SEWA", opsiSewa)
            intentKonfirmasi.putExtra("EXTRA_LOKASI_AMBIL", lokasiAmbil)
            intentKonfirmasi.putExtra("EXTRA_LOKASI_KEMBALI", lokasiKembali)
            intentKonfirmasi.putExtra("EXTRA_TANGGAL_AMBIL", tanggalAmbil)
            intentKonfirmasi.putExtra("EXTRA_TANGGAL_KEMBALI", tanggalKembali)

            startActivity(intentKonfirmasi)
        }
    }
}