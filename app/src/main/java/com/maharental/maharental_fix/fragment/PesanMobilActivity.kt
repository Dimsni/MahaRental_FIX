package com.maharental.maharental_fix.fragment

import android.os.Build
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.datepicker.MaterialDatePicker
import com.maharental.maharental_fix.Kendaraan
import com.maharental.maharental_fix.databinding.ActivityPesanMobilBinding
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class PesanMobilActivity : AppCompatActivity() {

    private lateinit var binding: ActivityPesanMobilBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPesanMobilBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // 1. Logika Judul Atas (Lepas Kunci / Driver)
        val opsiSewa = intent.getStringExtra("EXTRA_OPSI_SEWA")
        if (opsiSewa != null) {
            binding.tvSubtitle.text = opsiSewa.uppercase(Locale.getDefault())
        }

        // 2. Logika Judul Booking (Motor / Minibus / Mobil)
        val kendaraan = if (Build.VERSION.SDK_INT >= 33) {
            intent.getParcelableExtra("EXTRA_KENDARAAN", Kendaraan::class.java)
        } else {
            @Suppress("DEPRECATION")
            intent.getParcelableExtra("EXTRA_KENDARAAN")
        }

        if (kendaraan != null) {
            val tipeLower = kendaraan.tipe.lowercase()
            val kategoriLabel = when {
                tipeLower.contains("motor") -> "Motor"
                tipeLower.contains("minibus") -> "Minibus"
                else -> "Mobil" // Default jika bukan motor/minibus
            }
            // Ubah teks Booking sesuai kategori
            binding.tvBookingTitle.text = "Booking $kategoriLabel"
        }

        setupLocationDropdowns()
        setupDatePickers()
        setupButtons()
    }

    // ===================== LOKASI =====================
    private fun setupLocationDropdowns() {
        val locations = listOf(
            "UAD KAMPUS 1",
            "UAD KAMPUS 2",
            "UAD KAMPUS 3",
            "UAD KAMPUS 4"
        )

        val adapter = ArrayAdapter(
            this,
            android.R.layout.simple_dropdown_item_1line,
            locations
        )

        binding.actvLokasiPengambilan.setAdapter(adapter)
        binding.actvLokasiPengembalian.setAdapter(adapter)
    }

    // ===================== TANGGAL (KALENDER MODERN) =====================
    private fun setupDatePickers() {

        binding.etTanggalPengambilan.setOnClickListener {
            showRangeDatePicker()
        }

        binding.etTanggalPengembalian.setOnClickListener {
            showRangeDatePicker()
        }
    }

    /**
     * Kalender seperti gambar:
     * - Pilih tanggal awal & akhir
     * - 1 popup
     * - Material Design
     */
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

    // ===================== BUTTON =====================
    private fun setupButtons() {
        binding.btnCariMobil.setOnClickListener {

            val lokasiAmbil = binding.actvLokasiPengambilan.text.toString()
            val lokasiKembali = binding.actvLokasiPengembalian.text.toString()
            val tanggalAmbil = binding.etTanggalPengambilan.text.toString()
            val tanggalKembali = binding.etTanggalPengembalian.text.toString()

            if (lokasiAmbil.isEmpty() ||
                lokasiKembali.isEmpty() ||
                tanggalAmbil.isEmpty() ||
                tanggalKembali.isEmpty()
            ) {
                Toast.makeText(
                    this,
                    "Lengkapi semua data terlebih dahulu",
                    Toast.LENGTH_SHORT
                ).show()
                return@setOnClickListener
            }

            // Pesan konfirmasi Checkout
            Toast.makeText(
                this,
                "Checkout berhasil! Menunggu pembayaran...",
                Toast.LENGTH_SHORT
            ).show()

            // TODO:
            // Lanjut ke proses pembayaran atau simpan ke Firestore
        }
    }
}