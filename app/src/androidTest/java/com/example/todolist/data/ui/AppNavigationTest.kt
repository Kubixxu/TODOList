package com.example.todolist.data.ui

import android.app.Application
import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.Espresso.*
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.action.ViewActions.longClick
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.contrib.RecyclerViewActions.actionOnItemAtPosition
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.todolist.MainActivity
import com.example.todolist.R
import com.example.todolist.data.*
import com.example.todolist.model.Task
import com.example.todolist.model.Topic
import com.example.todolist.repository.TaskRepository
import com.example.todolist.repository.TopicRepository
import com.example.todolist.topic.TopicAdapter
import com.example.todolist.viewmodel.TaskViewModel
import com.example.todolist.viewmodel.TopicViewModel
import kotlinx.coroutines.runBlocking
import org.junit.*
import org.junit.runner.RunWith
import org.mockito.Mockito
import java.time.LocalDate


@RunWith(AndroidJUnit4::class)
class AppNavigationTest {

    private val FIRST_TASK_NAME : String = "FirstTask"
    private val SECOND_TASK_NAME : String = "SecondTask"

    @get: Rule
    val activityRule = ActivityScenarioRule(MainActivity::class.java)

    private val topics = arrayOf(
        Topic(0, "University", R.drawable.school),
        Topic(0, "Car", R.drawable.ic_baseline_handyman_24),
    )

    private lateinit var db: TodoDatabase
    private lateinit var taskDao : TaskDao
    private lateinit var topicDao : TopicDao
    private lateinit var topicRepo: TopicRepository
    private lateinit var taskRepo: TaskRepository
    private lateinit var topicViewModel: TopicViewModel
    private lateinit var taskViewModel: TaskViewModel
    private var topicIds: MutableList<Long> = emptyList<Long>().toMutableList()
    private var tasks: MutableList<Task> = emptyList<Task>().toMutableList()

    @Before
    fun setUp() = runBlocking {
        val context = ApplicationProvider.getApplicationContext<Context>()
        db = Room.inMemoryDatabaseBuilder(context, TodoDatabase::class.java).build()
        taskDao = db.taskDao()
        topicDao = db.topicDao()
        topicRepo = TopicRepository(topicDao)
        taskRepo = TaskRepository(taskDao)
        val applicationMock = Mockito.mock(Application::class.java)
        topicViewModel = TopicViewModel(applicationMock)
        taskViewModel = TaskViewModel(applicationMock)
        addTopicsAndTask()
    }

    @After
    fun closeDb() {
        topicViewModel.deleteAll()
        db.close()
    }


    /**
     * Check if topicList comes into view
     */
    @Test
    fun test_isTopicsListVisible_withExistingTopics() {
        onView(withId(R.id.rvTopicItems)).check(matches(isDisplayed()))
    }


    /**
     * Check if add topic form comes into view after click FAB
     */
    @Test
    fun test_isCreateTopicFormDisplay() {
        onView(withId(R.id.create_topic_fab)).perform(click())
        onView(withId(R.id.addTopic)).check(matches(isDisplayed()))
    }

    @Test
    fun test_backNavigation_toTopicListItemFromCreate() {
        onView(withId(R.id.create_topic_fab)).perform(click())
        onView(withId(R.id.addTopic)).check(matches(isDisplayed()))

        pressBack()

        onView(withId(R.id.rvTopicItems)).check(matches(isDisplayed()))
    }

    /**
     * Check if update topic form comes into view after long click
     */
    @Test
    fun test_isUpdateTopicFormDisplay() {
        onView(withId(R.id.rvTopicItems))
            .perform(actionOnItemAtPosition<TopicAdapter.TopicViewHolder>(0, longClick()))

        onView(withId(R.id.updateTopic)).check(matches(isDisplayed()))
    }

    @Test
    fun test_backNavigation_toTopicListItemFromUpdate() {
        onView(withId(R.id.rvTopicItems))
            .perform(actionOnItemAtPosition<TopicAdapter.TopicViewHolder>(0, longClick()))

        pressBack()

        onView(withId(R.id.rvTopicItems)).check(matches(isDisplayed()))
    }

    /**
     * Check if taskList comes into view after select topic
     */
    @Test
    fun test_topicItem_validateTasksList() {
        onView(withId(R.id.rvTopicItems))

        onView(withId(R.id.rvTopicItems))
            .perform(actionOnItemAtPosition<TopicAdapter.TopicViewHolder>(0, click()))

        onView(withId(R.id.tasksList)).check(matches(isDisplayed()))
    }

    @Test
    fun test_backNavigation_toTopicListItem() {
        onView(withId(R.id.rvTopicItems))
            .perform(actionOnItemAtPosition<TopicAdapter.TopicViewHolder>(0, click()))

        pressBack()

        onView(withId(R.id.rvTopicItems)).check(matches(isDisplayed()))
    }

    /**
     * Check if add topic form comes into view after click FAB
     */
    @Test
    fun test_isCreateTaskFormDisplay() {
        onView(withId(R.id.rvTopicItems))
            .perform(actionOnItemAtPosition<TopicAdapter.TopicViewHolder>(0, click()))
        onView(withId(R.id.fabAddTask)).perform(click())
        onView(withId(R.id.taskForm)).check(matches(isDisplayed()))
    }

    @Test
    fun test_backNavigation_toTaskListItemFromCreate() {
        onView(withId(R.id.rvTopicItems))
            .perform(actionOnItemAtPosition<TopicAdapter.TopicViewHolder>(0, click()))
        onView(withId(R.id.fabAddTask)).perform(click())

        pressBack()

        onView(withId(R.id.rvTopicItems)).check(matches(isDisplayed()))
    }

    /**
     * Check if update task form comes into view after long click
     */
    @Test
    fun test_isUpdateTaskFormDisplay() {
        onView(withId(R.id.rvTopicItems))
            .perform(actionOnItemAtPosition<TopicAdapter.TopicViewHolder>(0, click()))
        onView(withId(R.id.tasksList))
            .perform(actionOnItemAtPosition<TopicAdapter.TopicViewHolder>(0, longClick()))

        onView(withId(R.id.taskForm)).check(matches(isDisplayed()))
    }

    @Test
    fun test_backNavigation_toTaskListItemFromUpdate() {
        onView(withId(R.id.rvTopicItems))
            .perform(actionOnItemAtPosition<TopicAdapter.TopicViewHolder>(0, click()))
        onView(withId(R.id.tasksList))
            .perform(actionOnItemAtPosition<TopicAdapter.TopicViewHolder>(0, longClick()))

        pressBack()

        onView(withId(R.id.rvTopicItems)).check(matches(isDisplayed()))
    }

    private fun addTopicsAndTask() = runBlocking {
        for (topic in topics) {
            val id = topicViewModel.addTopicAsync(topic)
            topicIds.add(id)
            if (id != 0L) {
                val task1 = Task(0, id.toInt(), FIRST_TASK_NAME, LocalDate.now(), flag = true,
                    false, LocalDate.now(), null, null)
                val task2 = Task(0, id.toInt(), SECOND_TASK_NAME, null, flag = false,
                    false, LocalDate.now(), null, null)
                tasks.add(task1)
                taskViewModel.addTask(task1)
                tasks.add(task2)
                taskViewModel.addTask(task2)
            }
        }
    }
}

//Context jest wspólny
//Fail na iconach
//Testy validacji?