package com.example.todolist.task

import android.app.AlertDialog
import android.os.Bundle
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.todolist.R
import com.example.todolist.databinding.TasksListBinding
import com.example.todolist.model.Task
import com.example.todolist.viewmodel.TaskViewModel
import kotlin.streams.toList

class Tasks : Fragment() {

    private lateinit var taskListAdapter: TasksListAdapter
    private lateinit var taskViewModel: TaskViewModel
    private var _binding: TasksListBinding? = null
    private var tasksList = emptyList<Task>()
    private val binding get() = _binding!!

    companion object {
        var topicId = -1
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val callback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                findNavController().navigate(R.id.action_Tasks_to_TopicList)
            }
        }
        requireActivity().onBackPressedDispatcher.addCallback(this, callback)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        if (arguments?.getInt("topicId") != -1) {
            topicId = arguments?.getInt("topicId")!!
        }
        _binding = TasksListBinding.inflate(inflater, container, false)

        val recyclerView : RecyclerView = binding.root.findViewById(R.id.tasksList)
        prepareTaskList()

        taskListAdapter = TasksListAdapter(context, topicId, taskViewModel)
        recyclerView.adapter = taskListAdapter
        recyclerView.layoutManager = LinearLayoutManager(context)

        val itemTouchHelper = ItemTouchHelper(SwipeToDelete(this))
        itemTouchHelper.attachToRecyclerView(recyclerView)

        view?.setOnKeyListener { _, keyCode, _ ->
                if (keyCode == KeyEvent.KEYCODE_BACK) {
                    val action = TasksDirections.actionTasksToTopicList()
                    findNavController().navigate(action)
                }
            return@setOnKeyListener false
        }
        return binding.root
    }

    private fun prepareTaskList() {
        val recyclerView : RecyclerView = binding.root.findViewById(R.id.tasksList)
        val emptyImage : ImageView = binding.root.findViewById(R.id.emptyTasksImage)
        val emptyText1 : TextView = binding.root.findViewById(R.id.emptyTasksText)
        val emptyArrowImg2 : ImageView = binding.root.findViewById(R.id.empty_point_arrow2_topic)
        taskViewModel = ViewModelProvider(this)[TaskViewModel::class.java]
        taskViewModel.readAllData.observe(viewLifecycleOwner) { tasks ->
            setTasks(tasks)
            if (tasksList.isEmpty()) {
                recyclerView.visibility = View.GONE
                emptyImage.visibility = View.VISIBLE
                emptyText1.visibility = View.VISIBLE
                emptyArrowImg2.visibility = View.VISIBLE
            } else {
                recyclerView.visibility = View.VISIBLE
                emptyImage.visibility = View.GONE
                emptyText1.visibility = View.GONE
                emptyArrowImg2.visibility = View.GONE
            }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.fabAddTask.setOnClickListener {
            val action = TasksDirections.actionTasksToTaskForm()
            action.currentTask = null
            action.topicId = topicId
            findNavController().navigate(action)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null

    }

    fun deleteTask(position: Int) {
        val builder = AlertDialog.Builder(context)
        builder.setPositiveButton("Yes") {_,_ -> taskViewModel.deleteTask(taskListAdapter.tasks[position].task)}
        builder.setNegativeButton("No") {_,_ -> }
        builder.setTitle("Delete task ${taskListAdapter.tasks[position].task.name}?")
        builder.setMessage("Are you sure you want to delete task ${taskListAdapter.tasks[position].task.name}?")
        builder.create().show()
        prepareTaskList()
    }

    private fun setTasks(tasks: List<Task>) {
        tasksList = tasks.stream().filter{it.topic == topicId}.toList()
        taskListAdapter.setData(tasksList)
    }

}