package com.example.todolist.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "topics")
data class Topic (
    @PrimaryKey(autoGenerate = true)
    val id: Int,
    val name: String,
    val topicImageId: Int
    )