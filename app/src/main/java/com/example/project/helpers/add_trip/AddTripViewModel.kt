package com.example.project.helpers.add_trip

import androidx.lifecycle.ViewModel

class AddTripViewModel : ViewModel() {
    var destination: String? = null
    var startDate: String? = null
    var endDate: String? = null

    // Functions to update the trip information
    fun setTripDetails(destination: String, startDate: String, endDate: String) {
        this.destination = destination
        this.startDate = startDate
        this.endDate = endDate
    }
}
