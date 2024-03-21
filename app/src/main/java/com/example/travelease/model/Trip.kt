package com.example.travelease.model

import com.google.android.gms.maps.model.LatLng
import com.google.android.libraries.places.api.model.PhotoMetadata
import java.io.Serializable
import java.util.Date



interface Item : Serializable

data class Note(
    val id: Int? = null,
    val tripId: Int,
    val description: String,
) : Item

data class ChecklistItem(
    val id: Int? = null,
    val tripId: Int,
    val title: String,
    val isCompleted: Boolean,
) : Item


data class Place(
    val id: String,
    val itineraryDayId: Int? = null,
    val title: String? = null,
    val address: String? = null,
    val photoMetadata: MutableList<PhotoMetadata>? = null,
    val latlng: LatLng? = null,
) : Serializable

class PlaceData {
    val topLocations = listOf(
        Place(
            "ChIJ7cv00DwsDogRAMDACa2m4K8",
            null,
            "Chicago",
            "Chicago, IL, USA",
            null,
            null
        ),
        Place(
            "ChIJE9on3F3HwoAR9AhGJW_fL-I",
            null,
            "Los Angeles",
            "Los Angeles, CA, USA",
            null,
            null
        ),
        Place(
            "ChIJAYWNSLS4QIYROwVl894CDco",
            null,
            "Houston",
            "Houston, TX, USA",
            null,
            null
        ),
        Place(
            "ChIJVTPokywQkFQRmtVEaUZlJRA",
            null,
            "Seattle",
            "Seattle, WA, USA",
            null,
            null
        )
    )
    val nearbyLocations = listOf(
        Place(
            "ChIJOwg_06VPwokRYv534QaPC8g",
            null,
            "New York",
            "New York, NY, USA",
            null,
            null
        ),
        Place(
            "ChIJ94iv4IKB0IkRdM5upWgn5Qo",
            null,
            "Ithaca",
            "Ithaca, NY, USA",
            null,
            null
        ),
        Place(
            "ChIJj2HZewpZ0IkRCRdRnhG9LNQ",
            null,
            "Watkins Glen",
            "Watkins Glen, NY, USA",
            null,
            null
        ),
        Place(
            "ChIJU7MUlgWz1okRHuYlQfwfAFo",
            null,
            "Rochester",
            "Rochester, NY, USA",
            null,
            null
        )
    )
}

data class ItineraryDay(
    val id: Int? = null,
    val tripId: Int,
    val date: Date,
    val places: List<Place>? = null
) : Serializable

data class Trip(
    val id: Int? = null,
    val userId: String,
    val placeId: String,
    val title: String,
    val startDate: String,
    val endDate: String,
    val checkLists: List<ChecklistItem>? = null,
    val notes: List<Note>?= null,
    val itineraryDays: List<ItineraryDay>?= null
): Serializable

