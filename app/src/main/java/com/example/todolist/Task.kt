package com.example.todolist

import java.time.LocalDate
import java.util.*

data class Task (
    val topicName: String,
    var name: String,
    var date: LocalDate,
    var flag: Boolean,
    var completed: Boolean
)