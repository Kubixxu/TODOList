package com.example.todolist.task

import android.Manifest
import android.app.Dialog
import android.content.pm.PackageManager
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
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
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.example.todolist.R
import com.example.todolist.databinding.FragmentRecordingBinding
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import java.io.File
import java.util.*

class RecordingFragment : BottomSheetDialogFragment(), Timer.OnTimeTickListener {

    private val audioFileExtension: String = ".mp3"
    private var writePermission = false
    private var readPermission = false
    private var audioPermission = false

    private lateinit var mediaRecorder: MediaRecorder
    private lateinit var mediaPlayer: MediaPlayer
    private var audioDirPath: String? = null
    private lateinit var audioFilePath: String
    private var firstAudioPath: String? = null
    private lateinit var audioFileName: String
    private lateinit var timer: Timer

    private var recording = false
    private var playing = false

    private var _binding: FragmentRecordingBinding? = null
    private lateinit var viewModel: SharedRecordingViewModel

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = super.onCreateDialog(savedInstanceState)

        dialog.setOnShowListener {
            (view?.parent as ViewGroup).background =
                ColorDrawable(Color.TRANSPARENT)
        }
        return dialog
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        checkPermissions()

        audioDirPath = activity?.applicationContext?.getExternalFilesDir(Environment.DIRECTORY_MUSIC)
            .toString()
        timer = Timer(this)

        if (writePermission && audioPermission && readPermission) {
            viewModel = ViewModelProvider(requireActivity())[SharedRecordingViewModel::class.java]
            if (!viewModel.getData().equals("") && viewModel.getData() != null) {
                audioFilePath = viewModel.getData()!!
                firstAudioPath = audioFilePath
                view.findViewById<LinearLayout>(R.id.stopRecordLayout).visibility = View.VISIBLE
                view.findViewById<LinearLayout>(R.id.startRecordLayout).visibility = View.GONE
            } else {
                startRecording()
            }

            view.findViewById<ImageButton>(R.id.stopRecord).setOnClickListener {
                stopRecording()
                view.findViewById<LinearLayout>(R.id.stopRecordLayout).visibility = View.VISIBLE
                view.findViewById<LinearLayout>(R.id.startRecordLayout).visibility = View.GONE
            }

            view.findViewById<ImageButton>(R.id.playRecord).setOnClickListener {
                startPlaying()
            }

            view.findViewById<ImageButton>(R.id.deleteRecord).setOnClickListener {
                if (!firstAudioPath.equals(audioFilePath) && audioFileName != "")
                    if (File(audioFilePath).exists()) File(audioFilePath).delete()
                viewModel.updateData(null)
                audioFilePath = ""
                if (playing) {
                    stopPlaying()
                }
                dismiss()
            }
            view.findViewById<ImageButton>(R.id.saveRecord).setOnClickListener {
                if (firstAudioPath != null && File(firstAudioPath!!).exists())
                    File(firstAudioPath!!).delete()
                firstAudioPath = null
                if (playing) {
                    stopPlaying()
                }
                viewModel.updateData(audioFilePath)
                findNavController().navigateUp()
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        _binding = FragmentRecordingBinding.inflate(inflater, container, false)

        return inflater.inflate(R.layout.fragment_recording, container, false)
    }

    companion object {
        const val REQUEST_PERMISSION_CODE = 11
    }


    @Deprecated("Deprecated in Java")
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        if (grantResults.isNotEmpty()) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED)
                writePermission = true
            if (grantResults[1] == PackageManager.PERMISSION_GRANTED)
                audioPermission = true
            if (grantResults[2] == PackageManager.PERMISSION_GRANTED)
                readPermission = true
        }
    }


    private fun checkPermissions() {
        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED
            && ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.RECORD_AUDIO) == PackageManager.PERMISSION_GRANTED &&
            ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
            writePermission = true
            readPermission = true
            audioPermission = true
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
        audioFileName = UUID.randomUUID().toString() + audioFileExtension
        audioFilePath = "$audioDirPath/$audioFileName"
        try {
            mediaRecorder = MediaRecorder()
            mediaRecorder.setAudioSource(MediaRecorder.AudioSource.DEFAULT)
            mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP)
            mediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB)
            mediaRecorder.setOutputFile(audioFilePath)

            recording = true

            mediaRecorder.prepare()
            mediaRecorder.start()
            timer.start()
            Toast.makeText(requireContext(), "Recording ...", Toast.LENGTH_SHORT).show()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun stopRecording() {
        mediaRecorder.stop()
        mediaRecorder.reset()
        mediaRecorder.release()

        recording = false
        timer.stop()
    }

    private fun startPlaying() {
        try {
            if (!playing) {
                mediaPlayer = MediaPlayer()

                playing = true

                val file = File(audioFilePath)

                val uri = Uri.fromFile(file)
                mediaPlayer = MediaPlayer.create(requireContext(), uri)
                mediaPlayer.start()
                view?.findViewById<ImageButton>(R.id.playRecord)?.setImageResource(R.drawable.ic_pause_foreground)
                view?.findViewById<ImageButton>(R.id.playRecord)?.setOnClickListener {
                    playing()
                }

                mediaPlayer.setOnCompletionListener {
                    view?.findViewById<ImageButton>(R.id.playRecord)?.setImageResource(R.drawable.ic_play_foreground)
                    playing = false
                }

            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun stopPlaying() {
        mediaPlayer.stop()
        mediaPlayer.release()

        playing = false
    }

    private fun playing() {
        if (playing) {
            mediaPlayer.pause()
            playing = false
            view?.findViewById<ImageButton>(R.id.playRecord)?.setImageResource(R.drawable.ic_play_foreground)
        } else {
            mediaPlayer.start()
            playing = true
            view?.findViewById<ImageButton>(R.id.playRecord)?.setImageResource(R.drawable.ic_pause_foreground)
        }
    }

    override fun onTimerTick(duration: String) {
        val amp = mediaRecorder.maxAmplitude.toFloat()
        if (amp > 0f){
            view?.findViewById<WaveformView>(R.id.canvas)?.addAmplitude(amp)
        }
    }
}