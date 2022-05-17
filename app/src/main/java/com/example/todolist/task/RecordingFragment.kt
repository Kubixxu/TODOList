package com.example.todolist.task

import android.Manifest
import android.content.pm.PackageManager
import android.media.MediaPlayer
import android.media.MediaRecorder
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.example.todolist.R
import com.example.todolist.databinding.FragmentRecordingBinding
import java.io.File


// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [RecordingFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class RecordingFragment : Fragment() {

    private var permission = arrayOf(android.Manifest.permission.RECORD_AUDIO)
    private var permissionGranted = false
    //    private var media_recorder: MediaRecorder = MediaRecorder()
    private var dirPath = ""
    private var filename = ""
    private var isRecording = false
    private var isStop = false
    private var write_permission = false
    private var read_permission = false
    private var audio_permission = false


    private lateinit var media_recorder: MediaRecorder
    private lateinit var media_player: MediaPlayer
    private var audio_dir_path: String? = null
    private var audio_file_path: String? = null
    private var audio_file_name: String = "audio1.3gp"


    private var recording = false
    private var playing = false

    private var time = 1

    private var _binding: FragmentRecordingBinding? = null
    private val binding get() = _binding!!

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        checkPermissions()
        if (write_permission && audio_permission && read_permission) {

            audio_dir_path = getActivity()?.getApplicationContext()?.getExternalFilesDir(Environment.DIRECTORY_MUSIC)
                .toString()
            audio_file_path = "$audio_dir_path/$audio_file_name"

            startRecording()


            view.findViewById<ImageButton>(R.id.stopRecord).setOnClickListener {
                stopRecording()
                view.findViewById<LinearLayout>(R.id.stopRecordLayout).visibility = View.VISIBLE
                view.findViewById<LinearLayout>(R.id.startRecordLayout).visibility = View.GONE
            }

            view.findViewById<ImageButton>(R.id.playRecord).setOnClickListener {
                startPlaying()
            }

//            binding.stopRecord.setOnClickListener {
//                println("HEEEJ")
//            }

//            startRecording()

//            binding.playRecord!!.setOnClickListener { _ ->
////                    startPlaying()
//            }

//                pause_btn_2!!.setOnClickListener {
////                    pausePlaying()
//
//                    stop_btn_2!!.isEnabled = !playing
//                }

        }
        // Inflate the layout for this fragment

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        _binding = FragmentRecordingBinding.inflate(inflater, container, false)



        return inflater.inflate(R.layout.fragment_recording, container, false)
    }

    companion object {
        val REQUEST_PERMISSION_CODE = 11
    }


    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        if (grantResults.isNotEmpty()) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED)
                write_permission = true
            if (grantResults[1] == PackageManager.PERMISSION_GRANTED)
                audio_permission = true
            if (grantResults[2] == PackageManager.PERMISSION_GRANTED)
                read_permission = true
        }
    }


    private fun checkPermissions() {
        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED
            && ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.RECORD_AUDIO) == PackageManager.PERMISSION_GRANTED &&
            ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
            write_permission = true
            read_permission = true
            audio_permission = true
        }
        else {
            ActivityCompat.requestPermissions(
                requireActivity(),
                arrayOf(
                    Manifest.permission.WRITE_EXTERNAL_STORAGE,
                    Manifest.permission.RECORD_AUDIO,
                    Manifest.permission.READ_EXTERNAL_STORAGE
                ),
                REQUEST_PERMISSION_CODE
            )
            checkPermissions()
        }
    }


    private fun startRecording() {
        println("djdj")
        println(audio_file_path)
        try {
            media_recorder = MediaRecorder()
            media_recorder.setAudioSource(MediaRecorder.AudioSource.DEFAULT)
            media_recorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP)
            media_recorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB)
            media_recorder.setOutputFile(audio_file_path)

            recording = true

            media_recorder.prepare()
            media_recorder.start()
            Toast.makeText(requireContext(), "Rozpoczęto nagrywanie", Toast.LENGTH_SHORT).show()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun stopRecording() {
        media_recorder.stop();     // stop recording
        media_recorder.reset();    // set state to idle
        media_recorder.release();  // release resources back to the system

        recording = false

        Toast.makeText(requireContext(), "Zakończono nagrywanie", Toast.LENGTH_SHORT).show()
    }

    private fun startPlaying() {
        try {
            media_player = MediaPlayer()

            playing = true

            val file = File(audio_file_path)

            if (file.exists()) {
                println(file.canRead())
                val uri = Uri.fromFile(file)
                media_player = MediaPlayer.create(requireContext(), uri)
                media_player.setVolume(100F, 100F)
                media_player.start()
            }

            Toast.makeText(requireContext(), "Rozpoczęto odtwarzanie", Toast.LENGTH_SHORT).show()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}