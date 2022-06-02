package com.example.todolist.data

import androidx.lifecycle.LiveData
import androidx.room.*
import com.example.todolist.model.Task

@Dao
interface TaskDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun addTask(task: Task) : Long

    @Update
    suspend fun updateTask(task: Task)

    @Delete
    suspend fun deleteTask(task: Task)


    @Query("SELECT * FROM tasks WHERE topic == :topicId ORDER BY id ASC")
    fun readTasksFromTopic(topicId: Int): LiveData<List<Task>>

    @Query("SELECT * FROM tasks ORDER BY id ASC")
    fun readAllData(): LiveData<List<Task>>
}