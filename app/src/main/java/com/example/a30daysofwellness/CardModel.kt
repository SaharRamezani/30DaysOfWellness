package com.example.a30daysofwellness

import android.net.Uri

data class CardModel(
    val day: Int,
    val title: String,
    val description: String,
    val imageUri: Uri?
)
