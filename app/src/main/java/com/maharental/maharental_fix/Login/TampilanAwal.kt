package com.maharental.maharental_fix.Login

import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.button.MaterialButton
import com.maharental.maharental_fix.R


class TampilanAwal : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_tampilan_awal)

        findViewById<MaterialButton>(R.id.btnMulai).setOnClickListener {
            startActivity(Intent(this, LoginActivity::class.java))
            finish() // supaya back dari Login tidak balik ke tampilanAwal
        }
    }
}