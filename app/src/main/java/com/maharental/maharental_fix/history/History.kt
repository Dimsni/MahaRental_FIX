package com.maharental.maharental_fix.history

import com.maharental.maharental_fix.Kendaraan

object History {
    val listHistory: MutableList<Kendaraan> = mutableListOf()

    fun add(item: Kendaraan) {
        listHistory.add(0, item) // terbaru di atas
    }
}
