package com.maharental.maharental_fix.Login

import android.content.Intent
import android.os.Bundle
import android.util.Patterns
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.UserProfileChangeRequest
import com.google.firebase.auth.auth
import com.maharental.maharental_fix.R

class DaftarAkun : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_daftar_akun)

        // Inisialisasi Firebase
        auth = Firebase.auth

        // Binding Views
        val etNama = findViewById<TextInputEditText>(R.id.et_nama)
        val etEmail = findViewById<TextInputEditText>(R.id.et_email_daftar)
        val etPassword = findViewById<TextInputEditText>(R.id.et_pass_daftar)
        val btnDaftar = findViewById<MaterialButton>(R.id.btn_daftar)
        val tvLogin = findViewById<TextView>(R.id.tv_login_link)

        // 1. Aksi Tombol Daftar
        btnDaftar.setOnClickListener {
            val nama = etNama.text.toString().trim()
            val email = etEmail.text.toString().trim()
            val password = etPassword.text.toString()

            // Validasi Input yang defensif
            if (nama.isEmpty()) {
                etNama.error = "Nama lengkap wajib diisi"
                etNama.requestFocus()
                return@setOnClickListener
            }

            if (email.isEmpty() || !Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                etEmail.error = "Email tidak valid"
                etEmail.requestFocus()
                return@setOnClickListener
            }

            if (password.length < 6) {
                etPassword.error = "Password minimal 6 karakter"
                etPassword.requestFocus()
                return@setOnClickListener
            }

            // Proses ke Firebase
            buatAkunBaru(nama, email, password)
        }

        // 2. Navigasi Kembali ke Login
        tvLogin.setOnClickListener {
            finish()
        }
    }

    private fun buatAkunBaru(nama: String, email: String, pass: String) {

        auth.createUserWithEmailAndPassword(email, pass)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    val user = auth.currentUser
                    val profileUpdates = UserProfileChangeRequest.Builder()
                        .setDisplayName(nama)
                        .build()

                    user?.updateProfile(profileUpdates)
                        ?.addOnCompleteListener {
                            // Tampilkan pesan sukses
                            Toast.makeText(
                                this,
                                "Pendaftaran berhasil, silakan login",
                                Toast.LENGTH_SHORT
                            ).show()

                            // Logout dulu agar Login.kt tidak menganggap user sudah masuk
                            auth.signOut()

                            // Arahkan ke Login
                            startActivity(Intent(this, com.maharental.maharental_fix.Login.LoginActivity::class.java))
                            finishAffinity()
                        }
                } else {
                    Toast.makeText(this, "Gagal: ${task.exception?.message}", Toast.LENGTH_LONG)
                        .show()
                }
            }
    }
}