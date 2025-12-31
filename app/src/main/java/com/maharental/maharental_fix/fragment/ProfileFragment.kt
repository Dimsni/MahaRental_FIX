package com.maharental.maharental_fix.fragment

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.UserProfileChangeRequest
import com.maharental.maharental_fix.Login.LoginActivity
import com.maharental.maharental_fix.R

class ProfileFragment : Fragment() {

    private lateinit var auth: FirebaseAuth

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate layout
        val view = inflater.inflate(R.layout.fragment_profile, container, false)

        // Inisialisasi Firebase Auth
        auth = FirebaseAuth.getInstance()

        // Binding View
        val etNama = view.findViewById<TextInputEditText>(R.id.et_nama)
        val etEmail = view.findViewById<TextInputEditText>(R.id.et_email)
        val btnSave = view.findViewById<Button>(R.id.btn_save)
        val btnLogout = view.findViewById<Button>(R.id.btn_logout)
        val ivProfile = view.findViewById<ImageView>(R.id.iv_profile)

        // 1. TAMPILKAN DATA USER SAAT INI
        val currentUser = auth.currentUser
        if (currentUser != null) {
            etNama.setText(currentUser.displayName) // Tampilkan Nama
            etEmail.setText(currentUser.email)      // Tampilkan Email
        }

        // 2. LOGIKA SIMPAN PROFIL (UBAH NAMA)
        btnSave.setOnClickListener {
            val namaBaru = etNama.text.toString().trim()

            if (namaBaru.isEmpty()) {
                etNama.error = "Nama tidak boleh kosong"
                return@setOnClickListener
            }

            // Update Profile di Firebase
            val profileUpdates = UserProfileChangeRequest.Builder()
                .setDisplayName(namaBaru)
                .build()

            currentUser?.updateProfile(profileUpdates)
                ?.addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        Toast.makeText(context, "Profil berhasil diperbarui", Toast.LENGTH_SHORT).show()
                    } else {
                        Toast.makeText(context, "Gagal update: ${task.exception?.message}", Toast.LENGTH_SHORT).show()
                    }
                }
        }

        // 3. LOGIKA LOGOUT
        btnLogout.setOnClickListener {
            auth.signOut()
            val intent = Intent(activity, LoginActivity::class.java)
            // Hapus stack activity agar user tidak bisa back ke halaman ini
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
        }

        return view
    }
}