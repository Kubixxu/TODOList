package com.example.todolist

import java.time.LocalDate
import java.util.*

data class Task (
    val topicName: String,
    val name: String,
    val date: LocalDate,
    val flag: Boolean,
    var completed: Boolean
)