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
            val topic3 = Topic(0, "Third name", R.drawable.ic_baseline_trending_up_24)
            val topic4 = Topic(0, "Fourth name", R.drawable.ic_baseline_golf_course_24)
            var topicsLD = dao.readAllData()
            var topicMap = LiveDataTestUtil.getValue(topicsLD)
            val sizeBfAddition = topicMap.keys.size
            dao.addTopic(topic1)
            dao.addTopic(topic2)
            dao.addTopic(topic3)
            dao.addTopic(topic4)
            topicsLD = dao.readAllData()
            topicMap = LiveDataTestUtil.getValue(topicsLD)
            assertNotNull(topicMap)
            assertEquals(4, topicMap.keys.size - sizeBfAddition)
            var containsTopic : Boolean
            topicMap.keys.forEach {
                containsTopic = false
                containsTopic = (topic1.name == it.name && it.topicImageId == topic1.topicImageId) || (topic2.name == it.name && it.topicImageId == topic2.topicImageId) ||
                    (topic3.name == it.name && it.topicImageId == topic3.topicImageId) || (topic4.name == it.name && it.topicImageId == topic4.topicImageId)
                assertEquals(true, containsTopic)
            }
    }

    @Test
    fun addDoubledTopicTest() = runBlocking {

        val validTopic = Topic(1, "Some name", R.drawable.university_hat_icon)
        val doubledIdTopic = Topic(1, "Another name", R.drawable.university_hat_icon)
        var topicsLD = dao.readAllData()
        var topicMap = LiveDataTestUtil.getValue(topicsLD)
        val sizeBfAddition = topicMap.keys.size
        dao.addTopic(validTopic)
        dao.addTopic(doubledIdTopic)
        topicsLD = dao.readAllData()
        topicMap = LiveDataTestUtil.getValue(topicsLD)
        assertNotNull(topicMap)
        assertEquals(1, topicMap.keys.size - sizeBfAddition)
    }


    @Test
    fun updateValidTopicTest() = runBlocking {
        val topic1 = Topic(0, "Some name", R.drawable.university_hat_icon)
        val topic2 = Topic(0, "Strange name", R.drawable.ic_baseline_family_restroom_24)
        var topicsLD = dao.readAllData()
        var topicMap = LiveDataTestUtil.getValue(topicsLD)
        val sizeBfAddition = topicMap.keys.size
        dao.addTopic(topic1)
        dao.addTopic(topic2)
        topicsLD = dao.readAllData()
        topicMap = LiveDataTestUtil.getValue(topicsLD)
        topicMap.keys.forEach {
            val updatedTopic = Topic(it.id, "New name", R.drawable.ic_baseline_trending_up_24)
            dao.updateTopic(updatedTopic)
        }
        topicsLD = dao.readAllData()
        topicMap = LiveDataTestUtil.getValue(topicsLD)
        assertEquals(2, topicMap.keys.size - sizeBfAddition)
        var containsTopic : Boolean
        topicMap.keys.forEach {
            containsTopic = it.name == "New name" && it.topicImageId == R.drawable.ic_baseline_trending_up_24
            assertEquals(true, containsTopic)
        }
    }

    @Test
    fun updateNonexistentTopicTest() = runBlocking {
        var topicsLD = dao.readAllData()
        var topicMap = LiveDataTestUtil.getValue(topicsLD)
        val sizeBfAddition = topicMap.keys.size
        dao.updateTopic(Topic(-2424, "Nice topic", R.drawable.image_add_icon_customized))
        topicsLD = dao.readAllData()
        topicMap = LiveDataTestUtil.getValue(topicsLD)
        assertEquals(0, topicMap.keys.size - sizeBfAddition)
    }

    @Test
    fun deleteValidTopicTest() = runBlocking {
        val topic1 = Topic(0, "Some name", R.drawable.university_hat_icon)
        val topic2 = Topic(0, "Another name", R.drawable.ic_baseline_family_restroom_24)
        val topic3 = Topic(0, "Third name", R.drawable.ic_baseline_trending_up_24)
        val topic4 = Topic(0, "Fourth name", R.drawable.ic_baseline_golf_course_24)
        dao.addTopic(topic1)
        dao.addTopic(topic2)
        dao.addTopic(topic3)
        dao.addTopic(topic4)
        var topicsLD = dao.readAllData()
        var topicMap = LiveDataTestUtil.getValue(topicsLD)
        val sizeBfDeletion = topicMap.keys.size
        //assertEquals(4, topicMap.keys.size - sizeBfAddition)
        topicMap.keys.forEach {
            dao.deleteTopic(it)
        }
        topicsLD = dao.readAllData()
        topicMap = LiveDataTestUtil.getValue(topicsLD)
        assertEquals(-4, topicMap.keys.size - sizeBfDeletion)
    }

    @Test
    fun deleteInvalidTopicTest() = runBlocking {
        val topic1 = Topic(0, "Some name", R.drawable.university_hat_icon)
        val topic2 = Topic(0, "Another name", R.drawable.ic_baseline_family_restroom_24)
        val topic3 = Topic(0, "Third name", R.drawable.ic_baseline_trending_up_24)
        val topic4 = Topic(0, "Fourth name", R.drawable.ic_baseline_golf_course_24)
        dao.addTopic(topic1)
        dao.addTopic(topic2)
        dao.addTopic(topic3)
        dao.addTopic(topic4)
        var topicsLD = dao.readAllData()
        var topicMap = LiveDataTestUtil.getValue(topicsLD)
        val sizeBfDeletion = topicMap.keys.size
        //assertEquals(4, topicMap.keys.size - sizeBfAddition)
        topicMap.keys.forEach {
            dao.deleteTopic(Topic(-532, it.name, it.topicImageId))
        }
        topicsLD = dao.readAllData()
        topicMap = LiveDataTestUtil.getValue(topicsLD)
        assertEquals(0, topicMap.keys.size - sizeBfDeletion)
    }




    @After
    fun closeDb() {
        db.close()
    }
}