package com.example.todolist.task

import android.app.Activity.RESULT_OK
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.text.Editable
import android.text.SpannableStringBuilder
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.TextView
import android.widget.Toast
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.example.todolist.R
import com.example.todolist.databinding.TaskFormBinding
import com.example.todolist.model.Task
import com.example.todolist.viewmodel.TaskViewModel
import com.example.todolist.viewmodel.TopicViewModel
import com.google.android.material.textfield.TextInputEditText
import kotlinx.android.synthetic.main.icon_with_text.*
import kotlinx.android.synthetic.main.task_form.*
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.jar.Manifest


/**
 * A simple [Fragment] subclass as the second destination in the navigation.
 */
class TaskFromFragment : Fragment() {

    private var _binding: TaskFormBinding? = null
    private var imageUri: Uri? = null
    private val taskViewModel: TaskViewModel by activityViewModels()
    private val IMAGE_PICK_CODE = 1000
    private val PERMISSION_CODE = 1001

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        _binding = TaskFormBinding.inflate(inflater, container, false)

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
                    addRemoveImageButton.text = "ADD IMAGE"
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
                userImageView.setImageURI(imageUri)
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

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        var currentTask = arguments?.get("currentTask")

        binding.nameInput.doOnTextChanged { text, start, before, count ->
            if (text == "")
                binding.nameErrorText.visibility = View.VISIBLE
            else
                binding.nameErrorText.visibility = View.GONE
        }

        binding.dateInput.doOnTextChanged { text, start, before, count ->
            if (text == "")
                binding.dateErrorText.visibility = View.VISIBLE
            else
                binding.dateErrorText.visibility = View.GONE
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
            binding.dateInput.text = Editable.Factory.getInstance().newEditable(currentTask.date.format(
                DateTimeFormatter.ofPattern("dd-MM-yyyy")))
            //binding.userImageView.setImageURI(Uri.parse(currentTask.imageUri))
            binding.checkBox.isChecked = currentTask.flag
        } else {
            binding.floatingActionButton.text = getString(R.string.create)
            binding.floatingActionButton.setOnClickListener {
                if (addNewTask(view))
                    findNavController().navigate(R.id.action_TaskForm_to_Tasks)
            }
        }
    }

    private fun updateTask(view: View, currentTask: Task): Boolean {
        var errors: Boolean = false
        view.apply {
            val sdf = DateTimeFormatter.ofPattern("dd-MM-yyyy")
            val flag = findViewById<CheckBox>(R.id.checkBox).isChecked
            val name = findViewById<TextInputEditText>(R.id.nameInput).text.toString()
            val date = findViewById<TextInputEditText>(R.id.dateInput).text.toString()

            if (name == "") {
                findViewById<TextView>(R.id.nameErrorText).visibility = View.VISIBLE
                errors = true
            }

            if (date == "") {
                findViewById<TextView>(R.id.dateErrorText).visibility = View.VISIBLE
                errors = true
            }

            if (!errors) {
                currentTask.name = name
                currentTask.date = LocalDate.parse(date, sdf)
                currentTask.flag = flag
                //currentTask.imageUri = imageUri.toString()

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
            val name = findViewById<TextInputEditText>(R.id.nameInput).text.toString()
            val date = findViewById<TextInputEditText>(R.id.dateInput).text.toString()
            if (name == "") {
                findViewById<TextView>(R.id.nameErrorText).visibility = View.VISIBLE
                errors = true
            }

            if (date == "") {
                findViewById<TextView>(R.id.dateErrorText).visibility = View.VISIBLE
                errors = true
            }

            if (!errors) {
                taskViewModel.addTask(Task( 0, arguments?.getInt("topicId")!!,  name,LocalDate.parse(date, sdf), flag, false, LocalDate.now()))
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