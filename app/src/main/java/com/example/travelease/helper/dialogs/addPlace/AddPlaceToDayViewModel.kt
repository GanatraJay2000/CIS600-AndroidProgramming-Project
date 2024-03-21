package com.example.travelease.helper.dialogs.addPlace

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.travelease.model.Place

class AddPlaceToDayViewModel : ViewModel() {
    private val _location = MutableLiveData<Place>()

    val location: MutableLiveData<Place> get() = _location

    fun setLocation(location: Place) {
        _location.value = location
    }
}