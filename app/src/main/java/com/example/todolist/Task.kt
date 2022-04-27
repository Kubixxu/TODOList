package com.example.todolist

import java.util.*

data class Task (
    val topicName: String,
    val name: String,
    val date: Date,
    val priority: Int,
    var completed: Boolean
)