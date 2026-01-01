package com.maharental.maharental_fix

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Kendaraan(
    var nama: String = "",
    var tipe: String = "",
    var harga: Long = 0,
    var gambar: String = "",
    var deskripsi: String = ""
) : Parcelable