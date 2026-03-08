package com.example.eventdicoding.retrofit

import retrofit2.Call
import com.example.eventdicoding.data.response.EventResponse
import retrofit2.http.GET
import retrofit2.http.Query

interface APIService {
    @GET("events")
    fun getEvents(
        @Query("active") active: Int
    ): Call<EventResponse>

    @GET("events")
    fun searchEvents(
        @Query("active") active: Int = -1,
        @Query("q") query: String
    ): Call<EventResponse>
}