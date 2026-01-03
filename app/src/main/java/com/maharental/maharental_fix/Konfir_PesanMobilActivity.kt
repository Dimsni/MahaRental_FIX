package com.maharental.maharental_fix

import android.app.Activity
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.maharental.maharental_fix.databinding.ActivityKonfirPesanMobilBinding

import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.Locale
import java.util.concurrent.TimeUnit

class Konfir_PesanMobilActivity : AppCompatActivity() {

    private lateinit var binding: ActivityKonfirPesanMobilBinding

    // 1. Definisikan Launcher untuk menangkap hasil dari CheckoutActivity
    private val paymentLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val paymentMethod = result.data?.getStringExtra("SELECTED_PAYMENT")
            // Tampilkan metode pembayaran yang dipilih di samping teks
            binding.tvPaymentMethod.text = paymentMethod
            binding.tvPaymentMethod.setTextColor(resources.getColor(android.R.color.black))
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityKonfirPesanMobilBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnBack.setOnClickListener {
            finish()
        }

        // Ambil Data dari Intent
        val kendaraan = if (Build.VERSION.SDK_INT >= 33) {
            intent.getParcelableExtra("EXTRA_KENDARAAN", Kendaraan::class.java)
        } else {
            @Suppress("DEPRECATION")
            intent.getParcelableExtra("EXTRA_KENDARAAN")
        }

        val lokasiAmbil = intent.getStringExtra("EXTRA_LOKASI_AMBIL") ?: "-"
        val lokasiKembali = intent.getStringExtra("EXTRA_LOKASI_KEMBALI") ?: "-"
        val tanggalAmbil = intent.getStringExtra("EXTRA_TANGGAL_AMBIL") ?: ""
        val tanggalKembali = intent.getStringExtra("EXTRA_TANGGAL_KEMBALI") ?: ""

        // Tampilkan Data Tanggal & Lokasi
        binding.etPickupDate.setText(tanggalAmbil)
        binding.etReturnDate.setText(tanggalKembali)
        binding.tvLokasiSummary.text = "$lokasiAmbil ke $lokasiKembali"

        if (kendaraan != null) {
            binding.tvCarName.text = kendaraan.nama

            // Tampilkan Gambar menggunakan Glide (Firestore URL)
            Glide.with(this)
                .load(kendaraan.gambar)
                .placeholder(R.drawable.logoo)
                .error(R.drawable.logoo)
                .into(binding.ivCar)

            val formatRupiah = NumberFormat.getCurrencyInstance(Locale("id", "ID"))
            binding.tvCarPrice.text = "${formatRupiah.format(kendaraan.harga)} / Hari"

            val durasi = hitungSelisihHari(tanggalAmbil, tanggalKembali)
            val totalBiaya = durasi * kendaraan.harga

            binding.tvPriceDetailText.text = "${formatRupiah.format(kendaraan.harga)} x $durasi hari"
            binding.tvPriceDetailAmount.text = formatRupiah.format(totalBiaya)
            binding.tvTotalAmountValue.text = formatRupiah.format(totalBiaya)
        }

        // 2. Klik Pilih Pembayaran -> Buka Checkout dengan Launcher
        binding.tvPaymentMethod.setOnClickListener {
            val intent = Intent(this, CheckoutActivity::class.java)
            paymentLauncher.launch(intent)
        }

        // 3. Klik Bayar Sekarang -> Langsung ke BookingBerhasilActivity
        binding.btnPayNow.setOnClickListener {
            val selectedMethod = binding.tvPaymentMethod.text.toString()
            if (selectedMethod == "Metode Pembayaran" || selectedMethod.isEmpty()) {
                Toast.makeText(this, "Harap pilih metode pembayaran", Toast.LENGTH_SHORT).show()
            } else {
                val intentSuccess = Intent(this, BookingBerhasilActivity::class.java)
                intentSuccess.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                startActivity(intentSuccess)
            }
        }
    }

    private fun hitungSelisihHari(tglAwal: String, tglAkhir: String): Long {
        return try {
            val sdf = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
            val date1 = sdf.parse(tglAwal)
            val date2 = sdf.parse(tglAkhir)
            val diff = date2!!.time - date1!!.time
            val days = TimeUnit.MILLISECONDS.toDays(diff)
            if (days <= 0) 1 else days
        } catch (e: Exception) {
            1
        }
    }
}