package com.example.eventdicoding.data.local

import android.annotation.SuppressLint
import android.content.Context
import androidx.lifecycle.LiveData
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

class FavoriteEventRepository(context: Context) {
    private val favoriteEventDao: FavoriteEventDao
    private val executorService: ExecutorService = Executors.newSingleThreadExecutor()

    init {
        val db = FavoriteEventDatabase.getDatabase(context)
        favoriteEventDao = db.favoriteEventDao()
    }

    fun getAllFavoriteEvent(): LiveData<List<FavoriteEventEntity>> {
        return favoriteEventDao.getAllFavorite()
    }

    fun insertEvent(favoriteEventEntity: FavoriteEventEntity) {
        executorService.execute { favoriteEventDao.insertEvent(favoriteEventEntity) }
    }

    fun delete(favoriteEventEntity: FavoriteEventEntity) {
        executorService.execute { favoriteEventDao.delete(favoriteEventEntity) }
    }

    fun getFavoriteEventById(id: String): LiveData<FavoriteEventEntity> {
        return favoriteEventDao.getFavoriteEventById(id)
    }

    companion object {
        @SuppressLint("StaticFieldLeak")
        @Volatile
        private var instance: FavoriteEventRepository? = null

        fun getInstance(context: Context): FavoriteEventRepository =
            instance ?: synchronized(this) {
                instance ?: FavoriteEventRepository(context)
            }.also { instance = it }
    }
}