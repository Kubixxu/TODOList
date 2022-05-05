package com.example.todolist

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.content.res.Configuration
import android.os.Bundle
import android.util.AttributeSet
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.ItemTouchHelper
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

    companion object {
        var tasksList = ArrayList<Task>();
    }

    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        _binding = TasksListBinding.inflate(inflater, container, false)


        val recyclerView : RecyclerView = binding.root.findViewById(R.id.tasksList)
        prepareTaskList()


        tasksList.sortBy { it.completed }
        taskListAdapter = TasksListAdapter(tasksList, context)
        recyclerView.adapter = taskListAdapter
        recyclerView.layoutManager = LinearLayoutManager(context)

        var itemTouchHelper = ItemTouchHelper(SwipeToDelete(this))
        itemTouchHelper.attachToRecyclerView(recyclerView)


        return binding.root

    }

    fun prepareTaskList() {
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


    fun deleteTask(position: Int) {
        val builder = AlertDialog.Builder(context)
        builder.setPositiveButton("Yes") {_,_ -> delete(position)}
        builder.setNegativeButton("No") {_,_ -> }
        builder.setTitle("Delete task ${taskListAdapter.tasks[position].name}?")
        builder.setMessage("Are you sure you want to delete task ${taskListAdapter.tasks[position].name}?")
        builder.create().show()
        taskListAdapter.notifyItemChanged(position)
    }

    private fun delete(position: Int) {
        val currTask = taskListAdapter.tasks[position]
        taskListAdapter.tasks.remove(currTask)
        taskListAdapter.notifyDataSetChanged()
        println(tasksList)
        prepareTaskList()
    }

}