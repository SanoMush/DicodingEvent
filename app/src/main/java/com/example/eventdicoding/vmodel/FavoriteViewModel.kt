package com.example.eventdicoding.vmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import com.example.eventdicoding.data.local.FavoriteEventEntity
import com.example.eventdicoding.data.local.FavoriteEventRepository

class FavoriteViewModel(application: Application) : AndroidViewModel(application) {

    private val mFavoriteEventRepository: FavoriteEventRepository =
        FavoriteEventRepository.getInstance(application)

    fun getAllFavoriteEvents(): LiveData<List<FavoriteEventEntity>> {
        return mFavoriteEventRepository.getAllFavoriteEvent()
    }
}