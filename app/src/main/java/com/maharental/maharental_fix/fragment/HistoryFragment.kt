package com.maharental.maharental_fix.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.maharental.maharental_fix.Kendaraan
import com.maharental.maharental_fix.databinding.FragmentHistoryBinding
import com.maharental.maharental_fix.history.HistoryAdapter

class HistoryFragment : Fragment() {

    private var _binding: FragmentHistoryBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHistoryBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.rvHistory.layoutManager = LinearLayoutManager(context)
        loadHistory()
    }

    override fun onResume() {
        super.onResume()
        loadHistory()
    }

    private fun loadHistory() {
        val uid = FirebaseAuth.getInstance().currentUser?.uid
        if (uid == null) {
            binding.tvEmptyHistory.visibility = View.VISIBLE
            binding.rvHistory.visibility = View.GONE
            binding.tvEmptyHistory.text = "Silakan login untuk melihat riwayat"
            return
        }

        FirebaseFirestore.getInstance()
            .collection("history")
            .document(uid)
            .collection("items")
            .orderBy("timestamp", Query.Direction.DESCENDING)
            .get()
            .addOnSuccessListener { snap ->
                val list = snap.documents.mapNotNull { doc ->
                    val nama = doc.getString("nama") ?: return@mapNotNull null
                    val tipe = doc.getString("tipe") ?: ""
                    val harga = doc.getLong("harga") ?: 0L
                    val gambar = doc.getString("gambar") ?: ""
                    val deskripsi = doc.getString("deskripsi") ?: ""
                    val jumlahUnit = (doc.getLong("jumlahUnit") ?: 1L).toInt()

                    Kendaraan(
                        nama = nama,
                        tipe = tipe,
                        harga = harga,          // ✅ sudah TOTAL
                        gambar = gambar,
                        deskripsi = deskripsi,
                        jumlahUnit = jumlahUnit
                    )
                }

                if (list.isEmpty()) {
                    binding.tvEmptyHistory.visibility = View.VISIBLE
                    binding.rvHistory.visibility = View.GONE
                } else {
                    binding.tvEmptyHistory.visibility = View.GONE
                    binding.rvHistory.visibility = View.VISIBLE
                    binding.rvHistory.adapter = HistoryAdapter(list)
                }
            }
            .addOnFailureListener {
                binding.tvEmptyHistory.visibility = View.VISIBLE
                binding.rvHistory.visibility = View.GONE
                binding.tvEmptyHistory.text = "Gagal memuat riwayat"
            }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
