package com.example.todolist.repository

import androidx.lifecycle.LiveData
import com.example.todolist.data.TaskDao
import com.example.todolist.data.TopicDao
import com.example.todolist.model.Task
import com.example.todolist.model.Topic

class TaskRepository(private val taskDao: TaskDao) {
    val readAllData: LiveData<List<Task>> = taskDao.readAllData()

    fun readTasksFromTopic(topicId: Int) {
        taskDao.readTasksFromTopic(topicId)
    }

    suspend fun addTask(task: Task) {
        taskDao.addTask(task)
    }

    suspend fun updateTask(task: Task) {
        taskDao.updateTask(task)
    }

    suspend fun deleteTask(task: Task) {
        taskDao.deleteTask(task)
    }
}