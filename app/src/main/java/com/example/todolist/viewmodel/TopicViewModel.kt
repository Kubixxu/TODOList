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
import kotlinx.coroutines.withContext

class TopicViewModel(application: Application) : AndroidViewModel(application) {
    val readAllData: LiveData<Map<Topic, Int>>
    private val repository: TopicRepository
    init {
        val topicDao = TodoDatabase.getDatabase(application).topicDao()
        repository = TopicRepository(topicDao)
        readAllData = repository.readAllData
    }

    fun addTopic(topic: Topic)  {
        viewModelScope.launch(Dispatchers.IO) {
            repository.addTopic(topic)
        }
    }

    suspend fun addTopicAsync(topic: Topic) : Long  {
        return repository.addTopic(topic)
    }

    fun updateTopic(topic: Topic) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.updateTopic(topic)
        }
    }

    fun deleteTopic(topic: Topic) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.deleteTopic(topic)
        }
    }

    fun deleteAll() {
        viewModelScope.launch(Dispatchers.IO) {
             repository.deleteAll()
        }
    }

}