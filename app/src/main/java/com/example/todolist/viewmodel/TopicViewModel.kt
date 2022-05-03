package com.example.todolist.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import com.example.todolist.data.TodoDatabase
import com.example.todolist.repository.TopicRepository
import com.example.todolist.model.Topic
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class TopicViewModel(application: Application) : AndroidViewModel(application) {
    val readAllData: LiveData<List<Topic>>
    private val repository: TopicRepository
    init {
        val topicDao = TodoDatabase.getDatabase(application).topicDao()
        repository = TopicRepository(topicDao)
        readAllData = repository.readAllData
    }

    fun addTopic(topic: Topic) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.addTopic(topic)
        }
    }

    fun updateTopic(topic: Topic) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.updateTopic(topic)
        }
    }
}