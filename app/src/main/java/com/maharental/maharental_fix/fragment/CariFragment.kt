package com.maharental.maharental_fix.fragment

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.firestore.FirebaseFirestore
import com.maharental.maharental_fix.R
import com.maharental.maharental_fix.Kendaraan
import com.maharental.maharental_fix.databinding.FragmentCariBinding
import com.maharental.maharental_fix.katalog.KendaraanAdapter

class CariFragment : Fragment() {

    private var _binding: FragmentCariBinding? = null
    private val binding get() = _binding!!

    private lateinit var adapterKendaraan: KendaraanAdapter
    private val daftarKendaraan = ArrayList<Kendaraan>()
    private lateinit var db: FirebaseFirestore

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCariBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        db = FirebaseFirestore.getInstance()

        // 1. Setup RecyclerView dengan Callback Empty State
        adapterKendaraan = KendaraanAdapter(daftarKendaraan) { isListEmpty ->
            // Logika: Jika list kosong, tampilkan pesan. Jika tidak, sembunyikan.
            if (isListEmpty) {
                binding.tvKatalogKosong.visibility = View.VISIBLE
                binding.rvKatalog.visibility = View.GONE
            } else {
                binding.tvKatalogKosong.visibility = View.GONE
                binding.rvKatalog.visibility = View.VISIBLE
            }
        }

        binding.rvKatalog.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = adapterKendaraan
        }

        ambilDataKendaraan()

        // 2. Logika Search (Sesuai String)
        binding.kolomPencarian.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean = false

            override fun onQueryTextChange(newText: String?): Boolean {
                adapterKendaraan.filter(newText ?: "")
                return true
            }
        })

        // 3. Logika Tombol Back (Kembali ke Home)
        binding.btnKembali.setOnClickListener {
            // Arahkan kembali ke HomeFragment
            parentFragmentManager.beginTransaction()
                .replace(R.id.frame_layout, HomeFragment())
                .commit()

            // Pindahkan seleksi navbar bawah ke icon Home agar sinkron
            activity?.findViewById<BottomNavigationView>(R.id.bottomNavigationView)?.selectedItemId = R.id.nav_home
        }
    }

    private fun ambilDataKendaraan() {
        db.collection("kendaraan")
            .get()
            .addOnSuccessListener { documents ->
                val dataBaru = ArrayList<Kendaraan>()
                for (document in documents) {
                    val data = document.toObject(Kendaraan::class.java)
                    dataBaru.add(data)
                }
                adapterKendaraan.updateData(dataBaru)

                // Pastikan filter berjalan ulang jika user sudah mengetik sesuatu sebelum data loading selesai
                val querySekarang = binding.kolomPencarian.query.toString()
                if (querySekarang.isNotEmpty()) {
                    adapterKendaraan.filter(querySekarang)
                }
            }
            .addOnFailureListener { exception ->
                Toast.makeText(context, "Error: ${exception.message}", Toast.LENGTH_SHORT).show()
            }
    }

    // --- Hide Bottom Navbar ---
    override fun onResume() {
        super.onResume()
        activity?.findViewById<BottomNavigationView>(R.id.bottomNavigationView)?.visibility = View.GONE
    }

    override fun onPause() {
        super.onPause()
        activity?.findViewById<BottomNavigationView>(R.id.bottomNavigationView)?.visibility = View.VISIBLE
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}