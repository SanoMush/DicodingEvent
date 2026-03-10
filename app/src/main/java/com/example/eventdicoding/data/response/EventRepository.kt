package com.example.eventdicoding.data.response

import android.util.Log
import com.example.eventdicoding.retrofit.APIService
import java.net.SocketTimeoutException
import java.net.UnknownHostException

class EventRepository private constructor(private val apiService: APIService) {

    suspend fun getEvents(active: Int): Result<List<ListEventsItem>> {
        return try {
            val response = apiService.getEvents(active)
            Result.Success(response.listEvents)
        } catch (e: Exception) {
            Result.Error(handleException(e))
        }
    }

    suspend fun searchEvents(query: String): Result<List<ListEventsItem>> {
        return try {
            val response = apiService.searchEvents(active = -1, query = query)
            Result.Success(response.listEvents)
        } catch (e: Exception) {
            Result.Error(handleException(e))
        }
    }

    private fun handleException(e: Exception): String {
        return when (e) {
            is UnknownHostException -> "Maaf, tidak ada koneksi internet."
            is SocketTimeoutException -> "Koneksi internet Anda terlalu lambat."
            else -> "Terjadi kesalahan: ${e.localizedMessage}"
        }
    }

    companion object {
        @Volatile
        private var instance: EventRepository? = null
        fun getInstance(apiService: APIService): EventRepository =
            instance ?: synchronized(this) {
                instance ?: EventRepository(apiService).also { instance = it }
            }
    }
}