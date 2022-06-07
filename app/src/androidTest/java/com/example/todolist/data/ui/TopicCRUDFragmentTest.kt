package com.example.todolist.data.ui

import android.app.Application
import androidx.test.espresso.Espresso
import androidx.test.espresso.Espresso.onData
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.*
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.contrib.RecyclerViewActions.actionOnItemAtPosition
import androidx.test.espresso.matcher.RootMatchers.isDialog
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.todolist.MainActivity
import com.example.todolist.R
import com.example.todolist.data.ui.utils.EspressoTestsMatchers.withDrawable
import com.example.todolist.data.ui.utils.RecyclerViewMatcher.atPositionOnView
import com.example.todolist.topic.TopicAdapter
import com.example.todolist.viewmodel.TopicViewModel
import kotlinx.coroutines.runBlocking
import org.hamcrest.CoreMatchers.instanceOf
import org.hamcrest.CoreMatchers.not
import org.hamcrest.core.AllOf.allOf
import org.hamcrest.core.Is.`is`
import org.junit.Before
import org.junit.FixMethodOrder
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.MethodSorters


@FixMethodOrder(MethodSorters.NAME_ASCENDING)
@RunWith(AndroidJUnit4::class)
class TopicCRUDFragmentTest {

    private val TOPIC_NAME = "TopicExample"
    private val TOPIC_NAME_UPDATED = "TopicExampleUpdate"
    private val ICON = R.drawable.school
    private val ICON_UPDATED = R.drawable.ic_baseline_handyman_24

    @get: Rule
    val activityRule = ActivityScenarioRule(MainActivity::class.java)

    private var position: Int = -1

    @Before
    fun setUp() = runBlocking {
        val topicViewModel = TopicViewModel(Application())
        position = topicViewModel.getTopicCount()
    }

    @Test
    fun test_A_createNewTopic_validation() {
        onView(withId(R.id.create_topic_fab)).perform(click())

        onView(withId(R.id.accept_create_topic_fab)).perform(click())

        onView(withId(R.id.addTopic)).check(matches(isDisplayed()))
        onView(withId(R.id.topicNameErrorTV)).check(matches(isDisplayed()))
        onView(withId(R.id.topicIconErrorTV)).check(matches(isDisplayed()))
    }


    /**
     * Check if create topic form comes into view after click FAB
     * Check if input data and create button work
     * Check if new topic was added successfully with properly data
     */
    @Test
    fun test_B_createNewTopic() {
        onView(withId(R.id.create_topic_fab)).perform(click())

        onView(withId(R.id.editTextTextPersonName)).perform(typeText(TOPIC_NAME))
        Espresso.closeSoftKeyboard()
        onView(withId(R.id.textInputLayout2)).perform(click())
        onData(allOf(`is`(instanceOf(Int::class.java)),
            `is`(ICON))).perform(click())
        onView(withId(R.id.accept_create_topic_fab)).perform(click())

        onView(withId(R.id.rvTopicItems))
            .check(matches(atPositionOnView(position, withText(TOPIC_NAME), R.id.topicName)))

        onView(withId(R.id.rvTopicItems))
            .check(matches(atPositionOnView(position, withDrawable(ICON), R.id.topicIcon)))
    }


    /**
     * Check if update topic form comes into view after long click
     * Check if input data and update button work
     * Check if new topic was updated successfully with properly data
     */
    @Test
    fun test_C_updateTopic() {
        onView(withId(R.id.rvTopicItems))
            .perform(actionOnItemAtPosition<TopicAdapter.TopicViewHolder>((position - 1), longClick()))

        onView(withId(R.id.editTextUpdatePersonNameText)).perform(replaceText(TOPIC_NAME_UPDATED))
        Espresso.closeSoftKeyboard()
        onView(withId(R.id.textInputLayout)).perform(click())
        onData(allOf(`is`(instanceOf(Int::class.java)), `is`(ICON_UPDATED))).perform(click())
        onView(withId(R.id.accept_update_topic_fab)).perform(click())

        onView(withId(R.id.rvTopicItems))
            .check(matches(atPositionOnView(position - 1, withText(TOPIC_NAME_UPDATED), R.id.topicName)))
    }

    /**
     * Check if alert dialog about deleting task is showing on swipe left
     * Check if press "No" then topic is not deleted
     */
    @Test
    fun test_D_notDeleteTopic() {
        onView(withId(R.id.rvTopicItems))
            .perform(actionOnItemAtPosition<TopicAdapter.TopicViewHolder>
                (position - 1, swipeLeft()))

        onView(withText("Are you sure you want to delete topic ${TOPIC_NAME_UPDATED}?"))
            .check(matches(isDisplayed()))

        onView(withText("No")).inRoot(isDialog())
            .check(matches(isDisplayed())).perform(click())

        onView(withId(R.id.rvTopicItems))
            .check(matches(atPositionOnView(position - 1,
                withText(TOPIC_NAME_UPDATED), R.id.topicName)))

    }

    /**
     * Check if alert dialog about deleting task is showing on swipe left
     * Check if press "Yes" delete topic
     * Check if topic is not showing
     */
    @Test
    fun test_E_deleteTopic() {
        onView(withId(R.id.rvTopicItems))
            .perform(actionOnItemAtPosition<TopicAdapter.TopicViewHolder>
                ((position - 1), swipeLeft()))

        onView(withText("Are you sure you want to delete topic ${TOPIC_NAME_UPDATED}?"))
            .check(matches(isDisplayed()))

        onView(withText("Yes")).inRoot(isDialog())
            .check(matches(isDisplayed())).perform(click())

        // Check if topic is not in view
        onView(withId(R.id.rvTopicItems))
            .check(matches(not(atPositionOnView(position - 1,
                withText(TOPIC_NAME_UPDATED), R.id.topicName))))
    }
}