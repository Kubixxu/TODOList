package com.example.todolist.data

import androidx.lifecycle.LiveData
import androidx.room.*
import com.example.todolist.model.Topic

@Dao
interface TopicDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun addTopic(topic: Topic)

    @Update
    suspend fun updateTopic(topic: Topic)

    @Delete
    suspend fun deleteTopic(topic: Topic)

    @MapInfo(valueColumn = "taskCount")
    @Query("SELECT topics.*, COUNT(tasks.id) as taskCount FROM topics LEFT JOIN tasks ON topics.id = tasks.topic GROUP BY topics.id ORDER BY topics.id ASC")
    fun readAllData(): LiveData<Map<Topic, Int>>
}