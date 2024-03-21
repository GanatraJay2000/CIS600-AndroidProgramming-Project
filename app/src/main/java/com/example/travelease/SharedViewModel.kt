package com.example.travelease

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class SharedViewModel : ViewModel() {
    private val _locationId = MutableLiveData<String>()

    val locationId: MutableLiveData<String> get() = _locationId

    fun setLocationId(locationId: String) {
        _locationId.value = locationId
    }



    private val _locationTitle = MutableLiveData<String>()

    val locationTitle: MutableLiveData<String> get() = _locationTitle

    fun setLocationTitle(locationTitle: String) {
        _locationTitle.value = locationTitle
    }




    val _tripId = MutableLiveData<Int>()

    val tripId: Int? get() = _tripId.value

    fun setTripId(id: Int) {
        _tripId.value = id
    }
}