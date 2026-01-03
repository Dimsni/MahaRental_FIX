package com.maharental.maharental_fix

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import com.maharental.maharental_fix.databinding.ActivityMainBinding
import com.maharental.maharental_fix.fragment.CariFragment
import com.maharental.maharental_fix.fragment.HistoryFragment
import com.maharental.maharental_fix.fragment.HomeFragment
import com.maharental.maharental_fix.fragment.ProfileFragment

class MainActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = Firebase.auth

        // --- LOGIKA NAVIGASI OTOMATIS ---
        val goToHistory = intent.getBooleanExtra("GO_TO_HISTORY", false)

        if (goToHistory) {
            // Jika ada flag, langsung ke HistoryFragment dan set icon menu bawah
            binding.bottomNavigationView.selectedItemId = R.id.nav_history
            replaceFragment(HistoryFragment())
        } else {
            // Jika tidak ada flag (buka normal), tampilkan HomeFragment
            if (savedInstanceState == null) { // Supaya tidak tertimpa saat rotasi layar
                replaceFragment(HomeFragment())
            }
        }

        // --- LOGIKA KLIK BOTTOM NAVIGATION ---
        binding.bottomNavigationView.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_home -> {
                    replaceFragment(HomeFragment())
                    true
                }
                R.id.nav_cari -> {
                    replaceFragment(CariFragment())
                    true
                }
                R.id.nav_history -> {
                    replaceFragment(HistoryFragment())
                    true
                }
                R.id.nav_profil -> {
                    replaceFragment(ProfileFragment())
                    true
                }
                else -> false
            }
        }
    }

    private fun replaceFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.frame_layout, fragment)
            .commit()
    }
}