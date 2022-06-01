package com.example.todolist.data

import android.os.Handler
import android.os.Looper
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
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