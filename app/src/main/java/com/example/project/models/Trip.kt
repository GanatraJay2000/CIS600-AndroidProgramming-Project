package com.example.project.models

import com.google.android.libraries.places.api.model.PhotoMetadata
import java.io.Serializable
import java.text.SimpleDateFormat
import java.util.Date


interface Item : Serializable
data class Note(
    val id: Int,
    val description: String,
) : Item

data class ChecklistItem(
    val id: Int,
    val title: String,
    val isCompleted: Boolean,
) : Item

data class Place(
    val id: String,
    val title: String,
    val address: String,
    val photoMetadata: MutableList<PhotoMetadata>? = null,
)

data class NearByPlace(
    val name: Any?,
    val formattedAddress: Any?,
    val placeId: Any?,
    val photos: List<*>?
)

data class NearByPlacePhoto(
    val photoReference: String,
    val height: Int,
    val width: Int
)



enum class SectionType {
    NOTES,
    CHECKLIST,
    PLACES,
}
data class Trip(
    val id: Int,
    val placeId: String,
    val title: String,
    val photoMetadata: MutableList<PhotoMetadata>? = null,
    val startDate: Date,
    val endDate: Date,
    val checkLists: List<ChecklistItem>,
    val notes: List<Note>,
    val itineraryDays: List<ItineraryDay>
): Serializable

data class ItineraryDay(
    val date: Date,
    val places: List<Place>
) : Serializable


val dummyNotes = listOf(
    Note(
        1,
        "Note1 about the trip"
    ),
    Note(
        2,
        "Note2 about the trip"
    ),
    Note(
        3,
        "Note3 about the trip"
    ),
    Note(
        4,
        "Note4 about the trip"
    )
)

val dummyChecklists = listOf(
    ChecklistItem(
        1,
        "Pack bags",
        false
    ),
    ChecklistItem(
        2,
        "Pack bags",
        false
    ),
    ChecklistItem(
        3,
        "Pack bags",
        false
    ),
    ChecklistItem(
        4,
        "Pack bags",
        false
    )
)

val dummyPlaces = listOf(
    Place(
        "1",
        "London1",
        "hehe",
    ),
    Place(
        "2",
        "London2",
        "hehe",
    ),
    Place(
        "3",
        "London3",
        "hehe",
    ),
    Place(
        "4",
        "London4",
        "hehe",
    )
)

val dummyItineraryDays = listOf(
    ItineraryDay(
        SimpleDateFormat("MM-dd-yyyy").parse("12-12-2020"),
        dummyPlaces
    ),
    ItineraryDay(
        SimpleDateFormat("MM-dd-yyyy").parse("12-13-2020"),
        dummyPlaces
    ),
    ItineraryDay(
        SimpleDateFormat("MM-dd-yyyy").parse("12-14-2020"),
        dummyPlaces
    ),
    ItineraryDay(
        SimpleDateFormat("MM-dd-yyyy").parse("12-15-2020"),
        dummyPlaces
    ),
    ItineraryDay(
        SimpleDateFormat("MM-dd-yyyy").parse("12-16-2020"),
        dummyPlaces
    ),
    ItineraryDay(
        SimpleDateFormat("MM-dd-yyyy").parse("12-17-2020"),
        dummyPlaces
    ),
    ItineraryDay(
        SimpleDateFormat("MM-dd-yyyy").parse("12-18-2020"),
        dummyPlaces
    )

)

val dummyTrips = listOf(
    Trip(
        1,
        "ChIJDZqXv5vz2YkRRZWt1-IM1QA",
        "Trip to Paris",
        null,
        SimpleDateFormat("MM-dd-yyyy").parse("12-12-2020"),
        SimpleDateFormat("MM-dd-yyyy").parse("12-18-2020"),
        dummyChecklists,
        dummyNotes,
        dummyItineraryDays
    ),
);