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

        // Inisialisasi View Binding
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = Firebase.auth

        // 1. Set Fragment default saat aplikasi pertama kali dibuka
        replaceFragment(HomeFragment())

        // 2. Logika Klik pada BottomNavigationView
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

    // Fungsi pembantu (helper) untuk mengganti Fragment
    private fun replaceFragment(fragment: Fragment) {
        val fragmentManager = supportFragmentManager
        val fragmentTransaction = fragmentManager.beginTransaction()
        // 'frame_layout' adalah ID dari FrameLayout di activity_main.xml
        fragmentTransaction.replace(R.id.frame_layout, fragment)
        fragmentTransaction.commit()
    }
}