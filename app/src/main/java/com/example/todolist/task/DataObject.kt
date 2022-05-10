package com.example.todolist.task

import com.example.todolist.model.Task

class DataObject() {
    lateinit var task: Task
    lateinit var header: String
    var isHeader: Boolean = false

    constructor(task: Task) : this() {
        this.task = task
    }

    constructor(header: String) : this() {
        this.header = header
        isHeader = true
    }

}