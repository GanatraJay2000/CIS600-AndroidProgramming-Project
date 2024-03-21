package com.example.travelease.helper.dialogs.search

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.travelease.model.Place

class SearchBottomSheetViewModel : ViewModel() {
    val navigateToLocation = MutableLiveData<Boolean>()
    fun turnOffNavigate() {
        navigateToLocation.value = false
    }
    fun turnOnNavigate() {
        navigateToLocation.value = true
    }

    val _place = MutableLiveData<Place>()
    val place: MutableLiveData<Place>
        get() = _place

    fun setPlace(place: Place) {
        _place.value = place
    }
}