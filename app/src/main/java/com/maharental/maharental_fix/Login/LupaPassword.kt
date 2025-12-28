package com.maharental.maharental_fix.Login

import android.os.Bundle
import android.util.Patterns
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import com.maharental.maharental_fix.R

class LupaPassword : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_lupa_password)

        val etEmail = findViewById<TextInputEditText>(R.id.et_email_lupa)
        val btnReset = findViewById<MaterialButton>(R.id.btn_reset)
        val tvBack = findViewById<TextView>(R.id.tv_back_login)

        btnReset.setOnClickListener {
            val email = etEmail.text.toString().trim()

            // Validasi dulu sebelum kirim ke server
            if (email.isEmpty()) {
                etEmail.error = "Email wajib diisi"
                return@setOnClickListener
            }

            if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                etEmail.error = "Format email salah"
                return@setOnClickListener
            }

            // Kirim request reset ke Firebase
            Firebase.auth.sendPasswordResetEmail(email)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        Toast.makeText(this, "Link reset telah dikirim. Cek email Anda.", Toast.LENGTH_LONG).show()
                        finish() // Tutup halaman ini setelah sukses
                    } else {
                        // Tampilkan pesan error yang jelas (misal: user not found)
                        Toast.makeText(this, "Gagal: ${task.exception?.localizedMessage}", Toast.LENGTH_LONG).show()
                    }
                }
        }

        tvBack.setOnClickListener {
            finish()
        }
    }
}