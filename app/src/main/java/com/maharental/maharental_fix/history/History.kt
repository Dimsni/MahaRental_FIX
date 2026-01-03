package com.maharental.maharental_fix.history

import com.maharental.maharental_fix.Kendaraan

object History {
    val listHistory = ArrayList<Kendaraan>()

    fun addHistory(kendaraan: Kendaraan) {
        listHistory.add(kendaraan)
    }
}