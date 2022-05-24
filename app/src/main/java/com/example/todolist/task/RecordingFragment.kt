package com.example.todolist.task

import android.Manifest
import android.app.Dialog
import android.content.Context
import android.content.DialogInterface
import android.content.pm.PackageManager
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.media.MediaPlayer
import android.media.MediaRecorder
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.os.VibrationEffect
import android.os.Vibrator
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.ContextCompat.getSystemService
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.example.todolist.R
import com.example.todolist.databinding.FragmentRecordingBinding
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import java.io.File
import java.util.*


// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [RecordingFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class RecordingFragment : BottomSheetDialogFragment(), Timer.OnTimeTickListener {

    private val AUDIO_FILE_EXTENSION: String = ".mp3"
    private var write_permission = false
    private var read_permission = false
    private var audio_permission = false


    private lateinit var media_recorder: MediaRecorder
    private lateinit var media_player: MediaPlayer
    private var audio_dir_path: String? = null
    private lateinit var audio_file_path: String
    private var first_audio_path: String? = null
    private lateinit var audio_file_name: String
    private lateinit var timer: Timer


    private var recording = false
    private var playing = false

    private var _binding: FragmentRecordingBinding? = null
    private val binding get() = _binding!!
    private lateinit var viewModel: SharedRecordingViewModel
    private lateinit var vibrator: Vibrator


    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = super.onCreateDialog(savedInstanceState)

        dialog.setOnShowListener {
            //this line transparent your dialog background
            (view?.parent as ViewGroup).background =
                ColorDrawable(Color.TRANSPARENT)
        }

        return dialog
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        checkPermissions()

        audio_dir_path = getActivity()?.getApplicationContext()?.getExternalFilesDir(Environment.DIRECTORY_MUSIC)
            .toString()
        timer = Timer(this)
//        vibrator = activity?.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
//        vibrator.vibrate(VibrationEffect.createOneShot(50, VibrationEffect.DEFAULT_AMPLITUDE))

        if (write_permission && audio_permission && read_permission) {
            viewModel = ViewModelProvider(requireActivity()).get(SharedRecordingViewModel::class.java)
            if (!viewModel.getData().equals("") && viewModel.getData() != null) {
                audio_file_path = viewModel.getData()!!
                first_audio_path = audio_file_path
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
                if (!first_audio_path.equals(audio_file_path) && audio_file_name != "")
                    if (File(audio_file_path).exists()) File(audio_file_path).delete()
                viewModel.updateData(null)
                audio_file_path = ""
                if (playing) {
                    stopPlaying()
                }
                dismiss()
            }
            view.findViewById<ImageButton>(R.id.saveRecord).setOnClickListener {
                if (first_audio_path != null && File(first_audio_path).exists())
                    File(first_audio_path).delete()
                first_audio_path = null
                if (playing) {
                    stopPlaying()
                }
                viewModel.updateData(audio_file_path)
                findNavController().navigateUp()
            }
        }
    }

//    override fun onCancel(dialog: DialogInterface) {
//        super.onCancel(dialog)
//        if(File(audio_file_path).exists()) File(audio_file_path).delete()
//        viewModel.updateData(null)
//    }

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
        audio_file_name = UUID.randomUUID().toString() + AUDIO_FILE_EXTENSION
        audio_file_path = "$audio_dir_path/$audio_file_name"
        try {
            media_recorder = MediaRecorder()
            media_recorder.setAudioSource(MediaRecorder.AudioSource.DEFAULT)
            media_recorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP)
            media_recorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB)
            media_recorder.setOutputFile(audio_file_path)

            recording = true

            media_recorder.prepare()
            media_recorder.start()
            timer.start()
            Toast.makeText(requireContext(), "Recording ...", Toast.LENGTH_SHORT).show()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun stopRecording() {
        media_recorder.stop()
        media_recorder.reset()
        media_recorder.release()

        recording = false
        timer.stop()
    }

    private fun startPlaying() {
        try {
            if (!playing) {
                media_player = MediaPlayer()

                playing = true

                val file = File(audio_file_path)

                val uri = Uri.fromFile(file)
                media_player = MediaPlayer.create(requireContext(), uri)
                media_player.start()
                view?.findViewById<ImageButton>(R.id.playRecord)?.setImageResource(R.drawable.ic_pause_foreground)
                view?.findViewById<ImageButton>(R.id.playRecord)?.setOnClickListener {
                    playing()
                }

                media_player.setOnCompletionListener {
                    view?.findViewById<ImageButton>(R.id.playRecord)?.setImageResource(R.drawable.ic_play_foreground)
                    playing = false
                }

            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun stopPlaying() {
        media_player.stop()
        media_player.release()

        playing = false
    }

    private fun playing() {
        if (playing) {
            media_player.pause()
            playing = false
            view?.findViewById<ImageButton>(R.id.playRecord)?.setImageResource(R.drawable.ic_play_foreground)
        } else {
            media_player.start()
            playing = true
            view?.findViewById<ImageButton>(R.id.playRecord)?.setImageResource(R.drawable.ic_pause_foreground)
        }
    }

    override fun onTimerTick(duration: String) {
        val amp = media_recorder.maxAmplitude.toFloat()
        if (amp > 0f){
            view?.findViewById<WaveformView>(R.id.canvas)?.addAmplitude(amp)
        }
    }
}