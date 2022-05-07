package com.example.todolist.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import com.example.todolist.data.TodoDatabase
import com.example.todolist.model.Task
import com.example.todolist.model.Topic
import com.example.todolist.repository.TaskRepository
import com.example.todolist.repository.TopicRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class TaskViewModel(application: Application) : AndroidViewModel(application) {
    val readAllData: LiveData<List<Task>>
    private val repository: TaskRepository
    init {
        val taskDao = TodoDatabase.getDatabase(application).taskDao()
        repository = TaskRepository(taskDao)
        readAllData = repository.readAllData
    }

    fun readTasksFromTopic(topicId: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.readTasksFromTopic(topicId)
        }
    }

    fun addTask(task: Task) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.addTask(task)
        }
    }

    fun updateTask(task: Task) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.updateTask(task)
        }
    }

    fun deleteTask(task: Task) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.deleteTask(task)
        }
    }
}