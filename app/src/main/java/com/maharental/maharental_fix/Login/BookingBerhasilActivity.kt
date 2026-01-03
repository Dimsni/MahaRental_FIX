package com.maharental.maharental_fix

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.maharental.maharental_fix.databinding.ActivityBookingBerhasilBinding

class BookingBerhasilActivity : AppCompatActivity() {

    private lateinit var binding: ActivityBookingBerhasilBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityBookingBerhasilBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnReturnHistory.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)

            // Mengirim instruksi ke MainActivity
            intent.putExtra("GO_TO_HISTORY", true)

            // Menghapus tumpukan activity agar saat di-back aplikasi langsung keluar
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
            finish()
        }
    }
}