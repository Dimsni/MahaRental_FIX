package com.maharental.maharental_fix

import android.content.Intent
import android.os.Bundle
import android.widget.ArrayAdapter
import androidx.appcompat.app.AppCompatActivity
import com.maharental.maharental_fix.databinding.ActivityCheckoutBinding

class CheckoutActivity : AppCompatActivity() {

    private lateinit var binding: ActivityCheckoutBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCheckoutBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Mengisi Spinner dengan contoh metode pembayaran
        val methods = arrayOf("Credit Card", "Bank Transfer", "E-Wallet")
        binding.spinnerPaymentMethod.adapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, methods)

        binding.btnConfirm.setOnClickListener {
            val intent = Intent(this, BookingBerhasilActivity::class.java)
            // FLAG_ACTIVITY_CLEAR_TASK menghapus antrian activity agar user tidak bisa 'back' ke form bayar
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
        }
    }
}