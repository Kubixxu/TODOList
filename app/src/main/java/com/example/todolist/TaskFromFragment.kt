package com.example.todolist

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
import androidx.navigation.fragment.findNavController
import com.example.todolist.Tasks.Companion.tasksList
import com.example.todolist.databinding.TaskFormBinding
import com.google.android.material.textfield.TextInputEditText
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.temporal.TemporalQueries.localDate


/**
 * A simple [Fragment] subclass as the second destination in the navigation.
 */
class TaskFromFragment : Fragment() {

    private var _binding: TaskFormBinding? = null

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
                // create new instance of DatePickerFragment
                val datePickerFragment = DatePickerFragment()
                val supportFragmentManager = requireActivity().supportFragmentManager

                // we have to implement setFragmentResultListener
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

        val position = arguments?.getInt("position")

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

        if (position != -1) {
            binding.floatingActionButton.setOnClickListener {
                if (updateTask(view, position!!))
                    findNavController().navigate(R.id.action_SecondFragment_to_FirstFragment)
            }
            binding.floatingActionButton.text = getString(R.string.update)
            val currTask = tasksList[position!!]

            binding.nameInput.text = Editable.Factory.getInstance().newEditable(currTask.name)
            binding.dateInput.text = Editable.Factory.getInstance().newEditable(currTask.date.format(
                DateTimeFormatter.ofPattern("dd-MM-yyyy")))
            binding.checkBox.isChecked = currTask.flag
        } else {
            binding.floatingActionButton.text = getString(R.string.create)
            binding.floatingActionButton.setOnClickListener {
                if (addNewTask(view))
                    findNavController().navigate(R.id.action_SecondFragment_to_FirstFragment)
            }
        }
    }

    private fun updateTask(view: View, position: Int): Boolean {
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
                tasksList[position].name = name
                tasksList[position].date = LocalDate.parse(date, sdf)
                tasksList[position].flag = flag
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
                tasksList.add(Task("University", name, LocalDate.parse(date, sdf), flag, false))
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