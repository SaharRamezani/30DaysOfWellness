package com.example.a30daysofwellness

import android.net.Uri
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class MainViewModel : ViewModel() {
    private val _cardList = MutableStateFlow<List<CardModel>>(emptyList())
    val cardList = _cardList.asStateFlow()

    private var dayCounter = 1

    fun addCard(title: String, description: String, imageUri: Uri?) {
        val newCard = CardModel(
            day = dayCounter,
            title = title,
            description = description,
            imageUri = imageUri
        )
        _cardList.value = _cardList.value + newCard
        dayCounter++
    }
}
