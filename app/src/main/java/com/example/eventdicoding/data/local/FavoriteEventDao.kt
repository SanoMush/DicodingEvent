package com.example.eventdicoding.data.local

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface FavoriteEventDao {


    @Query("SELECT * FROM favorite_events ORDER BY id ASC")
    fun getAllFavorite(): LiveData<List<FavoriteEventEntity>>

    @Query("SELECT * FROM favorite_events WHERE id = :id")
    fun getFavoriteEventById(id: String): LiveData<FavoriteEventEntity>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insertEvent(favoriteEventEntity: FavoriteEventEntity)

    @Delete
    fun delete(favoriteEventEntity: FavoriteEventEntity)
}