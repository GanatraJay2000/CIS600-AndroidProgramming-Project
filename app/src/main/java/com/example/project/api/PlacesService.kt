package com.example.project.api

import com.example.project.models.PlacesResponse
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface PlacesService {
    @GET("place/textsearch/json")
    fun searchPlaces(
        @Query("query") query: String,
        @Query("language") language: String,
        @Query("fields") fields: String,
        @Query("key") apiKey: String
    ): Call<PlacesResponse>
}