package com.example.project.helpers.search

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.project.models.Place

class SearchBottomSheetViewModel : ViewModel() {
    val navigateToTrip = MutableLiveData<Boolean>()
    fun turnOffNavigate() {
        navigateToTrip.value = false
    }
    fun turnOnNavigate() {
        navigateToTrip.value = true
    }
    val _place = MutableLiveData<Place>()
    val place: MutableLiveData<Place>
        get() = _place

    fun setPlace(place: Place) {
        _place.value = place
    }

}