package com.example.project.ui.profile

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.project.models.Trip
import kotlinx.coroutines.launch

class ProfileViewModel(private val databaseHelper: MyDatabaseHelper) : ViewModel() {
    // LiveData to hold the list of trips
    private val _tripsLiveData = MutableLiveData<List<Trip>>()
    val tripsLiveData: LiveData<List<Trip>> = _tripsLiveData

    fun getTripsByUserId(userId: Int) {
        viewModelScope.launch {
            val trips = databaseHelper.getAllTrips() // Retrieve trips using MyDatabaseHelper
            _tripsLiveData.postValue(trips) // Post the fetched trips to LiveData
        }
    }
}

// In ProfileViewModel.kt

class ProfileViewModelFactory(private val databaseHelper: MyDatabaseHelper) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ProfileViewModel::class.java)) {
            return ProfileViewModel(databaseHelper) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
