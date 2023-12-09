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
)

data class Checklist(
    val id: Int,
    val title: String,
    val items: List<ChecklistItem>,
) : Item

data class Place(
    val id: String,
    val title: String,
    val address: String,
    val photoMetadata: MutableList<PhotoMetadata>? = null,
) : Item

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
data class Section(
    val id: Int,
    val title: String,
    val type: SectionType,
    val items: List<Item>
): Serializable
data class Trip(
    val id: Int,
    val title: String,
    val imageUrl: String,
    val startDate: Date,
    val endDate: Date,
    val sections: List<Section>,
    val itineraryDays: List<ItineraryDay>
): Serializable

data class ItineraryDay(
    val date: Date,
    val sections: List<Section>
) : Serializable

val dummyNotes = listOf<Note>(
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
    ),

)

val dummyChecklistItems = listOf<ChecklistItem>(
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
    ),
)

val dummySections = listOf(
    Section(
        1,
        "My Notes",
        SectionType.NOTES,
        listOf(
            Note(1, "Note1 about the trip"),
            Note(2, "Note2 about the trip"),
            Note(3, "Note3 about the trip"),
            Note(4, "Note4 about the trip"),
        )
    ),
    Section(2, "Checklist", SectionType.CHECKLIST, listOf(Checklist(1, "Checklist1", listOf(ChecklistItem(1, "Pack bags", false))))),
    Section(
        3,
        "Places to Visit",
        SectionType.PLACES,
        listOf(
            Place(
                "1",
                "London1",
                "hehe",
            ),
            Place(
                "2",
                "London2",
                "hehe",
            )

        )
    )
)

val dummyTrips = listOf(
    Trip(
        1,
        "Trip to Paris",
        "https://images.unsplash.com/photo-1528360983277-13d401cdc186?q=80&w=2070&auto=format&fit=crop&ixlib=rb-4.0.3&ixid=M3wxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8fA%3D%3D",
        SimpleDateFormat("MM-dd-yyyy").parse("12-12-2020"),
        SimpleDateFormat("MM-dd-yyyy").parse("12-18-2020"),
        dummySections,
        listOf(
            ItineraryDay(
                SimpleDateFormat("MM-dd-yyyy").parse("12-12-2020"),
                dummySections
            ),
            ItineraryDay(
                SimpleDateFormat("MM-dd-yyyy").parse("12-13-2020"),
                dummySections
            ),
            ItineraryDay(
                SimpleDateFormat("MM-dd-yyyy").parse("12-14-2020"),
                dummySections
            ),
            ItineraryDay(
                SimpleDateFormat("MM-dd-yyyy").parse("12-15-2020"),
                dummySections
            ),
            ItineraryDay(
                SimpleDateFormat("MM-dd-yyyy").parse("12-16-2020"),
                dummySections
            ),
            ItineraryDay(
                SimpleDateFormat("MM-dd-yyyy").parse("12-17-2020"),
                dummySections
            ),
            ItineraryDay(
                SimpleDateFormat("MM-dd-yyyy").parse("12-18-2020"),
                dummySections
            )
        )
    ),
    Trip(
        2,
        "Trip to London",
        "https://images.unsplash.com/photo-1502602898657-3e91760cbb34?q=80&w=2073&auto=format&fit=crop&ixlib=rb-4.0.3&ixid=M3wxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8fA%3D%3D", // Replace with actual URLs
        SimpleDateFormat("MM-dd-yyyy").parse("12-12-2020"),
        SimpleDateFormat("MM-dd-yyyy").parse("12-18-2020"),
        dummySections,
        listOf()
    ),
    Trip(
        3,
        "Trip to New York",
        "https://images.unsplash.com/photo-1507699622108-4be3abd695ad?q=80&w=2071&auto=format&fit=crop&ixlib=rb-4.0.3&ixid=M3wxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8fA%3D%3D",
        SimpleDateFormat("MM-dd-yyyy").parse("12-12-2020"),
        SimpleDateFormat("MM-dd-yyyy").parse("12-18-2020"),
        dummySections,
        listOf()
    ),
    Trip(
        4,
        "Trip to Tokyo",
        "https://images.unsplash.com/photo-1519659528534-7fd733a832a0?q=80&w=1926&auto=format&fit=crop&ixlib=rb-4.0.3&ixid=M3wxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8fA%3D%3D",
        SimpleDateFormat("MM-dd-yyyy").parse("12-12-2020"),
        SimpleDateFormat("MM-dd-yyyy").parse("12-18-2020"),
        dummySections,
        listOf()
    )
);