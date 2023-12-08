package com.example.project.ui.location

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class LocationViewModel : ViewModel() {
    private val _locationId = MutableLiveData<Int>()
    fun setLocationId(locationId: Int) {
        _locationId.value = locationId
    }

    val locationId: LiveData<Int> = _locationId
}