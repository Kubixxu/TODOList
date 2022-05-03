package com.example.todolist.data

import androidx.lifecycle.LiveData

class TopicRepository(private val topicDao: TopicDao) {

    val readAllData: LiveData<List<Topic>> = topicDao.readAllData()

    suspend fun addTopic(topic: Topic) {
        topicDao.addTopic(topic)
    }
}