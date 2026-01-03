package com.maharental.maharental_fix.history

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

class HistoryAdapter(private val listHistory: List<Kendaraan>) :
    RecyclerView.Adapter<HistoryAdapter.HistoryViewHolder>() {

    class HistoryViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val imgGambar: ImageView = itemView.findViewById(R.id.ivGambarHistory)
        val txtNama: TextView = itemView.findViewById(R.id.tvNamaHistory)
        val txtTipe: TextView = itemView.findViewById(R.id.tvTipeHistory)
        val txtHarga: TextView = itemView.findViewById(R.id.tvHargaHistory)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HistoryViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_history, parent, false)
        return HistoryViewHolder(view)
    }

    override fun onBindViewHolder(holder: HistoryViewHolder, position: Int) {
        val data = listHistory[position]

        holder.txtNama.text = data.nama
        holder.txtTipe.text = data.tipe

        val formatRupiah = NumberFormat.getCurrencyInstance(Locale("id", "ID"))
        holder.txtHarga.text = formatRupiah.format(data.harga)

        Glide.with(holder.itemView.context)
            .load(data.gambar)
            .centerCrop()
            .placeholder(R.drawable.ic_launcher_background)
            .error(R.drawable.ic_launcher_background)
            .into(holder.imgGambar)
    }

    override fun getItemCount(): Int {
        return listHistory.size
    }
}