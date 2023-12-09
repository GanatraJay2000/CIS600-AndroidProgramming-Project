package com.example.project.helpers.add_trip

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class AddTripViewModel : ViewModel() {
    val _destination: MutableLiveData<String> = MutableLiveData()
    val _startDate: MutableLiveData<String> = MutableLiveData()
    val _endDate: MutableLiveData<String> = MutableLiveData()

    val destination: LiveData<String> = _destination
    val startDate: LiveData<String> = _startDate
    val endDate: LiveData<String> = _endDate

    fun setDestination(destination: String) {
        _destination.value = destination
    }

    fun setStartDate(startDate: String) {
        _startDate.value = startDate
    }

    fun setEndDate(endDate: String) {
        _endDate.value = endDate
    }



    fun setTripDetails(destination: String, startDate: String, endDate: String) {
        _destination.value = destination
        _startDate.value = startDate
        _endDate.value = endDate
    }
}
