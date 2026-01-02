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

        val btnLepasKunci = view.findViewById<LinearLayout>(R.id.btn_pesan_sekarang)
        val btnPakeDriver = view.findViewById<LinearLayout>(R.id.btn_syarat_sewa)

        // 1. Tambahkan ID untuk tombol CS
        val btnCS = view.findViewById<LinearLayout>(R.id.btn_customer_service)

        // LOGIKA KLIK: Shopping Cart -> Pindah ke Tab Cari (Search)
        btnLepasKunci.setOnClickListener {
            val bottomNav = requireActivity().findViewById<BottomNavigationView>(R.id.bottomNavigationView)
            bottomNav.selectedItemId = R.id.nav_cari
        }

        // LOGIKA KLIK: Article -> Munculkan Pop Up Dialog Syarat Sewa
        btnPakeDriver.setOnClickListener {
            showSyaratSewaDialog()
        }

        // 2. LOGIKA KLIK: Customer Service -> Munculkan Pop Up CS
        btnCS.setOnClickListener {
            showCustomerServiceDialog()
        }

        return view
    }

    private fun showSyaratSewaDialog() {
        val dialog = Dialog(requireContext())
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setContentView(R.layout.dialog_syarat_sewa)

        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.window?.setLayout(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )

        val btnClose = dialog.findViewById<ImageView>(R.id.btn_close_dialog)
        btnClose.setOnClickListener {
            dialog.dismiss()
        }

        dialog.show()
    }

    // 3. Fungsi Baru: Munculkan Dialog CS
    private fun showCustomerServiceDialog() {
        val dialog = Dialog(requireContext())
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setContentView(R.layout.dialog_customer_service) // Pastikan file layout ini sudah dibuat

        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.window?.setLayout(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )

        val btnClose = dialog.findViewById<ImageView>(R.id.btn_close_cs)
        btnClose.setOnClickListener {
            dialog.dismiss()
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