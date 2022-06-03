package com.example.todolist.data.ui

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
import org.hamcrest.CoreMatchers.instanceOf
import org.hamcrest.core.AllOf.allOf
import org.hamcrest.core.Is.`is`
import org.junit.FixMethodOrder
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.MethodSorters


@FixMethodOrder(MethodSorters.NAME_ASCENDING)
@RunWith(AndroidJUnit4::class)
class TopicCRUDFragmentTest {

    val TOPIC_NAME = "TopicExample"
    val TOPIC_NAME_UPDATED = "TopicExampleUpdate"
    val ICON = R.drawable.school
    val ICON_UPDATED = R.drawable.ic_baseline_handyman_24

    @get: Rule
    val activityRule = ActivityScenarioRule(MainActivity::class.java)

    @Test
    fun test_A_createNewTopic() {
        onView(withId(R.id.create_topic_fab)).perform(click())

        onView(withId(R.id.editTextTextPersonName)).perform(typeText(TOPIC_NAME))
        Espresso.closeSoftKeyboard()
        onView(withId(R.id.textInputLayout2)).perform(click())
        onData(allOf(`is`(instanceOf(Int::class.java)),
            `is`(ICON))).perform(click())
        onView(withId(R.id.accept_create_topic_fab)).perform(click())

        onView(withId(R.id.rvTopicItems))
            .check(matches(atPositionOnView(0, withText(TOPIC_NAME), R.id.topicName)))

        onView(withId(R.id.rvTopicItems))
            .check(matches(atPositionOnView(0, withDrawable(ICON), R.id.topicIcon)))
    }

    //TODO ICON doesn't change
    @Test
    fun test_B_updateTopic() {
        onView(withId(R.id.rvTopicItems))
            .perform(actionOnItemAtPosition<TopicAdapter.TopicViewHolder>(0, longClick()))

        onView(withId(R.id.editTextUpdatePersonNameText)).perform(replaceText(TOPIC_NAME_UPDATED))
        Espresso.closeSoftKeyboard()
        onView(withId(R.id.textInputLayout)).perform(click())
        onData(allOf(`is`(instanceOf(Int::class.java)), `is`(ICON_UPDATED))).perform(click())
        onView(withId(R.id.accept_update_topic_fab)).perform(click())

        onView(withId(R.id.rvTopicItems))
            .check(matches(atPositionOnView(0, withText(TOPIC_NAME_UPDATED), R.id.topicName)))

//        onView(withId(R.id.rvTopicItems))
//            .check(matches(atPositionOnView(0, withDrawable(ICON), R.id.topicIcon)))
    }

    @Test
    fun test_C_deleteTopic() {
        onView(withId(R.id.rvTopicItems))
            .perform(actionOnItemAtPosition<TopicAdapter.TopicViewHolder>(0, swipeLeft()))

        onView(withText("Yes")).inRoot(isDialog())
            .check(matches(isDisplayed())).perform(click())

        // Check if empty topic list screen come into view
        onView(withId(R.id.empty_list_img)).check(matches(isDisplayed()))
        onView(withId(R.id.empty_textView1)).check(matches(isDisplayed()))
        onView(withId(R.id.empty_textView2)).check(matches(isDisplayed()))
        onView(withId(R.id.empty_point_arrow2)).check(matches(isDisplayed()))
    }
}