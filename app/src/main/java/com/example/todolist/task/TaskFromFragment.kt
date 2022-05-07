package com.example.todolist.task

import android.os.Bundle
import android.text.Editable
import android.text.SpannableStringBuilder
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.TextView
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
import java.time.LocalDate
import java.time.format.DateTimeFormatter


/**
 * A simple [Fragment] subclass as the second destination in the navigation.
 */
class TaskFromFragment : Fragment() {

    private var _binding: TaskFormBinding? = null
    private val taskViewModel: TaskViewModel by activityViewModels()

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
        }

        return binding.root

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