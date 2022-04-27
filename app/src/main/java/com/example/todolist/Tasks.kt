package com.example.todolist

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.todolist.databinding.TasksListBinding
import java.time.LocalDate
import java.util.*
import kotlin.collections.ArrayList

/**
 * A simple [Fragment] subclass as the default destination in the navigation.
 */
class Tasks : Fragment() {

    private lateinit var taskListAdapter: TasksListAdapter
    private var _binding: TasksListBinding? = null
    private var tasksList = ArrayList<Task>();
    // Map<Task, List<Topic>>

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        _binding = TasksListBinding.inflate(inflater, container, false)

//        tasksList.add(Task("University", "Eat", Date(202020202), 0, true))
//        tasksList.add(Task("University", "Eat", Date(20001212), 1, false))

        val recyclerView : RecyclerView = binding.root.findViewById(R.id.tasksList)
        val emptyImage : ImageView = binding.root.findViewById(R.id.emptyTasksImage)
        val emptyText1 : TextView = binding.root.findViewById(R.id.emptyTasksText)

        if (tasksList.isEmpty()) {
            recyclerView.visibility = View.GONE
            emptyImage.visibility = View.VISIBLE
            emptyText1.visibility = View.VISIBLE
        } else {
            recyclerView.visibility = View.VISIBLE
            emptyImage.visibility = View.GONE
            emptyText1.visibility = View.GONE
        }

        taskListAdapter = TasksListAdapter(tasksList)
        recyclerView.adapter = taskListAdapter
        recyclerView.layoutManager = LinearLayoutManager(context)
        //Log.d("INVOKED", "onCreateView has been invoked!")
        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.fabAddTask.setOnClickListener {
            findNavController().navigate(R.id.action_FirstFragment_to_SecondFragment)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}