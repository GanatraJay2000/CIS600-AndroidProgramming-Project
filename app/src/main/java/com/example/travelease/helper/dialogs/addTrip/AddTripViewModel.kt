package com.example.travelease.helper.dialogs.addTrip

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class AddTripViewModel : ViewModel() {
    val _tripId = MutableLiveData<Long>()

    val tripId: MutableLiveData<Long> get() = _tripId

    fun setTripId(tripId: Long) {
        _tripId.value = tripId
    }

}
