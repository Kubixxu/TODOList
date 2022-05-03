package com.example.todolist.model

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.android.parcel.Parcelize

@Parcelize
@Entity(tableName = "topics")
data class Topic (
    @PrimaryKey(autoGenerate = true)
    val id: Int,
    val name: String,
    val topicImageId: Int
    ) : Parcelable