package com.maharental.maharental_fix.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.Toast
import com.maharental.maharental_fix.R

class HomeFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate layout baru
        val view = inflater.inflate(R.layout.fragment_home, container, false)

        // Inisialisasi tombol opsi (opsional, untuk memberikan aksi klik)
        val btnLepasKunci = view.findViewById<LinearLayout>(R.id.btn_lepas_kunci)
        val btnPakeDriver = view.findViewById<LinearLayout>(R.id.btn_pake_driver)

        btnLepasKunci.setOnClickListener {
            Toast.makeText(context, "Anda memilih Lepas Kunci", Toast.LENGTH_SHORT).show()
            // Di sini nanti bisa tambahkan kode untuk pindah ke halaman list mobil
        }

        btnPakeDriver.setOnClickListener {
            Toast.makeText(context, "Anda memilih Dengan Driver", Toast.LENGTH_SHORT).show()
        }

        return view
    }
}