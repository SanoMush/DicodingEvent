package com.example.eventdicoding.retrofit

import com.example.eventdicoding.data.response.EventResponse
import retrofit2.http.GET
import retrofit2.http.Query

interface APIService {
    @GET("events")
    suspend fun getEvents(@Query("active") active: Int): EventResponse

    @GET("events")
    suspend fun searchEvents(
        @Query("active") active: Int = -1,
        @Query("q") query: String
    ): EventResponse
}