package com.example.todolist.task

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class SharedRecordingViewModel : ViewModel() {
    val audioPath = MutableLiveData<String?>()
    fun updateData(data: String?) {
        audioPath.value = data
    }
    fun getData(): String? {
        return audioPath.value
    }
}