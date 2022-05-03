package com.example.todolist.repository

import androidx.lifecycle.LiveData
import com.example.todolist.data.TopicDao
import com.example.todolist.model.Topic

class TopicRepository(private val topicDao: TopicDao) {

    val readAllData: LiveData<List<Topic>> = topicDao.readAllData()

    suspend fun addTopic(topic: Topic) {
        topicDao.addTopic(topic)
    }

    suspend fun updateTopic(topic: Topic) {
        topicDao.updateTopic(topic)
    }
}