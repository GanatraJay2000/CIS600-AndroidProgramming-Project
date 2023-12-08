package com.example.project.models

import java.io.Serializable

data class Location(
    val id: Int,
    val title: String,
    val imageUrl: String,
    val description: String
): Serializable

val dummyLocations = listOf(
    Location(
        id = 1,
        title = "Japan",
        imageUrl = "https://images.unsplash.com/photo-1528360983277-13d401cdc186?q=80&w=2070&auto=format&fit=crop&ixlib=rb-4.0.3&ixid=M3wxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8fA%3D%3D",
        description = "Immerse yourself in the rich culture and history of Japan, from ancient temples to modern cities."
    ),
    Location(
        id = 2,
        title = "Paris",
        imageUrl = "https://images.unsplash.com/photo-1502602898657-3e91760cbb34?q=80&w=2073&auto=format&fit=crop&ixlib=rb-4.0.3&ixid=M3wxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8fA%3D%3D", // Replace with actual URLs
        description = "Discover the enchanting streets of Paris, from the Eiffel Tower to the charming cafes."
    ),
    Location(
        id = 3,
        title = "New Zealand",
        imageUrl = "https://images.unsplash.com/photo-1507699622108-4be3abd695ad?q=80&w=2071&auto=format&fit=crop&ixlib=rb-4.0.3&ixid=M3wxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8fA%3D%3D",
        description = "Experience the breathtaking landscapes and thrilling adventures in New Zealand."
    ),
    Location(
        id = 4,
        title = "Kenya",
        imageUrl = "https://images.unsplash.com/photo-1519659528534-7fd733a832a0?q=80&w=1926&auto=format&fit=crop&ixlib=rb-4.0.3&ixid=M3wxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8fA%3D%3D",
        description = "Get close to wildlife with an unforgettable safari experience in the heart of Kenya."
    ),
    Location(
        id = 5,
        title = "Maldives",
        imageUrl = "https://images.unsplash.com/photo-1590523741831-ab7e8b8f9c7f?q=80&w=1974&auto=format&fit=crop&ixlib=rb-4.0.3&ixid=M3wxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8fA%3D%3D",
        description = "Relax in the pristine beaches and crystal-clear waters of the Maldives."
    )
)