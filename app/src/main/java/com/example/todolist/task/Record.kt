package com.example.todolist.task

import android.icu.text.SimpleDateFormat
import android.media.MediaRecorder
import android.os.Environment
import android.os.Looper.prepare
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import kotlinx.coroutines.NonCancellable.start
import java.io.IOException
import java.lang.Exception
import java.util.*

class Record : Fragment() {

//    private lateinit var recorder: MediaRecorder
//    private var dirPath = ""
//    private var filename = ""
//    private var isRecording = false
//    private var isStop = false
//
//
//    fun startRecording(permissionGranted: Boolean, permission: Array<String>) {
//        if (!permissionGranted) {
//            ActivityCompat.requestPermissions(requireActivity(), permission, com.example.todolist.REQUEST_CODE)
//            return
//        } else {
//            recorder = MediaRecorder()
//            dirPath = "C:/Users/annam/Desktop/"
//
//            var simpleDateFormat = SimpleDateFormat("yyyy.MM.DD_hh.mm.ss")
//            var date = simpleDateFormat.format(Date())
//
//            filename = "audio_record_$date"
//            recorder.apply {
//                setAudioSource(MediaRecorder.AudioSource.MIC)
//                setOutputFormat(MediaRecorder.OutputFormat.MPEG_4)
//                setAudioEncoder(MediaRecorder.AudioEncoder.AAC)
//                setOutputFile("$dirPath$filename.mp3")
//                try {
//                    prepare()
//                    start()
//                } catch (e: IOException) {
//                }
//
//
//            }
//
//        }
//    }
//
//    fun stopRecording() {
//        recorder.apply {
//            stop()
//            reset()
//            release()
//        }
////        recorder.reset();
////        recorder.release();
//    }
}