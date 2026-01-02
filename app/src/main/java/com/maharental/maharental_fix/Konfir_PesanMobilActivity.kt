package com.maharental.maharental_fix

import android.app.DatePickerDialog
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.maharental.maharental_fix.databinding.ActivityKonfirPesanMobilBinding
import java.util.*

class Konfir_PesanMobilActivity : AppCompatActivity() {

    private lateinit var binding: ActivityKonfirPesanMobilBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityKonfirPesanMobilBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // 1. Klik pada "Metode Pembayaran" langsung ke Checkout
        binding.tvPaymentMethod.setOnClickListener {
            val intent = Intent(this, CheckoutActivity::class.java)
            startActivity(intent)
        }

        // 2. Tombol Bayar Sekarang juga mengarah ke Checkout
        binding.btnPayNow.setOnClickListener {
            val intent = Intent(this, CheckoutActivity::class.java)
            startActivity(intent)
        }

        // Fungsi DatePicker untuk input tanggal
        binding.etPickupDate.setOnClickListener { showDatePicker(binding.etPickupDate) }
        binding.etReturnDate.setOnClickListener { showDatePicker(binding.etReturnDate) }
    }

    private fun showDatePicker(editText: android.widget.EditText) {
        val c = Calendar.getInstance()
        val datePickerDialog = DatePickerDialog(this, { _, year, month, day ->
            val selectedDate = "$day/${month + 1}/$year"
            editText.setText(selectedDate)
        }, c.get(Calendar.YEAR), c.get(Calendar.MONTH), c.get(Calendar.DAY_OF_MONTH))

        datePickerDialog.show()
    }
}