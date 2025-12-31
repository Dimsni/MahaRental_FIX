package com.maharental.maharental_fix.fragment

import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.FirebaseAuth
import com.maharental.maharental_fix.R

class HomeFragment : Fragment() {

    private lateinit var auth: FirebaseAuth
    private lateinit var tvUsername: TextView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_home, container, false)

        // Inisialisasi Firebase Auth
        auth = FirebaseAuth.getInstance()

        // Binding ID dari Layout
        tvUsername = view.findViewById(R.id.tv_username_home)

        // 1. Tombol Shopping Cart (Pesan Sekarang / Lepas Kunci)
        val btnLepasKunci = view.findViewById<LinearLayout>(R.id.btn_lepas_kunci)

        // 2. Tombol Article (Syarat Sewa / Pake Driver)
        val btnPakeDriver = view.findViewById<LinearLayout>(R.id.btn_pake_driver)

        // LOGIKA KLIK: Shopping Cart -> Pindah ke Tab Cari (Search)
        btnLepasKunci.setOnClickListener {
            // Mengakses BottomNavigationView di MainActivity untuk pindah tab
            val bottomNav = requireActivity().findViewById<BottomNavigationView>(R.id.bottomNavigationView)
            bottomNav.selectedItemId = R.id.nav_cari
        }

        // LOGIKA KLIK: Article -> Munculkan Pop Up Dialog Syarat Sewa
        btnPakeDriver.setOnClickListener {
            showSyaratSewaDialog()
        }

        return view
    }

    // Fungsi untuk memunculkan Dialog Syarat Sewa sebagai Pop Up
    private fun showSyaratSewaDialog() {
        val dialog = Dialog(requireContext())
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setContentView(R.layout.dialog_syarat_sewa)

        // Mengatur background dialog menjadi transparan agar rounded corner CardView terlihat
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        // Mengatur lebar dialog agar sesuai (match parent dengan sedikit margin bawaan Android)
        dialog.window?.setLayout(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )

        // Logika tombol Close (X) di dalam dialog
        val btnClose = dialog.findViewById<ImageView>(R.id.btn_close_dialog)
        btnClose.setOnClickListener {
            dialog.dismiss() // Menutup dialog
        }

        dialog.show()
    }

    override fun onResume() {
        super.onResume()
        loadUserData()
    }

    private fun loadUserData() {
        val currentUser = auth.currentUser
        if (currentUser != null) {
            val nama = currentUser.displayName ?: "User MahaRental"
            tvUsername.text = nama
        }
    }
}