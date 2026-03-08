package com.example.eventdicoding.vmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.eventdicoding.data.response.EventResponse
import com.example.eventdicoding.data.response.ListEventsItem
import com.example.eventdicoding.retrofit.APIConfig
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.net.SocketTimeoutException
import java.net.UnknownHostException

class MainViewModel : ViewModel() {
    //    Menyimpan data event yang sedang berlangsung (upcoming).

    private val _activeEvents = MutableLiveData<List<ListEventsItem>>()
    val activeEvents: LiveData<List<ListEventsItem>> = _activeEvents

    //    Menyimpan data event yang sudah selesai (finished).
    private val _finishedEvents = MutableLiveData<List<ListEventsItem>>()
    val finishedEvents: LiveData<List<ListEventsItem>> = _finishedEvents

    //    Menyimpan hasil pencarian event.
    private val _searchResults = MutableLiveData<List<ListEventsItem>>()
    val searchResults: LiveData<List<ListEventsItem>> = _searchResults

    //    Menunjukkan apakah sedang dalam proses loading data.
    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    //    Menyimpan pesan error jika terjadi kesalahan.
    private val _errorMessage = MutableLiveData<String?>()
    val errorMessage: LiveData<String?> = _errorMessage

    init {
        fetchEvents(1) // Mengambil upcoming events (active = 1)
        fetchEvents(0) // Mengambil finished events (active = 0)
    }


    //    Mengambil data event dari API berdasarkan parameter active.
    fun fetchEvents(active: Int) {
        _isLoading.value = true
        _errorMessage.value = null
        val client = APIConfig.create().getEvents(active) // Simpan client di variabel
        client.enqueue(object : Callback<EventResponse> {
            override fun onResponse(call: Call<EventResponse>, response: Response<EventResponse>) {
                _isLoading.value = false
                if (response.isSuccessful) {
                    when (active) {
                        1 -> _activeEvents.value = response.body()?.listEvents ?: emptyList()
                        0 -> _finishedEvents.value = response.body()?.listEvents ?: emptyList()
                    }
                } else {
                    _errorMessage.value = "Terjadi kesalahan: ${response.message()}"
                }
            }

            override fun onFailure(call: Call<EventResponse>, t: Throwable) {
                _isLoading.value = false
                handleError(client, t)
            }
        })
    }


    //    Melakukan pencarian event berdasarkan query.
    fun searchEvents(query: String) {
        _isLoading.value = true
        _errorMessage.value = null
        val client = APIConfig.create().searchEvents(active = -1, query = query) // Simpan client di variabel
        client.enqueue(object : Callback<EventResponse> { // Panggil enqueue() langsung pada client
            override fun onResponse(call: Call<EventResponse>, response: Response<EventResponse>) {
                _isLoading.value = false
                if (response.isSuccessful) {
                    _searchResults.value = response.body()?.listEvents ?: emptyList()
                } else {
                    _errorMessage.value = "Gagal mencari event"
                }
            }

            override fun onFailure(call: Call<EventResponse>, t: Throwable) {
                _isLoading.value = false
                handleError(client, t)
            }
        })
    }

    //    Menangani error yang terjadi saat mengambil data dari API.
    private fun handleError(call: Call<EventResponse>, t: Throwable) {
        try {
            val response = call.execute() // Eksekusi call untuk mendapatkan response
            val message = when {
                !response.isSuccessful && response.code() == 404 -> "Data tidak ditemukan"
                !response.isSuccessful && response.code() == 500 -> "Server sedang bermasalah"
                t is UnknownHostException -> "Maaf, internet Anda lemot atau mati"
                t is SocketTimeoutException -> "Koneksi internet Anda terlalu lambat"
                else -> "Terjadi kesalahan: ${t.localizedMessage}"
            }
            _errorMessage.value = message
        } catch (e: Exception) {
            _errorMessage.value = "Terjadi kesalahan: ${e.localizedMessage}"
            Log.e("MainViewModel", "handleError: ${e.message}")
        }
        Log.e("MainViewModel", "onFailure: ${t.message}")
    }
}