//package com.example.todolist.task
//
//import android.Manifest
//import android.content.pm.PackageManager
//import android.media.MediaPlayer
//import android.media.MediaRecorder
//import android.os.Build
//import android.os.Bundle
//import android.os.Environment
//import android.view.LayoutInflater
//import android.view.View
//import android.view.ViewGroup
//import android.widget.*
//import androidx.annotation.RequiresApi
//import androidx.constraintlayout.widget.ConstraintSet
//import androidx.core.app.ActivityCompat
//import androidx.core.content.ContextCompat
//import androidx.fragment.app.Fragment
//import com.example.todolist.MainActivity
//import com.example.todolist.R
//import com.example.todolist.databinding.TaskFormBinding
//import java.io.File
//import java.io.IOException
//
//class VoiceRecording : Fragment() {
//    private var permission = arrayOf(android.Manifest.permission.RECORD_AUDIO)
//    private var permissionGranted = false
////    private var media_recorder: MediaRecorder = MediaRecorder()
//    private var dirPath = ""
//    private var filename = ""
//    private var isRecording = false
//    private var isStop = false
//    private var write_permission = false
//    private var audio_permission = false
//
//
//    private lateinit var media_recorder: MediaRecorder
//    private lateinit var media_player: MediaPlayer
//    private var audio_dir_path: String? = null
//    private var audio_file_path: String? = null
//    private var audio_file_name: String = "audio1.3gp"
//
//
//    private var recording = false
//    private var playing = false
//
//    private var time = 1
//
//    private var startLayout: LinearLayout? = null
//    private var stopLayout: LinearLayout? = null
//    private var stop_btn: Button? = null
//
//    private var play_btn: Button? = null
//    private var delete_btn_1: Button? = null
//    private var save_btn: Button? = null
//
//    companion object {
//        val REQUEST_PERMISSION_CODE = 11
//    }
//
//    override fun onCreateView(
//        inflater: LayoutInflater,
//        container: ViewGroup?,
//        savedInstanceState: Bundle?
//    ): View? {
//
//        _binding = TaskFormBinding.inflate(inflater, container, false)
//
//        startLayout = view?.findViewById(R.id.startRecordLayout)
//        startLayout?.visibility = View.VISIBLE
//        stopLayout = view?.findViewById(R.id.stopRecordLayout)
//        stop_btn = view?.findViewById(R.id.stopRecord)
//        play_btn = view?.findViewById(R.id.playRecord)
//        delete_btn_1 = view?.findViewById(R.id.deleteRecord)
//        save_btn = view?.findViewById(R.id.saveRecord)
//        print("mdmd")
//            checkPermissions()
//            if (write_permission && audio_permission) {
//
//                audio_dir_path = getActivity()?.getApplicationContext()?.getExternalFilesDir(Environment.DIRECTORY_MUSIC)
//                    .toString()
//                audio_file_path = "$audio_dir_path/$audio_file_name"
//
//
//                stop_btn!!.setOnClickListener { _ ->
//                    startRecording()
//                    stopLayout?.visibility = View.VISIBLE
//                    startLayout?.visibility = View.GONE
//                }
//
//                startRecording()
//
//                play_btn!!.setOnClickListener { _ ->
////                    startPlaying()
//                }
//
////                pause_btn_2!!.setOnClickListener {
//////                    pausePlaying()
////
////                    stop_btn_2!!.isEnabled = !playing
////                }
//
//            }
//        return super.onCreateView(inflater, container, savedInstanceState)
//    }
//
//    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
//        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
//
//        if (grantResults.isNotEmpty()) {
//            if (grantResults[0] == PackageManager.PERMISSION_GRANTED)
//                write_permission = true
//            if (grantResults[1] == PackageManager.PERMISSION_GRANTED)
//                audio_permission = true
//        }
//    }
//
//
//    private fun checkPermissions() {
//        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED
//            && ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.RECORD_AUDIO) == PackageManager.PERMISSION_GRANTED) {
//            write_permission = true
//            audio_permission = true
//        }
//        else
//            ActivityCompat.requestPermissions(requireActivity(), arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.RECORD_AUDIO),
//                VoiceRecording.REQUEST_PERMISSION_CODE
//            )
//    }
//
//
//    private fun startRecording() {
//            println("djdj")
//        try {
//            media_recorder = MediaRecorder()
//            media_recorder.setAudioSource(MediaRecorder.AudioSource.DEFAULT)
//            media_recorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP)
//            media_recorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB)
//            media_recorder.setOutputFile(audio_file_path)
//
//            recording = true
//
//            media_recorder.prepare()
//            media_recorder.start()
//            Toast.makeText(requireContext(), "Rozpoczęto nagrywanie", Toast.LENGTH_SHORT).show()
//        } catch (e: Exception) {
//            e.printStackTrace()
//        }
//    }
//
//    private fun stopRecording() {
//        media_recorder.stop()
//        media_recorder.release()
//
//        recording = false
//
//        Toast.makeText(requireContext(), "Zakończono nagrywanie", Toast.LENGTH_SHORT).show()
//    }
//}
//
//
//
//
//
//
//
////package com.example.cw_8b
////
////import android.Manifest
////import android.media.MediaPlayer
////import androidx.appcompat.app.AppCompatActivity
////import androidx.core.content.ContextCompat
////import java.lang.Exception
////import kotlinx.coroutines.CoroutineScope
////import kotlinx.coroutines.Dispatchers.IO
////import kotlinx.coroutines.Dispatchers.Main
////import kotlinx.coroutines.delay
////import kotlinx.coroutines.launch
////
////class SoundActivity : AppCompatActivity() {
////    private lateinit var media_recorder: MediaRecorder
////    private lateinit var media_player: MediaPlayer
////    private var audio_dir_path: String? = null
////    private var audio_file_path: String? = null
////    private var audio_file_name: String = "audio1.3gp"
////
////
////    private var recording = false
////    private var playing = false
////
////    private var time = 1
////
////    private var start_btn_1: Button? = null
////    private var pause_btn_1: Button? = null
////    private var stop_btn_1: Button? = null
////
////    private var start_btn_2: Button? = null
////    private var pause_btn_2: Button? = null
////    private var stop_btn_2: Button? = null
////
////    private var text_1: TextView? = null
////    private var progressbar_1: ProgressBar? = null
////
////    companion object {
////        val REQUEST_PERMISSION_CODE = 11
////    }
////
////    private fun startRecording() {
////        try {
////            media_recorder = MediaRecorder()
////            media_recorder.setAudioSource(MediaRecorder.AudioSource.DEFAULT)
////            media_recorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP)
////            media_recorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB)
////            media_recorder.setOutputFile(audio_file_path)
////
////            recording = true
////
////            media_recorder.prepare()
////            media_recorder.start()
////            Toast.makeText(this, "Rozpoczęto nagrywanie", Toast.LENGTH_SHORT).show()
////        } catch (e: Exception) {
////            e.printStackTrace()
////        }
////    }
////
////    private fun loop() {
////        CoroutineScope(IO).launch {
////            delay(1000)
////            CoroutineScope(Main).launch{
////                setTime(time)
////                time += 1
////                if (playing)
////                    loop()
////            }
////        }
////    }
////
////    private fun setTime(time: Int) {
////        val seconds = time - time / 60 * time
////        val minutes = time / 60
////        var seconds_string = seconds.toString()
////        var minutes_string = minutes.toString()
////        if (seconds < 10)
////            seconds_string = "0$seconds"
////        if (minutes < 10)
////            minutes_string = "0$minutes"
////        if (playing) {
////            text_1!!.text = getString(R.string.soundac_timer, minutes_string, seconds_string)
////            progressbar_1!!.max = media_player.duration
////            progressbar_1!!.progress = media_player.currentPosition
////        }
////    }
////
////
////
////    private fun pauseRecording() {
////        if (recording) {
////            media_recorder.resume()
////            recording = false
////            pause_btn_1!!.text = getString(R.string.pause)
////            Toast.makeText(this, "Wznowiono nagrywanie", Toast.LENGTH_SHORT).show()
////        } else {
////            media_recorder.pause()
////            recording = true
////            pause_btn_1!!.text = getString(R.string.resume)
////            Toast.makeText(this, "Wstrzymano nagrywanie", Toast.LENGTH_SHORT).show()
////        }
////    }
////
////    private fun stopRecording() {
////        media_recorder.stop()
////        media_recorder.release()
////
////        recording = false
////
////        Toast.makeText(this, "Zakończono nagrywanie", Toast.LENGTH_SHORT).show()
////    }
////
////    private fun startPlaying() {
////        try {
////            media_player = MediaPlayer()
////            media_player.setDataSource(audio_file_path)
////            media_player.prepareAsync()
////
////            playing = true
////
////            media_player.setOnPreparedListener {
////                media_player.start()
////                loop()
////            }
////
////            media_player.setOnCompletionListener { _ ->
////                stopPlaying()
////                start_btn_2!!.isEnabled = true
////                pause_btn_2!!.isEnabled = false
////                stop_btn_2!!.isEnabled = false
////            }
////
////            Toast.makeText(this, "Rozpoczęto odtwarzanie", Toast.LENGTH_SHORT).show()
////        } catch (e: Exception) {
////            e.printStackTrace()
////        }
////    }
////
////    private fun pausePlaying() {
////        if (playing) {
////            media_player.start()
////            playing = false
////            pause_btn_2!!.text = getString(R.string.pause)
////            loop()
////            Toast.makeText(this, "Wznowiono odtwarzanie", Toast.LENGTH_SHORT).show()
////        } else {
////            media_player.pause()
////            playing = true
////            pause_btn_2!!.text = getString(R.string.resume)
////            Toast.makeText(this, "Wstrzymano odtwarzanie", Toast.LENGTH_SHORT).show()
////        }
////    }
////
////    private fun stopPlaying() {
////        setTime(0)
////        time = 1
////        progressbar_1!!.progress = 0
////
////        media_player.stop()
////        media_player.release()
////
////        playing = false
////
////        Toast.makeText(this, "Zakończono odtwarzanie", Toast.LENGTH_SHORT).show()
////    }
////
////    private fun checkPermissions() {
////        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED
////            && ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO) == PackageManager.PERMISSION_GRANTED) {
////            write_permission = true
////            audio_permission = true
////        }
////        else
////            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.RECORD_AUDIO), REQUEST_PERMISSION_CODE)
////    }
////
////    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
////        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
////
////        if (grantResults.isNotEmpty()) {
////            if (grantResults[0] == PackageManager.PERMISSION_GRANTED)
////                write_permission = true
////            if (grantResults[1] == PackageManager.PERMISSION_GRANTED)
////                audio_permission = true
////        }
////
////        if (write_permission && audio_permission) {
////            start_btn_1!!.isEnabled = true
////            pause_btn_1!!.isEnabled = false
////            stop_btn_1!!.isEnabled = false
////
////            start_btn_2!!.isEnabled = false
////            pause_btn_2!!.isEnabled = false
////            stop_btn_2!!.isEnabled = false
////        } else {
////            start_btn_1!!.isEnabled = false
////            pause_btn_1!!.isEnabled = false
////            stop_btn_1!!.isEnabled = false
////
////            start_btn_2!!.isEnabled = false
////            pause_btn_2!!.isEnabled = false
////            stop_btn_2!!.isEnabled = false
////        }
////    }
////}