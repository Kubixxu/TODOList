package com.example.todolist.task

import android.os.Build
import android.app.Activity.RESULT_OK
import android.content.Context
import android.content.ContextWrapper
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.text.Editable
import android.text.SpannableStringBuilder
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.TextView
import androidx.annotation.RequiresApi
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.core.net.toUri
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.example.todolist.R
import com.example.todolist.databinding.TaskFormBinding
import com.example.todolist.model.Task
import com.example.todolist.viewmodel.TaskViewModel
import com.google.android.material.textfield.TextInputEditText
import kotlinx.android.synthetic.main.task_form.*
import java.io.*
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter


/**
 * A simple [Fragment] subclass as the second destination in the navigation.
 */
const val REQUEST_CODE = 200
class TaskFromFragment : Fragment() {

    private var _binding: TaskFormBinding? = null
    private var imageUri: Uri? = null
    private var imageUriIntPath: Uri? = null
    private var imageUriDBState : Uri? = null
    private val taskViewModel: TaskViewModel by activityViewModels()
    private val IMAGE_PICK_CODE = 1000
    private val PERMISSION_CODE = 1001

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!
    private lateinit var model: SharedRecordingViewModel
    private var audio_path: String? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        var currentTask = arguments?.get("currentTask")
        if (currentTask != null) {
            currentTask = currentTask as Task
            imageUriDBState = Uri.parse(currentTask.imagePath)

        } else {
            imageUriDBState = null
        }
        val callback = object : OnBackPressedCallback(true) {

            override fun handleOnBackPressed() {
                if (imageUriDBState == null && userImageView.drawable != null) {
                    imageUriIntPath?.path?.let { deleteImage(it) }
                } else if (imageUriDBState != null && userImageView.drawable == null) {
                    imageUri = imageUriDBState
                    saveImageToInternalMemory(generateImageName("usr_img.jpg"))
                }
                findNavController().navigate(R.id.action_TaskForm_to_Tasks)
                Log.d("IMAGERM", "INVOKED THIS")
                imageUriDBState?.path?.let { Log.d("IMAGERM", it) }
                imageUriIntPath?.path?.let { Log.d("IMAGERM", it) }
            }

        }
        requireActivity().onBackPressedDispatcher.addCallback(this, callback)
    }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        _binding = TaskFormBinding.inflate(inflater, container, false)

        model = ViewModelProvider(requireActivity()).get(SharedRecordingViewModel::class.java)
        model.audio_path.observe(viewLifecycleOwner, Observer<String?> { dataFromFragment2 ->
            audio_path = dataFromFragment2
            println(audio_path)
        })
        binding.apply {
            dateInput.setOnClickListener {
                val datePickerFragment = DatePickerFragment()
                val supportFragmentManager = requireActivity().supportFragmentManager

                supportFragmentManager.setFragmentResultListener(
                    "REQUEST_KEY",
                    viewLifecycleOwner
                ) { resultKey, bundle ->
                    if (resultKey == "REQUEST_KEY") {
                        val date: Editable =
                            SpannableStringBuilder(bundle.getString("SELECTED_DATE"))
                        dateInput.text = date
                    }
                }
                datePickerFragment.show(supportFragmentManager, "DatePickerFragment")
            }
            addRemoveImageButton.setOnClickListener {
                if(userImageView.drawable == null) {
                    pickupImageFromGallery()
                } else {
                    userImageView.setImageDrawable(null)
                    imageUri = null
                    imageUriIntPath!!.path?.let { it1 -> deleteImage(it1) }
                    imageUriIntPath = null
                    addRemoveImageButton.text = "ADD IMAGE"
                    addRemoveImageButton.setCompoundDrawablesWithIntrinsicBounds(R.drawable.image_add_icon_customized, 0,0,0)
                }
            }
        }

        return binding.root

    }
    fun pickupImageFromGallery() {
        //Log.d("INVOKED", (userImageView.drawable == null).toString())
        val intent = Intent(Intent.ACTION_PICK)
        intent.data = MediaStore.Images.Media.EXTERNAL_CONTENT_URI
        startActivityForResult(intent, IMAGE_PICK_CODE)

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        //Log.d("INVOKED", "onCreateView has been invoked!")
        //Log.d("INVOKED", "Request code " + requestCode)
        if (resultCode == RESULT_OK && requestCode == IMAGE_PICK_CODE) {
            //Log.d("INVOKED", "onCreateView has been invoked!")
            if (data != null) {
                imageUri = data.data
                imageUriIntPath = saveImageToInternalMemory(generateImageName("usr_img.jpg"))
                //Log.d("IMAGE", imageUriIntPath.toString())
                imageUriIntPath!!.path?.let { loadImageFromInternalMem(it) }
                addRemoveImageButton.setCompoundDrawablesWithIntrinsicBounds(R.drawable.image_rm_icon_customized, 0,0,0)
                addRemoveImageButton.text = "REMOVE IMAGE"
            }
        }

    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when(requestCode) {
            PERMISSION_CODE -> {
                if(grantResults.size > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    pickupImageFromGallery()
                } else {
                    val toast = Toast.makeText(requireContext(), "Permission denied!", Toast.LENGTH_SHORT)
                    toast.show()
                }
            }
        }
    }

    private fun loadImageFromInternalMem(path: String) {
        try {
            val f = File(path)
            val b = BitmapFactory.decodeStream(FileInputStream(f))
            userImageView.setImageBitmap(b)
        } catch (e: FileNotFoundException) {
            e.printStackTrace()
        }
    }

    private fun generateImageName(baseName: String) : String {
        val current = LocalDateTime.now()
        val formatter = DateTimeFormatter.ofPattern("yyyy_MM_dd_HH_mm_ss.SSS")
        val formatted = current.format(formatter)
        return formatted + baseName
    }

    private fun saveImageToInternalMemory(name: String) : Uri {
        val cw = ContextWrapper(requireContext())
        val directory: File = cw.getDir("user_images", Context.MODE_PRIVATE)
        val imgPath = File(directory, name)
        var fos: FileOutputStream? = null
        try {
            fos = FileOutputStream(imgPath)
            val bitmapImage = ImageDecoder.decodeBitmap(ImageDecoder.createSource(requireContext().contentResolver, imageUri!!))
            bitmapImage.compress(Bitmap.CompressFormat.PNG, 100, fos)
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            try {
                fos!!.close()
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
        return imgPath.toUri()
    }

    private fun deleteImage(name: String) {
        val f = File(name)
        f.delete()
    }


    @RequiresApi(Build.VERSION_CODES.R)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        var currentTask = arguments?.get("currentTask")

        binding.nameInput.doOnTextChanged { text, start, before, count ->
            if (text == "")
                binding.nameErrorText.visibility = View.VISIBLE
            else
                binding.nameErrorText.visibility = View.GONE
        }


        if (currentTask != null) {
            currentTask = currentTask as Task
            binding.floatingActionButton.setOnClickListener {
                if (updateTask(view, currentTask!!))
                    findNavController().navigate(R.id.action_TaskForm_to_Tasks)
            }
            binding.floatingActionButton.text = getString(R.string.update)
            binding.floatingActionButton.icon = resources.getDrawable(R.drawable.ic_edit_foreground)

            binding.nameInput.text = Editable.Factory.getInstance().newEditable(currentTask.name)

            if (currentTask.date != null) {
                binding.dateInput.text = Editable.Factory.getInstance().newEditable(currentTask.date?.format(
                    DateTimeFormatter.ofPattern("dd-MM-yyyy")))
            }

            imageUriIntPath = if (currentTask.imagePath == null) null else Uri.parse(currentTask.imagePath)
            if (imageUriIntPath != null) {
                currentTask.imagePath?.let { loadImageFromInternalMem(it) }
                addRemoveImageButton.text = "REMOVE IMAGE"
            } else {
                userImageView.setImageDrawable(null)
                addRemoveImageButton.text = "ADD IMAGE"
            }
            binding.checkBox.isChecked = currentTask.flag
            model.updateData(currentTask.voiceRecordPath)
        } else {
            binding.floatingActionButton.text = getString(R.string.create)
            binding.floatingActionButton.setOnClickListener {
                if (addNewTask(view))
                    findNavController().navigate(R.id.action_TaskForm_to_Tasks)
            }
        }
        binding.record.setOnClickListener {
            findNavController().navigate(R.id.action_TaskForm_to_recordingFragment)
        }
    }

    private fun updateTask(view: View, currentTask: Task): Boolean {
        var errors: Boolean = false
        view.apply {
            val sdf = DateTimeFormatter.ofPattern("dd-MM-yyyy")
            val flag = findViewById<CheckBox>(R.id.checkBox).isChecked
            var name = findViewById<TextInputEditText>(R.id.nameInput).text.toString()
            val date = findViewById<TextInputEditText>(R.id.dateInput).text.toString()
            val isVoiceRecord: Boolean = audio_path != null && !audio_path.equals("")
            var taskDate: LocalDate? = null

            if (isVoiceRecord) {
                if (date == "") {
                    taskDate = null
                } else {
                    taskDate = LocalDate.parse(date, sdf)
                }
                if (name == "") {
                    name =  "Voice record"
                }
            } else {
                if (date == "") {
                    taskDate = null
                } else {
                    taskDate = LocalDate.parse(date, sdf)
                }
                if (name == "") {
                    findViewById<TextView>(R.id.nameErrorText).visibility = View.VISIBLE
                    errors = true
                }
            }

            if (!errors) {
                currentTask.name = name
                currentTask.date = taskDate
                currentTask.flag = flag
                currentTask.voiceRecordPath = audio_path
                model.updateData(null)
                currentTask.imagePath = imageUriIntPath?.path
                taskViewModel.updateTask(currentTask)

                return true
            }
            return false
        }
    }

    private fun addNewTask(view: View): Boolean {
        var errors: Boolean = false
        view.apply {
            val sdf = DateTimeFormatter.ofPattern("dd-MM-yyyy")
            val flag = findViewById<CheckBox>(R.id.checkBox).isChecked
            var name = findViewById<TextInputEditText>(R.id.nameInput).text.toString()
            val date = findViewById<TextInputEditText>(R.id.dateInput).text.toString()
            var taskDate: LocalDate? = null
            val isVoiceRecord: Boolean = audio_path != null && !audio_path.equals("")

            if (isVoiceRecord) {
                if (date == "") {
                    taskDate = null
                } else {
                    taskDate = LocalDate.parse(date, sdf)
                }
                if (name == "") {
                    name =  "Voice record"
                }
            } else {
                if (date == "") {
                    taskDate = null
                } else {
                    taskDate = LocalDate.parse(date, sdf)
                }
                if (name == "") {
                    findViewById<TextView>(R.id.nameErrorText).visibility = View.VISIBLE
                    errors = true
                }
            }


            if (!errors) {
                taskViewModel.addTask(Task( 0, arguments?.getInt("topicId")!!,  name, taskDate, flag, false, LocalDate.now(), audio_path, imageUriIntPath?.path))
                model.updateData(null)
                return true
            }
            return false
        }
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}