package com.maharental.maharental_fix

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.widget.RadioButton
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.maharental.maharental_fix.databinding.ActivityCheckoutBinding

class CheckoutActivity : AppCompatActivity() {

    private lateinit var binding: ActivityCheckoutBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCheckoutBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnBack.setOnClickListener { finish() }

        binding.btnConfirm.setOnClickListener {
            val selectedId = binding.rgPaymentMethods.checkedRadioButtonId

            if (selectedId != -1) {
                val radioButton = findViewById<RadioButton>(selectedId)
                val paymentMethodName = radioButton.text.toString()

                val resultIntent = Intent()
                resultIntent.putExtra(EXTRA_SELECTED_PAYMENT, paymentMethodName)
                setResult(Activity.RESULT_OK, resultIntent)
                finish()
            } else {
                Toast.makeText(this, "Silakan pilih metode pembayaran", Toast.LENGTH_SHORT).show()
            }
        }
    }

    companion object {
        const val EXTRA_SELECTED_PAYMENT = "SELECTED_PAYMENT"
    }
}
