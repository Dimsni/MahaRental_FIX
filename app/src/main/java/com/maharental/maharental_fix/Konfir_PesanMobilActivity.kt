package com.maharental.maharental_fix

import android.app.Activity
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.maharental.maharental_fix.databinding.ActivityKonfirPesanMobilBinding
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.Locale
import java.util.concurrent.TimeUnit

class Konfir_PesanMobilActivity : AppCompatActivity() {

    private lateinit var binding: ActivityKonfirPesanMobilBinding
    private var kendaraanDipilih: Kendaraan? = null

    private var tanggalAmbil: String = ""
    private var tanggalKembali: String = ""
    private var lokasiAmbil: String = "-"
    private var lokasiKembali: String = "-"

    private val paymentLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                val paymentMethod =
                    result.data?.getStringExtra(CheckoutActivity.EXTRA_SELECTED_PAYMENT)

                binding.tvPaymentMethod.text = paymentMethod
                @Suppress("DEPRECATION")
                binding.tvPaymentMethod.setTextColor(resources.getColor(android.R.color.black))
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityKonfirPesanMobilBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnBack.setOnClickListener { finish() }

        // Ambil kendaraan dari Intent
        kendaraanDipilih = if (Build.VERSION.SDK_INT >= 33) {
            intent.getParcelableExtra("EXTRA_KENDARAAN", Kendaraan::class.java)
        } else {
            @Suppress("DEPRECATION")
            intent.getParcelableExtra("EXTRA_KENDARAAN")
        }

        lokasiAmbil = intent.getStringExtra("EXTRA_LOKASI_AMBIL") ?: "-"
        lokasiKembali = intent.getStringExtra("EXTRA_LOKASI_KEMBALI") ?: "-"
        tanggalAmbil = intent.getStringExtra("EXTRA_TANGGAL_AMBIL") ?: ""
        tanggalKembali = intent.getStringExtra("EXTRA_TANGGAL_KEMBALI") ?: ""

        binding.etPickupDate.setText(tanggalAmbil)
        binding.etReturnDate.setText(tanggalKembali)
        binding.tvLokasiSummary.text = "$lokasiAmbil ke $lokasiKembali"

        val formatRupiah = NumberFormat.getCurrencyInstance(Locale("id", "ID"))

        kendaraanDipilih?.let { kendaraan ->
            binding.tvCarName.text = kendaraan.nama

            Glide.with(this)
                .load(kendaraan.gambar)
                .placeholder(R.drawable.logoo)
                .error(R.drawable.logoo)
                .into(binding.ivCar)

            binding.tvCarPrice.text = "${formatRupiah.format(kendaraan.harga)} / Hari"

            val durasi = hitungSelisihHari(tanggalAmbil, tanggalKembali)
            val totalBiaya = durasi * kendaraan.harga

            binding.tvPriceDetailText.text =
                "${formatRupiah.format(kendaraan.harga)} x $durasi hari"
            binding.tvPriceDetailAmount.text = formatRupiah.format(totalBiaya)
            binding.tvTotalAmountValue.text = formatRupiah.format(totalBiaya)
        }

        // Pilih Pembayaran
        binding.tvPaymentMethod.setOnClickListener {
            val i = Intent(this, CheckoutActivity::class.java)
            paymentLauncher.launch(i)
        }

        // Bayar Sekarang -> simpan history PER USER ke Firestore -> masuk halaman berhasil
        binding.btnPayNow.setOnClickListener {
            val selectedMethod = binding.tvPaymentMethod.text.toString()

            if (selectedMethod == "Metode Pembayaran" || selectedMethod.isEmpty()) {
                Toast.makeText(this, "Harap pilih metode pembayaran", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val uid = FirebaseAuth.getInstance().currentUser?.uid
            if (uid == null) {
                Toast.makeText(this, "Silakan login dulu", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val kendaraan = kendaraanDipilih
            if (kendaraan == null) {
                Toast.makeText(this, "Data kendaraan tidak ditemukan", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val durasi = hitungSelisihHari(tanggalAmbil, tanggalKembali)
            val totalBiaya = durasi * kendaraan.harga

            // simpan ke Firestore: history/{uid}/items/{autoId}
            val dataHistory = hashMapOf(
                "nama" to kendaraan.nama,
                "tipe" to kendaraan.tipe,
                "harga" to totalBiaya,              // TOTAL (bukan per hari)
                "gambar" to kendaraan.gambar,
                "deskripsi" to kendaraan.deskripsi,
                "jumlahUnit" to kendaraan.jumlahUnit,
                "tanggalAmbil" to tanggalAmbil,
                "tanggalKembali" to tanggalKembali,
                "lokasiAmbil" to lokasiAmbil,
                "lokasiKembali" to lokasiKembali,
                "durasi" to durasi,
                "paymentMethod" to selectedMethod,
                "timestamp" to System.currentTimeMillis()
            )

            FirebaseFirestore.getInstance()
                .collection("history")
                .document(uid)
                .collection("items")
                .add(dataHistory)
                .addOnSuccessListener {
                    val intentSuccess = Intent(this, BookingBerhasilActivity::class.java)
                    intentSuccess.flags =
                        Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                    startActivity(intentSuccess)
                }
                .addOnFailureListener { e ->
                    Toast.makeText(this, "Gagal simpan history: ${e.message}", Toast.LENGTH_SHORT)
                        .show()
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
