package com.example.travelease.ui.trip

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.travelease.model.Place

class TripViewModel : ViewModel() {
    private val _place = MutableLiveData<Place>()

    val place: MutableLiveData<Place> get() = _place

    fun setPlace(place: Place) {
        _place.value = place
    }
}