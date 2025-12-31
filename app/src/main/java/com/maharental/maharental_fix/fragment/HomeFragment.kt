package com.maharental.maharental_fix.fragment

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.android.material.button.MaterialButton
import com.maharental.maharental_fix.PesanMobilActivity
import com.maharental.maharental_fix.R

class HomeFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        val view = inflater.inflate(R.layout.fragment_home, container, false)

        val btnPesanMobil = view.findViewById<MaterialButton>(R.id.btnPesanMobil)

        btnPesanMobil.setOnClickListener {
            startActivity(
                Intent(requireContext(), PesanMobilActivity::class.java)
            )
        }

        return view
    }
}
