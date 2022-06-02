package com.example.todolist.data.ui

import android.app.Application
import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.Espresso.*
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.contrib.RecyclerViewActions.actionOnItemAtPosition
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.todolist.MainActivity
import com.example.todolist.R
import com.example.todolist.data.*
import com.example.todolist.model.Topic
import com.example.todolist.repository.TaskRepository
import com.example.todolist.repository.TopicRepository
import com.example.todolist.topic.TopicAdapter
import com.example.todolist.viewmodel.TopicViewModel
import kotlinx.coroutines.runBlocking
import org.junit.*
import org.junit.runner.RunWith
import java.io.IOException


@RunWith(AndroidJUnit4::class)
class EmptyScreenTest {

    @get: Rule
    val activityRule = ActivityScenarioRule(MainActivity::class.java)

    private lateinit var db: TodoDatabase
    private lateinit var taskDao : TaskDao
    private lateinit var topicDao : TopicDao
    private lateinit var topicRepo: TopicRepository
    private lateinit var taskRepo: TaskRepository
    private lateinit var topicViewModel: TopicViewModel
    private val topic = Topic(0, "University", R.drawable.school)

    @Before
    fun setUp() = runBlocking {
        val context = ApplicationProvider.getApplicationContext<Context>()
        db = Room.inMemoryDatabaseBuilder(context, TodoDatabase::class.java).build()
        taskDao = db.taskDao()
        topicDao = db.topicDao()
        topicRepo = TopicRepository(topicDao)
        taskRepo = TaskRepository(taskDao)
        topicViewModel = TopicViewModel(Application())
    }

    @After
    @Throws(IOException::class)
    fun closeDb() {
        db.close()
    }


    /**
     * Check if empty screen is visible
     */
//    @Ignore("Doesn't work because empty screen has default visibility as GONE and it doesn't change idk why")
    @Test
    fun test_isEmptyScreenVisible_onAppLaunch() {
        onView(withId(R.id.empty_list_img)).check(matches(isDisplayed()))
        onView(withId(R.id.empty_textView1)).check(matches(isDisplayed()))
        onView(withId(R.id.empty_textView2)).check(matches(isDisplayed()))
        onView(withId(R.id.empty_point_arrow2)).check(matches(isDisplayed()))
    }


    /**
     * Check if after selecting topic, empty task list view is showing
     */
//    @Ignore("Doesn't work because empty screen has default visibility as GONE and it doesn't change idk why")
    @Test
    fun test_selectITopic_isTaskInTopicFragmentVisible() {
        addTopic()
        onView(withId(R.id.rvTopicItems))
            .perform(actionOnItemAtPosition<TopicAdapter.TopicViewHolder>(0, click()))

        onView(withId(R.id.emptyTasksImage)).check(matches(isDisplayed()))
        onView(withId(R.id.emptyTasksText)).check(matches(isDisplayed()))
        onView(withId(R.id.empty_point_arrow2_topic)).check(matches(isDisplayed()))
    }

    private fun addTopic() = runBlocking {
        topicViewModel.addTopic(topic)
    }
}