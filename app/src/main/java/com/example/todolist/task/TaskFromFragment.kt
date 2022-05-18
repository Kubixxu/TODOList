package com.example.todolist.task

import android.os.Build
import android.os.Bundle
import android.text.Editable
import android.text.SpannableStringBuilder
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.TextView
import androidx.annotation.RequiresApi
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
import java.time.LocalDate
import java.time.format.DateTimeFormatter


/**
 * A simple [Fragment] subclass as the second destination in the navigation.
 */
const val REQUEST_CODE = 200
class TaskFromFragment : Fragment() {

    private val taskViewModel: TaskViewModel by activityViewModels()

    // This property is only valid between onCreateView and
    // onDestroyView.

    private var _binding: TaskFormBinding? = null
    private val binding get() = _binding!!
    private lateinit var model: SharedRecordingViewModel
    private var audio_path: String? = null

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
        }

        return binding.root

    }


    @RequiresApi(Build.VERSION_CODES.R)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        var currentTask = arguments?.get("currentTask")
        println(currentTask)
        println(arguments?.getInt("topicId"))

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
                currentTask.voiceRecordPath = audio_path
                model.updateData(null)

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
                taskViewModel.addTask(Task( 0, arguments?.getInt("topicId")!!,  name,LocalDate.parse(date, sdf), flag, false, LocalDate.now(), audio_path))
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
