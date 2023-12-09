package com.example.project.models


data class PlacesResponse(
    val results: List<Place>,
    // Add other fields as needed
)
data class Place(
    val name: String,
    val address: String,
    val geometry: Geometry,
    val icon: String,
    val photos: List<Photo>,
    val place_id: String,
    val rating: Double,
    val types: List<String>,
    val user_ratings_total: Int,
    // Add other fields as needed
)

data class Geometry(
    val location: GeoLocation,
    val viewport: Viewport
    // Add other fields as needed
)

data class GeoLocation(
    val lat: Double,
    val lng: Double
    // Add other fields as needed
)

data class Viewport(
    val northeast: GeoLocation,
    val southwest: GeoLocation
    // Add other fields as needed
)

data class Photo(
    val height: Int,
    val html_attributions: List<String>,
    val photo_reference: String,
    val width: Int
    // Add other fields as needed
)


