package com.maharental.maharental_fix.fragment

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AlertDialog // Import AlertDialog
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.firestore.FirebaseFirestore
import com.maharental.maharental_fix.R
import com.maharental.maharental_fix.Kendaraan
import com.maharental.maharental_fix.databinding.FragmentCariBinding
import com.maharental.maharental_fix.katalog.DetailKendaraanActivity
import com.maharental.maharental_fix.katalog.KendaraanAdapter
import com.maharental.maharental_fix.fragment.PesanMobilActivity

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

        // 1. Setup RecyclerView
        adapterKendaraan = KendaraanAdapter(daftarKendaraan) { isListEmpty ->
            if (isListEmpty) {
                binding.tvKatalogKosong.visibility = View.VISIBLE
                binding.rvKatalog.visibility = View.GONE
            } else {
                binding.tvKatalogKosong.visibility = View.GONE
                binding.rvKatalog.visibility = View.VISIBLE
            }
        }

        // --- AKSI KLIK ITEM (DETAIL) ---
        adapterKendaraan.setOnItemClickCallback { kendaraanTerpilih ->
            val intent = Intent(requireContext(), DetailKendaraanActivity::class.java)
            intent.putExtra("EXTRA_KENDARAAN", kendaraanTerpilih)
            startActivity(intent)
        }

        // --- AKSI KLIK TOMBOL PESAN (MODIFIKASI: CEK KATEGORI & POPUP) ---
        adapterKendaraan.setOnBookingClickCallback { kendaraanTerpilih ->
            cekDanPesanKendaraan(kendaraanTerpilih)
        }
        // ---------------------------------------------

        binding.rvKatalog.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = adapterKendaraan
        }

        ambilDataKendaraan()

        // 2. Logika Search
        binding.kolomPencarian.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean = false

            override fun onQueryTextChange(newText: String?): Boolean {
                adapterKendaraan.filter(newText ?: "")
                return true
            }
        })

        // 3. Logika Tombol Back
        binding.btnKembali.setOnClickListener {
            parentFragmentManager.beginTransaction()
                .replace(R.id.frame_layout, HomeFragment())
                .commit()
            activity?.findViewById<BottomNavigationView>(R.id.bottomNavigationView)?.selectedItemId = R.id.nav_home
        }
    }

    // --- LOGIKA POPUP DAN PENGECEKAN MOTOR ---
    private fun cekDanPesanKendaraan(kendaraan: Kendaraan) {
        // Cek apakah tipe mengandung kata "Motor"
        if (kendaraan.tipe.contains("Motor", ignoreCase = true)) {
            // Langsung ke booking
            bukaHalamanPesan(kendaraan, "Lepas Kunci")
        } else {
            // Tampilkan Dialog Pilihan
            val opsiSewa = arrayOf("Lepas Kunci", "Dengan Driver")

            // Menggunakan requireContext() karena ini di dalam Fragment
            val builder = AlertDialog.Builder(requireContext())
            builder.setTitle("Pilih Opsi Sewa")
            builder.setItems(opsiSewa) { _, which ->
                val pilihan = opsiSewa[which]
                bukaHalamanPesan(kendaraan, pilihan)
            }
            builder.show()
        }
    }

    private fun bukaHalamanPesan(kendaraan: Kendaraan, opsi: String) {
        val intent = Intent(requireContext(), PesanMobilActivity::class.java)
        intent.putExtra("EXTRA_KENDARAAN", kendaraan)
        intent.putExtra("EXTRA_OPSI_SEWA", opsi) // Data pilihan dikirim ke Activity tujuan
        startActivity(intent)
    }
    // ------------------------------------------

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

                val querySekarang = binding.kolomPencarian.query.toString()
                if (querySekarang.isNotEmpty()) {
                    adapterKendaraan.filter(querySekarang)
                }
            }
            .addOnFailureListener { exception ->
                Toast.makeText(context, "Error: ${exception.message}", Toast.LENGTH_SHORT).show()
            }
    }

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