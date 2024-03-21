package com.example.travelease.ui.location

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.travelease.model.Place

class LocationViewModel : ViewModel() {
    private val _location = MutableLiveData<Place>()

    val location: MutableLiveData<Place> get() = _location

    fun setLocation(location: Place) {
        _location.value = location
    }

    private val _places = MutableLiveData<MutableList<Place>>()

    val places: MutableLiveData<MutableList<Place>> get() = _places

    fun setPlaces(places: MutableList<Place>) {
        _places.value = places
    }
}