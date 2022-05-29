package com.example.todolist.data

import android.content.Context
import android.os.Handler
import android.os.Looper
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.todolist.R
import com.example.todolist.model.Topic
import kotlinx.coroutines.*
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit


object LiveDataTestUtil {
    fun <T> getValue(liveData: LiveData<T>): T {
        val data = arrayOfNulls<Any>(1)
        val latch = CountDownLatch(1)
        val observer = object : Observer<T> {
            override fun onChanged(o: T?) {
                data[0] = o
                latch.countDown()
                liveData.removeObserver(this)
            }
        }
        addObserver(liveData, observer)
        latch.await(2, TimeUnit.SECONDS)
        return data[0] as T
    }
    private fun <T> addObserver(ldQuery: LiveData<T>, observer: Observer<T>) {
        val handler = Handler(Looper.getMainLooper()).post {
            ldQuery.observeForever(observer)
        }
    }
}

@RunWith(AndroidJUnit4::class)
class TodoDatabaseTopicTest {

    private lateinit var db: TodoDatabase
    private lateinit var dao : TopicDao

    @Before
    fun setUp() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        db = Room.inMemoryDatabaseBuilder(context, TodoDatabase::class.java).build()
        dao = db.topicDao()
    }

    @Test
    fun addValidTopicTest() = runBlocking {
            val topic1 = Topic(0, "Some name", R.drawable.university_hat_icon)
            val topic2 = Topic(0, "Another name", R.drawable.ic_baseline_family_restroom_24)
            val topic3 = Topic(0, "Some name", R.drawable.ic_baseline_trending_up_24)
            val topic4 = Topic(0, "Some name", R.drawable.ic_baseline_golf_course_24)
            dao.addTopic(topic1)
            dao.addTopic(topic2)
            dao.addTopic(topic3)
            dao.addTopic(topic4)
            val topicsLD = dao.readAllData()
            val topicMap = LiveDataTestUtil.getValue(topicsLD)
            assertNotNull(topicMap)
            assertEquals(topicMap.keys.size, 4)
            var containsTopic : Boolean
            topicMap.keys.forEach {
                containsTopic = false
                containsTopic = (topic1.name == it.name && it.topicImageId == topic1.topicImageId) || (topic2.name == it.name && it.topicImageId == topic2.topicImageId) ||
                    (topic3.name == it.name && it.topicImageId == topic3.topicImageId) || (topic4.name == it.name && it.topicImageId == topic4.topicImageId)
                assertEquals(true, containsTopic)
            }
    }

    @Test
    fun addInvalidTopicTest() {
        var validTopic : Topic
        var topicExistingId : Topic
        runBlocking {
            validTopic = Topic(1, "Some name", R.drawable.university_hat_icon)
            topicExistingId = Topic(1, "Another name", R.drawable.ic_baseline_family_restroom_24)
            dao.addTopic(validTopic)
            dao.addTopic(topicExistingId)
        }

        GlobalScope.launch {
            val topics = dao.readAllData()
            val topicMap = LiveDataTestUtil.getValue(topics)
            assertNotNull(topicMap)
            assertEquals(topicMap.keys.size, 4)
            assertEquals(true, topicMap.keys.contains(validTopic))
            assertEquals(false, topicMap.keys.contains(topicExistingId))

        }
    }


    @After
    fun closeDb() {
        db.close()
    }
}