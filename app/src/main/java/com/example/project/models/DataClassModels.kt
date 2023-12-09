package com.example.project.models

import java.io.Serializable
import java.util.Date


interface Item : Serializable

data class Note(
    val id: Int,
    val description: String,
    val title: String,
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
    val id: Int,
    val title: String,
    val imageUrl: String,
    val description: String,
    val longitude: Double,
    val latitude: Double,
) : Item
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
    val userId: Int,
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
    val tripId: Int
) : Serializable


data class ItineraryDay1(
    val date: Date,
    val tripId: Int
) : Serializable

