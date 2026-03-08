package com.example.eventdicoding.vmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.eventdicoding.data.response.EventRepository
import com.example.eventdicoding.data.response.Result
import com.example.eventdicoding.data.response.ListEventsItem
import kotlinx.coroutines.launch

class MainViewModel(private val repository: EventRepository) : ViewModel() {

    private val _activeEvents = MutableLiveData<Result<List<ListEventsItem>>>()
    val activeEvents: LiveData<Result<List<ListEventsItem>>> = _activeEvents


    private val _finishedEvents = MutableLiveData<Result<List<ListEventsItem>>>()
    val finishedEvents: LiveData<Result<List<ListEventsItem>>> = _finishedEvents

    private val _searchResults = MutableLiveData<Result<List<ListEventsItem>>>()
    val searchResults: LiveData<Result<List<ListEventsItem>>> = _searchResults

    init {
        fetchEvents(1)
        fetchEvents(0)
    }

    fun fetchEvents(active: Int) {
        viewModelScope.launch {
            if (active == 1) {
                _activeEvents.value = Result.Loading
                _activeEvents.value = repository.getEvents(active)
            } else {
                _finishedEvents.value = Result.Loading
                _finishedEvents.value = repository.getEvents(active)
            }
        }
    }

    fun searchEvents(query: String) {
        viewModelScope.launch {
            _searchResults.value = Result.Loading
            _searchResults.value = repository.searchEvents(query)
        }
    }
}