package com.example.todolist.data

import android.content.Context
import android.database.sqlite.SQLiteConstraintException
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.todolist.R
import com.example.todolist.model.Task
import com.example.todolist.model.Topic
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.*
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
            Task(0, topicId, "Task 4", null, true, false, LocalDate.of(2009, 2, 13), null, null),
            Task(0, topicId, "Task 5", LocalDate.of(1997, 7, 3), false, false, LocalDate.now(), null, null),
            Task(0, topicId, "Task 6", LocalDate.now(), true, true, LocalDate.of(2023, 7,11), null, null))

        taskList.forEach {
            taskDao.addTask(it)
        }
        tasksLD = taskDao.readAllData()
        val taskListFromDb = LiveDataTestUtil.getValue(tasksLD)
        assertEquals(6, taskListFromDb.size - sizeBfAddition)
        topicsLD = topicDao.readAllData()
        topicMap = LiveDataTestUtil.getValue(topicsLD)
        topicMap.values.forEach {
            assertEquals(6, it)
        }
        val taskListWithoutId = taskListFromDb.map { task -> Task(0, task.topic, task.name, task.date, task.flag, task.completed, task.dateCreation, task.voiceRecordPath, task.imagePath) }
        assertEquals(true, taskList.containsAll(taskListWithoutId) && taskListWithoutId.containsAll(taskList))
    }

    @Test(expected = SQLiteConstraintException::class)
    fun addTaskNonexistentTopicTest() = runBlocking {

        var tasksLD = taskDao.readAllData()
        val sizeBfAddition = LiveDataTestUtil.getValue(tasksLD).size
        taskDao.addTask(Task(0, -12, "Simple name", LocalDate.now(), true, true, LocalDate.now(), null, null))

    }

    /*@Test
    fun addTaskTooLongVoicePathTest() = runBlocking {
        val sampleTopic = Topic(1, "Some name", R.drawable.university_hat_icon)
        topicDao.addTopic(sampleTopic)
        taskDao.addTask(Task(0, 1, "Simple name", LocalDate.now(), true, true, LocalDate.now(),
            "Zq/wT44Kjp3kvKBgHKuY2hBaXNkkCCU1mM7gCDTrltQFizjDStYwlApu8QClc3RV140b37f93lFpDn0Nea6mazbEbP7Dw9vSSYXoOYHVP5s0/fjPJSbklqS8otpZOU1ECG0Yy3Z8hDN/OS15/pbcB99pI3Xh3X4yJ3oZKcjTwMnODjS4ZkLp5eKg/u7LXjYFXz.hXnhFMG5W6L3t3cgxGTRjA67ouLyTydz.q9b6EB39dth5epMborBtCN9t4LUN5FBemQsEym5Ew8.36K/ZLqGci7ERM8oAhCjPPXv6kahm",
            null))
        val taskList = LiveDataTestUtil.getValue(taskDao.readAllData())
        assertEquals(true, taskList[0].voiceRecordPath == "Zq/wT44Kjp3kvKBgHKuY2hBaXNkkCCU1mM7gCDTrltQFizjDStYwlApu8QClc3RV140b37f93lFpDn0Nea6mazbEbP7Dw9vSSYXoOYHVP5s0/fjPJSbklqS8otpZOU1ECG0Yy3Z8hDN/OS15/pbcB99pI3Xh3X4yJ3oZKcjTwMnODjS4ZkLp5eKg/u7LXjYFXz.hXnhFMG5W6L3t3cgxGTRjA67ouLyTydz.q9b6EB39dth5epMborBtCN9t4LUN5FBemQsEym5Ew8.36K/ZLqGci7ERM8oAhCjPPXv6kahm")
    }*/
    /*@Test
    fun addTaskTooLongImagePathTest() = runBlocking {
        val sampleTopic = Topic(1, "Some name", R.drawable.university_hat_icon)
        topicDao.addTopic(sampleTopic)
        taskDao.addTask(Task(0, 1, "Simple name", LocalDate.now(), true, true, LocalDate.now(),
            null,
            "Zq/wT44Kjp3kvKBgHKuY2hBaXNkkCCU1mM7gCDTrltQFizjDStYwlApu8QClc3RV140b37f93lFpDn0Nea6mazbEbP7Dw9vSSYXoOYHVP5s0/fjPJSbklqS8otpZOU1ECG0Yy3Z8hDN/OS15/pbcB99pI3Xh3X4yJ3oZKcjTwMnODjS4ZkLp5eKg/u7LXjYFXz.hXnhFMG5W6L3t3cgxGTRjA67ouLyTydz.q9b6EB39dth5epMborBtCN9t4LUN5FBemQsEym5Ew8.36K/ZLqGci7ERM8oAhCjPPXv6kahm"))
        val taskList = LiveDataTestUtil.getValue(taskDao.readAllData())
        assertEquals(true, taskList[0].imagePath == "Zq/wT44Kjp3kvKBgHKuY2hBaXNkkCCU1mM7gCDTrltQFizjDStYwlApu8QClc3RV140b37f93lFpDn0Nea6mazbEbP7Dw9vSSYXoOYHVP5s0/fjPJSbklqS8otpZOU1ECG0Yy3Z8hDN/OS15/pbcB99pI3Xh3X4yJ3oZKcjTwMnODjS4ZkLp5eKg/u7LXjYFXz.hXnhFMG5W6L3t3cgxGTRjA67ouLyTydz.q9b6EB39dth5epMborBtCN9t4LUN5FBemQsEym5Ew8.36K/ZLqGci7ERM8oAhCjPPXv6kahm")
    }*/

    @Test
    fun addDoubledTaskTest() = runBlocking {
        val sampleTopic = Topic(1, "Some name", R.drawable.university_hat_icon)
        topicDao.addTopic(sampleTopic)
        var taskLD = taskDao.readAllData()
        var taskList = LiveDataTestUtil.getValue(taskLD)
        val sizeBfAddition = taskList.size
        val task1 = Task(10, 1, "Name 1", null, false, false, LocalDate.now(), null, null)
        val task2 = Task(10, 1, "Name 2", LocalDate.now(), false, false, LocalDate.now(), null, null)
        taskDao.addTask(task1)
        taskDao.addTask(task2)
        taskLD = taskDao.readAllData()
        taskList = LiveDataTestUtil.getValue(taskLD)
        assertEquals(1, taskList.size - sizeBfAddition)
        assertEquals(task1, taskList[0])
    }

    @Test
    fun updateValidTaskTest() = runBlocking {
        val sampleTopic = Topic(0, "Sample topic", R.drawable.university_hat_icon)
        topicDao.addTopic(sampleTopic)
        var topicsLD = topicDao.readAllData()
        var topicMap = LiveDataTestUtil.getValue(topicsLD)
        var topicId = 0
        topicMap.keys.forEach { topicId = it.id }
        val taskList = listOf<Task>(Task(0, topicId, "Task 1", LocalDate.now(), false, true, LocalDate.now(), null, null), Task(0, topicId, "Task 2", LocalDate.of(2021, 7, 11), true, true, LocalDate.of(2022, 1, 1),
            null, null), Task(0, topicId, "Task 3", LocalDate.of(1965, 9, 17), false, false, LocalDate.of(2001, 8, 27), null, null),
            Task(0, topicId, "Task 4", null, true, false, LocalDate.of(2009, 2, 13), null, null),
            Task(0, topicId, "Task 5", LocalDate.of(1997, 7, 3), false, false, LocalDate.now(), null, null),
            Task(0, topicId, "Task 6", LocalDate.now(), true, true, LocalDate.of(2023, 7,11), null, null))

        taskList.forEach {
            taskDao.addTask(it)
        }
        var tasksLD = taskDao.readAllData()
        val taskListDB = LiveDataTestUtil.getValue(tasksLD)

        taskListDB.forEach {
            taskDao.updateTask(Task(it.id, it.topic, "Name changed", null, true, false, LocalDate.of(2021, 9, 25), null, null))
        }
        tasksLD = taskDao.readAllData()
        val tasksAfterUpdate = LiveDataTestUtil.getValue(tasksLD)
        val result = tasksAfterUpdate.foldRight(true) { task, acc ->
            acc && task.topic == topicId && task.name == "Name changed" && task.date == null && task.flag && !task.completed && task.dateCreation == LocalDate.of(2021, 9, 25) && task.voiceRecordPath == null && task.imagePath == null
        }
        assertEquals(true, result)
    }

    @Test
    fun updateNonexistentTopicTest() = runBlocking {
        val sampleTopic = Topic(1, "Sample topic", R.drawable.university_hat_icon)
        topicDao.addTopic(sampleTopic)

        val task = Task(0, sampleTopic.id, "Name", null, true, false, LocalDate.of(2021, 9, 25), null, null)
        taskDao.addTask(task)
        //val sizeBfAddition = taskList.size
        taskDao.updateTask(Task(0, -234, "Name changed", null, true, false, LocalDate.of(2021, 9, 25), null, null))
        var taskLD = taskDao.readAllData()
        var taskList = LiveDataTestUtil.getValue(taskLD)
        assertEquals(true, taskList[0].name == "Name")
    }

    @Test
    fun updateNonexistentTaskTest() = runBlocking {
        val sampleTopic = Topic(1, "Sample topic", R.drawable.university_hat_icon)
        topicDao.addTopic(sampleTopic)
        var tasksLD = taskDao.readAllData()
        val taskListDB = LiveDataTestUtil.getValue(tasksLD)
        val sizeBfAddition = taskListDB.size
        taskDao.updateTask(Task(-90, sampleTopic.id, "Name changed", null, true, false, LocalDate.of(2021, 9, 25), null, null))
        var taskLD = taskDao.readAllData()
        var taskList = LiveDataTestUtil.getValue(taskLD)
        assertEquals(0, taskList.size - sizeBfAddition)
    }

    @Test
    fun deleteValidTaskTest() = runBlocking {
        val sampleTopic = Topic(0, "Sample topic", R.drawable.university_hat_icon)
        topicDao.addTopic(sampleTopic)
        var topicsLD = topicDao.readAllData()
        var topicMap = LiveDataTestUtil.getValue(topicsLD)
        var topicId = 0
        topicMap.keys.forEach { topicId = it.id }
        val taskList = listOf<Task>(Task(0, topicId, "Task 1", LocalDate.now(), false, true, LocalDate.now(), null, null), Task(0, topicId, "Task 2", LocalDate.of(2021, 7, 11), true, true, LocalDate.of(2022, 1, 1),
            null, null), Task(0, topicId, "Task 3", LocalDate.of(1965, 9, 17), false, false, LocalDate.of(2001, 8, 27), null, null),
            Task(0, topicId, "Task 4", null, true, false, LocalDate.of(2009, 2, 13), null, null),
            Task(0, topicId, "Task 5", LocalDate.of(1997, 7, 3), false, false, LocalDate.now(), null, null),
            Task(0, topicId, "Task 6", LocalDate.now(), true, true, LocalDate.of(2023, 7,11), null, null))

        taskList.forEach {
            taskDao.addTask(it)
        }

        var tasksLD = taskDao.readAllData()
        val taskListFromDb = LiveDataTestUtil.getValue(tasksLD)
        val sizeBfDeletion = taskListFromDb.size
        taskListFromDb.forEach {
            taskDao.deleteTask(it)
        }
        tasksLD = taskDao.readAllData()
        val sizeAfterDeletion = LiveDataTestUtil.getValue(tasksLD).size
        assertEquals(-6, sizeAfterDeletion - sizeBfDeletion)
    }

    @Test
    fun cascadeDeleteTest() = runBlocking {
        val sampleTopic = Topic(0, "Sample topic", R.drawable.university_hat_icon)
        topicDao.addTopic(sampleTopic)
        var topicsLD = topicDao.readAllData()
        var topicMap = LiveDataTestUtil.getValue(topicsLD)
        var topicId = 0
        topicMap.keys.forEach { topicId = it.id }
        val taskList = listOf<Task>(Task(0, topicId, "Task 1", LocalDate.now(), false, true, LocalDate.now(), null, null), Task(0, topicId, "Task 2", LocalDate.of(2021, 7, 11), true, true, LocalDate.of(2022, 1, 1),
            null, null), Task(0, topicId, "Task 3", LocalDate.of(1965, 9, 17), false, false, LocalDate.of(2001, 8, 27), null, null),
            Task(0, topicId, "Task 4", null, true, false, LocalDate.of(2009, 2, 13), null, null),
            Task(0, topicId, "Task 5", LocalDate.of(1997, 7, 3), false, false, LocalDate.now(), null, null),
            Task(0, topicId, "Task 6", LocalDate.now(), true, true, LocalDate.of(2023, 7,11), null, null))

        taskList.forEach {
            taskDao.addTask(it)
        }

        var tasksLD = taskDao.readAllData()
        val taskListFromDb = LiveDataTestUtil.getValue(tasksLD)
        val sizeBfDeletion = taskListFromDb.size
        topicMap.keys.forEach {
            topicDao.deleteTopic(it)
        }
        tasksLD = taskDao.readAllData()
        val sizeAfterDeletion = LiveDataTestUtil.getValue(tasksLD).size
        assertEquals(-6, sizeAfterDeletion - sizeBfDeletion)
    }

    @Test
    fun deleteInvalidTaskTest() = runBlocking {
        val sampleTopic = Topic(0, "Sample topic", R.drawable.university_hat_icon)
        topicDao.addTopic(sampleTopic)
        var topicsLD = topicDao.readAllData()
        var topicMap = LiveDataTestUtil.getValue(topicsLD)
        var topicId = 0
        topicMap.keys.forEach { topicId = it.id }
        val sampleTask = Task(1, topicId, "Sample name", LocalDate.now(), false, true, LocalDate.now(), null, null)
        taskDao.addTask(sampleTask)
        val sizeBfDeletion = LiveDataTestUtil.getValue(taskDao.readAllData()).size
        taskDao.deleteTask(Task(45, 67, "Sample name", LocalDate.now(), false, true, LocalDate.now(), null, null))
        val sizeAftDeletion = LiveDataTestUtil.getValue(taskDao.readAllData()).size
        assertEquals(0, sizeAftDeletion - sizeBfDeletion)
    }


    @After
    fun tearDown() {
        db.close()
    }

}