package com.example.eventdicoding.data.local

import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.parcelize.Parcelize

@Entity(tableName = "favorite_events")
@Parcelize
data class FavoriteEventEntity(
    @PrimaryKey val id: String,
    val name: String,
    val ownerName: String,
    val beginTime: String,
    val quota: Int,
    val registrants: Int,
    val description: String,
    val imageLogo: String,
    val link: String
) : Parcelable