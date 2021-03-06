package com.example.todolist.task

import android.annotation.SuppressLint
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
import android.widget.Switch
import android.widget.TextView
import androidx.annotation.RequiresApi
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.core.net.toUri
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
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


class TaskFromFragment : Fragment() {

    private var _binding: TaskFormBinding? = null
    private var imageUri: Uri? = null
    private var imageUriIntPath: Uri? = null
    private var imageUriDBState : Uri? = null
    private val taskViewModel: TaskViewModel by activityViewModels()
    private val imagePickCode = 1000
    private val permissionCode = 1001

    private val binding get() = _binding!!
    private lateinit var model: SharedRecordingViewModel
    private var audioPath: String? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        var bitmap : Bitmap? = null
        var currentTask = arguments?.get("currentTask")
        if (currentTask != null) {
            currentTask = currentTask as Task
            if (currentTask.imagePath != null) {
                imageUriDBState = Uri.parse(currentTask.imagePath)
                val imagePath = imageUriDBState?.path
                bitmap = imagePath?.let { getBitmapFromPath(it) }
            }
        } else {
            imageUriDBState = null
        }

        val callback = object : OnBackPressedCallback(true) {

            override fun handleOnBackPressed() {
                if (imageUriDBState == null && userImageView.drawable != null) {
                    imageUriIntPath?.path?.let { deleteImage(it) }
                } else if (imageUriDBState != null && userImageView.drawable == null && bitmap != null) {
                    val filePath : String = imageUriDBState?.path!!
                    val lastSlashIndex = filePath.lastIndexOf('/')
                    val fileName = filePath.substring(lastSlashIndex + 1)
                    saveImageToInternalMemory(fileName, bitmap)
                }
                if (currentTask == null && audioPath != null && !audioPath.equals("")) {
                    if (File(audioPath!!).exists()) audioPath?.let { File(it).delete() }
                    model.updateData(null)
                }
                findNavController().navigate(R.id.action_TaskForm_to_Tasks)
            }
        }
        requireActivity().onBackPressedDispatcher.addCallback(this, callback)
    }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = TaskFormBinding.inflate(inflater, container, false)

        model = ViewModelProvider(requireActivity())[SharedRecordingViewModel::class.java]
        model.audioPath.observe(viewLifecycleOwner) { dataFromFragment2 ->
            audioPath = dataFromFragment2

            if (model.getData() == null) {
                record.setCompoundDrawablesWithIntrinsicBounds(
                    R.drawable.ic_mic_none_foreground,
                    0,
                    0,
                    0
                )
            } else {
                record.setCompoundDrawablesWithIntrinsicBounds(
                    R.drawable.ic_mic_foreground,
                    0,
                    0,
                    0
                )
            }

        }
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
                    addRemoveImageButton.setText(R.string.add_image)
                    addRemoveImageButton.setCompoundDrawablesWithIntrinsicBounds(R.drawable.image_add_icon_customized, 0,0,0)
                }
            }
        }
        return binding.root
    }

    private fun pickupImageFromGallery() {
        val intent = Intent(Intent.ACTION_PICK)
        intent.data = MediaStore.Images.Media.EXTERNAL_CONTENT_URI
        startActivityForResult(intent, imagePickCode)
    }

    @Deprecated("Deprecated in Java")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (resultCode == RESULT_OK && requestCode == imagePickCode) {
            if (data != null) {
                imageUri = data.data
                imageUriIntPath = saveImageToInternalMemory(generateImageName("usr_img.jpg"))
                imageUriIntPath!!.path?.let { loadImageFromInternalMem(it) }
                addRemoveImageButton.setCompoundDrawablesWithIntrinsicBounds(R.drawable.image_rm_icon_customized, 0,0,0)
                addRemoveImageButton.setText(R.string.remove_img)
            }
        }

    }

    @Deprecated("Deprecated in Java")
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when(requestCode) {
            permissionCode -> {
                if(grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    pickupImageFromGallery()
                } else {
                    val toast = Toast.makeText(requireContext(), "Permission denied!", Toast.LENGTH_SHORT)
                    toast.show()
                }
            }
        }
    }
    private fun getBitmapFromPath(path: String): Bitmap {
        val f = File(path)
        return BitmapFactory.decodeStream(FileInputStream(f))
    }
    private fun loadImageFromInternalMem(path: String) {
        try {
            val b = getBitmapFromPath(path)
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

    private fun saveImageToInternalMemory(name: String, bitmap: Bitmap) {
        val cw = ContextWrapper(requireContext())
        val directory: File = cw.getDir("user_images", Context.MODE_PRIVATE)
        val imgPath = File(directory, name)
        var fos: FileOutputStream? = null
        try {
            fos = FileOutputStream(imgPath)
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, fos)
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            try {
                fos!!.close()
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
    }

    private fun deleteImage(name: String) {
        val f = File(name)
        f.delete()
    }


    @SuppressLint("UseCompatLoadingForDrawables")
    @RequiresApi(Build.VERSION_CODES.R)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        var currentTask = arguments?.get("currentTask")

        binding.nameInput.doOnTextChanged { text, _, _, _ ->
            if (text == "")
                binding.nameErrorText.visibility = View.VISIBLE
            else
                binding.nameErrorText.visibility = View.GONE
        }


        if (currentTask != null) {
            currentTask = currentTask as Task
            binding.floatingActionButton.setOnClickListener {
                if (updateTask(view, currentTask))
                    findNavController().navigate(R.id.action_TaskForm_to_Tasks)
            }
            binding.floatingActionButton.setText(R.string.update)
            binding.floatingActionButton.icon = resources.getDrawable(R.drawable.ic_edit_foreground)

            binding.nameInput.text = Editable.Factory.getInstance().newEditable(currentTask.name)

            if (currentTask.date != null) {
                binding.dateInput.text = Editable.Factory.getInstance().newEditable(currentTask.date?.format(
                    DateTimeFormatter.ofPattern("dd-MM-yyyy")))
            }

            imageUriIntPath = if (currentTask.imagePath == null) null else Uri.parse(currentTask.imagePath)
            if (imageUriIntPath != null) {
                currentTask.imagePath?.let { loadImageFromInternalMem(it) }
                addRemoveImageButton.setText(R.string.remove_img)
                addRemoveImageButton.setCompoundDrawablesWithIntrinsicBounds(R.drawable.image_rm_icon_customized, 0,0,0)
            } else {
                userImageView.setImageDrawable(null)
                addRemoveImageButton.setText(R.string.add_image)
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
        var errors = false
        view.apply {
            val sdf = DateTimeFormatter.ofPattern("dd-MM-yyyy")
            val flag = findViewById<Switch>(R.id.checkBox).isChecked
            var name = findViewById<TextInputEditText>(R.id.nameInput).text.toString()
            val date = findViewById<TextInputEditText>(R.id.dateInput).text.toString()
            val isVoiceRecord: Boolean = audioPath != null && !audioPath.equals("")
            val taskDate: LocalDate?

            if (isVoiceRecord) {
                taskDate = if (date == "") {
                    null
                } else {
                    LocalDate.parse(date, sdf)
                }
                if (name == "") {
                    name =  "Voice record"
                }
            } else {
                taskDate = if (date == "") {
                    null
                } else {
                    LocalDate.parse(date, sdf)
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
                currentTask.voiceRecordPath = audioPath
                model.updateData(null)
                currentTask.imagePath = imageUriIntPath?.path
                taskViewModel.updateTask(currentTask)

                return true
            }
            return false
        }
    }

    private fun addNewTask(view: View): Boolean {
        var errors = false
        view.apply {
            val sdf = DateTimeFormatter.ofPattern("dd-MM-yyyy")
            val flag = findViewById<Switch>(R.id.checkBox).isChecked
            var name = findViewById<TextInputEditText>(R.id.nameInput).text.toString()
            val date = findViewById<TextInputEditText>(R.id.dateInput).text.toString()
            val taskDate: LocalDate?
            val isVoiceRecord: Boolean = audioPath != null && !audioPath.equals("")

            if (isVoiceRecord) {
                taskDate = if (date == "") {
                    null
                } else {
                    LocalDate.parse(date, sdf)
                }
                if (name == "") {
                    name =  "Voice record"
                }
            } else {
                taskDate = if (date == "") {
                    null
                } else {
                    LocalDate.parse(date, sdf)
                }
                if (name == "") {
                    findViewById<TextView>(R.id.nameErrorText).visibility = View.VISIBLE
                    errors = true
                }
            }

            if (!errors) {
                taskViewModel.addTask(Task( 0, arguments?.getInt("topicId")!!,  name, taskDate, flag, false, LocalDate.now(), audioPath, imageUriIntPath?.path))
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