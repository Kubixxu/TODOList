package com.example.todolist.task

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class SharedRecordingViewModel : ViewModel() {
    val audio_path = MutableLiveData<String?>()
    fun updateData(data: String?) {
        audio_path.value = data
    }
    fun getData(): String? {
        return audio_path.value
    }
}