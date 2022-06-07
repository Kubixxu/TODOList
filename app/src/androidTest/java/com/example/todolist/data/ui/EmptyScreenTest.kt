package com.example.todolist.data.ui

import android.app.Application
import androidx.test.espresso.Espresso.*
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.contrib.RecyclerViewActions.actionOnItemAtPosition
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.todolist.MainActivity
import com.example.todolist.R
import com.example.todolist.model.Topic
import com.example.todolist.topic.TopicAdapter
import com.example.todolist.viewmodel.TaskViewModel
import com.example.todolist.viewmodel.TopicViewModel
import kotlinx.coroutines.runBlocking
import org.junit.*
import org.junit.runner.RunWith


@RunWith(AndroidJUnit4::class)
class EmptyScreenTest {

    @get: Rule
    val activityRule = ActivityScenarioRule(MainActivity::class.java)


    private val topic = Topic(0, "University", R.drawable.school)
    private var position: Int = -1
    private lateinit var topicViewModel: TopicViewModel
    private lateinit var taskViewModel: TaskViewModel
    private var topicId = -1

    @Before
    fun setUp() = runBlocking {
        topicViewModel = TopicViewModel(Application())
        taskViewModel = TaskViewModel(Application())
        position = topicViewModel.getTopicCount()
    }

    @After
    fun closeDb() {
        topicViewModel.deleteTopicById(topicId)
    }


    /**
     * Check if empty screen is visible
     */
    @Test
    fun test_isEmptyScreenVisible_onAppLaunch() {
        if (position == 0) {
            onView(withId(R.id.empty_list_img)).check(matches(isDisplayed()))
            onView(withId(R.id.empty_textView1)).check(matches(isDisplayed()))
            onView(withId(R.id.empty_textView2)).check(matches(isDisplayed()))
            onView(withId(R.id.empty_point_arrow2)).check(matches(isDisplayed()))
        } else {
            onView(withId(R.id.rvTopicItems)).check(matches(isDisplayed()))
        }
    }


    /**
     * Check if after selecting topic, empty task list view is showing
     */
    @Test
    fun test_selectITopic_isTaskInTopicFragmentVisible() {
        addTopic()
        onView(withId(R.id.rvTopicItems))
            .perform(actionOnItemAtPosition<TopicAdapter.TopicViewHolder>(position, click()))

        onView(withId(R.id.emptyTasksImage)).check(matches(isDisplayed()))
        onView(withId(R.id.emptyTasksText)).check(matches(isDisplayed()))
        onView(withId(R.id.empty_point_arrow2_topic)).check(matches(isDisplayed()))
    }

    private fun addTopic() = runBlocking {
        topicId = topicViewModel.addTopicAsync(topic).toInt()
    }
}