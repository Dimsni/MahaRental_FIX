package com.maharental.maharental_fix.katalog

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.maharental.maharental_fix.Kendaraan
import com.maharental.maharental_fix.R
import java.text.NumberFormat
import java.util.Locale

// Tambahkan parameter callback di constructor atau fungsi filter
class KendaraanAdapter(
    private val daftarAsli: ArrayList<Kendaraan>,
    // Callback untuk memberi tahu Fragment jika list kosong
    private val onEmptyState: (Boolean) -> Unit
) : RecyclerView.Adapter<KendaraanAdapter.KendaraanViewHolder>() {

    private var daftarTampil: ArrayList<Kendaraan> = ArrayList(daftarAsli)

    init {
        updateList(daftarAsli)
    }

    class KendaraanViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val imgGambar: ImageView = itemView.findViewById(R.id.ivGambarKendaraan)
        val txtNama: TextView = itemView.findViewById(R.id.tvNamaKendaraan)
        val txtTipe: TextView = itemView.findViewById(R.id.tvTipeKendaraan)
        val txtHarga: TextView = itemView.findViewById(R.id.tvHargaSewa)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): KendaraanViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_kendaraan, parent, false)
        return KendaraanViewHolder(view)
    }

    override fun onBindViewHolder(holder: KendaraanViewHolder, position: Int) {
        val data = daftarTampil[position]

        holder.txtNama.text = data.nama
        holder.txtTipe.text = data.tipe

        val formatRupiah = NumberFormat.getCurrencyInstance(Locale("id", "ID"))
        holder.txtHarga.text = "${formatRupiah.format(data.harga)} / hari"

        Glide.with(holder.itemView.context)
            .load(data.gambar)
            .centerCrop()
            .placeholder(R.drawable.ic_launcher_background)
            .error(R.drawable.ic_launcher_background)
            .into(holder.imgGambar)
    }

    override fun getItemCount(): Int {
        return daftarTampil.size
    }

    fun updateData(dataBaru: List<Kendaraan>) {
        daftarAsli.clear()
        daftarAsli.addAll(dataBaru)
        updateList(daftarAsli)
    }

    fun filter(query: String) {
        val teksPencarian = query.lowercase(Locale.getDefault())
        val hasilFilter = ArrayList<Kendaraan>()

        if (teksPencarian.isEmpty()) {
            hasilFilter.addAll(daftarAsli)
        } else {
            for (item in daftarAsli) {
                // Cari berdasarkan Nama atau Tipe
                if (item.nama.lowercase(Locale.getDefault()).contains(teksPencarian) ||
                    item.tipe.lowercase(Locale.getDefault()).contains(teksPencarian)) {
                    hasilFilter.add(item)
                }
            }
        }
        updateList(hasilFilter)
    }

    private fun updateList(listBaru: List<Kendaraan>) {
        daftarTampil.clear()
        daftarTampil.addAll(listBaru)
        notifyDataSetChanged()

        // PENTING: Cek apakah hasil filter kosong?
        // Jika kosong, kirim 'true'. Jika ada isi, kirim 'false'.
        if (daftarTampil.isEmpty()) {
            onEmptyState(true)
        } else {
            onEmptyState(false)
        }
    }
}