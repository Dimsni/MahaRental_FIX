package com.maharental.maharental_fix.Login

import android.content.Intent
import android.os.Bundle
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import com.maharental.maharental_fix.MainActivity
import com.maharental.maharental_fix.R

class LoginActivity : AppCompatActivity() {

    // Deklarasi Auth
    private lateinit var auth: FirebaseAuth

    public override fun onStart() {
        super.onStart()
        // Cek jika user sudah login sebelumnya, langsung masuk Main
        val currentUser = auth.currentUser
        if (currentUser != null) {
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        // Inisialisasi Firebase Auth
        auth = Firebase.auth

        // View Binding Manual
        val tilEmail = findViewById<TextInputLayout>(R.id.til_email)
        val etEmail = findViewById<TextInputEditText>(R.id.et_email)
        val tilPassword = findViewById<TextInputLayout>(R.id.til_password)
        val etPassword = findViewById<TextInputEditText>(R.id.et_password)
        val btnLogin = findViewById<MaterialButton>(R.id.btn_login)
        val tvRegister = findViewById<TextView>(R.id.tv_register_link)
        val tvForgot = findViewById<TextView>(R.id.tv_forgot_pass)

        // 1. Logic Tombol Login dengan FIREBASE
        btnLogin.setOnClickListener {
            val email = etEmail.text.toString().trim()
            val password = etPassword.text.toString()

            // Reset Error
            tilEmail.error = null
            tilPassword.error = null

            // Validasi Input
            if (email.isEmpty()) {
                tilEmail.error = "Email harus diisi"
                return@setOnClickListener
            }
            if (password.isEmpty()) {
                tilPassword.error = "Password harus diisi"
                return@setOnClickListener
            }

            // PROSES LOGIN KE FIREBASE
            auth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this) { task ->
                    if (task.isSuccessful) {
                        // Login Sukses
                        Toast.makeText(this, "Selamat Datang!", Toast.LENGTH_SHORT).show()
                        val intent = Intent(this, MainActivity::class.java)
                        startActivity(intent)
                        finish()
                    } else {
                        // Login Gagal
                        Toast.makeText(this, "Login Gagal: Periksa Email/Password", Toast.LENGTH_SHORT).show()
                    }
                }
        }

        // 2. Logic Navigasi ke Daftar
        // Navigasi ke Daftar
        tvRegister.setOnClickListener {
            // Pastikan ini mengarah ke DaftarAkun::class.java
            val intent = Intent(this, DaftarAkun::class.java)
            startActivity(intent)
        }

        // Navigasi ke Lupa Password
        tvForgot.setOnClickListener {
            // Pastikan ini mengarah ke LupaPassword::class.java
            val intent = Intent(this, LupaPassword::class.java)
            startActivity(intent)
        }
    }
}