package com.example.todolist.data

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.todolist.R
import com.example.todolist.model.Task
import com.example.todolist.model.Topic
import kotlinx.coroutines.runBlocking
import org.junit.Assert.*

import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import java.time.LocalDate

@RunWith(AndroidJUnit4::class)
class TodoDatabaseTaskTest {

    private lateinit var db: TodoDatabase
    private lateinit var taskDao : TaskDao
    private lateinit var topicDao : TopicDao

    @Before
    fun setUp() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        db = Room.inMemoryDatabaseBuilder(context, TodoDatabase::class.java).build()
        taskDao = db.taskDao()
        topicDao = db.topicDao()
    }

    @Test
    fun addValidTaskTest() = runBlocking {
        val sampleTopic = Topic(0, "Sample topic", R.drawable.university_hat_icon)
        topicDao.addTopic(sampleTopic)
        var topicsLD = topicDao.readAllData()
        var topicMap = LiveDataTestUtil.getValue(topicsLD)
        var topicId = 0
        topicMap.keys.forEach { topicId = it.id }
        var tasksLD = taskDao.readAllData()
        val sizeBfAddition = LiveDataTestUtil.getValue(tasksLD).size
        val taskList = listOf<Task>(Task(0, topicId, "Task 1", LocalDate.now(), false, true, LocalDate.now(), null, null), Task(0, topicId, "Task 2", LocalDate.of(2021, 7, 11), true, true, LocalDate.of(2022, 1, 1),
            null, null), Task(0, topicId, "Task 3", LocalDate.of(1965, 9, 17), false, false, LocalDate.of(2001, 8, 27), null, null),
            Task(0, topicId, "Task 4", LocalDate.now(), true, false, LocalDate.of(2009, 2, 13), null, null),
            Task(0, topicId, "Task 5", LocalDate.of(1997, 7, 3), false, false, LocalDate.now(), null, null),
            Task(0, topicId, "Task 6", LocalDate.now(), true, true, LocalDate.of(2023, 7,11), null, null))

        taskList.forEach {
            taskDao.addTask(it)
        }
        tasksLD = taskDao.readAllData()
        val taskListFromDb = LiveDataTestUtil.getValue(tasksLD)
        assertEquals(6, taskListFromDb.size - sizeBfAddition)
        val taskListWithoutId = taskListFromDb.map { task -> Task(0, task.topic, task.name, task.date, task.flag, task.completed, task.dateCreation, task.voiceRecordPath, task.imagePath) }
        assertEquals(true, taskList.containsAll(taskListWithoutId) && taskListWithoutId.containsAll(taskList))
    }
    @After
    fun tearDown() {
        db.close()
    }
}