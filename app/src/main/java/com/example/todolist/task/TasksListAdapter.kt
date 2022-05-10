package com.example.todolist.task

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.example.todolist.R
import com.example.todolist.model.Task
import com.example.todolist.viewmodel.TaskViewModel
import kotlinx.android.synthetic.main.tasks_list.view.*
import kotlinx.android.synthetic.main.topic.view.*
import java.time.format.DateTimeFormatter
import kotlin.streams.toList


class TasksListAdapter(private val context: Context?, private var topicId: Int?,
                       private val taskViewModel: TaskViewModel
) : RecyclerView.Adapter<TasksListAdapter.TaskViewHolder>() {

    class TaskViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)
    var tasks: List<DataObject> = emptyList()

    companion object {
        var staticTopicId = 0
    }

    override fun getItemViewType(position: Int): Int {
        return if (!tasks[position].isHeader) R.layout.task else R.layout.task_completion_header
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TaskViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(viewType, parent, false)
        return TaskViewHolder(view)
    }


    override fun onBindViewHolder(holder: TaskViewHolder, position: Int) {
        staticTopicId = topicId!!
        var data = tasks[position]
        if (!data.isHeader) {
            val currTask = tasks[position].task
            val sdf = DateTimeFormatter.ofPattern("dd-MM-yyyy")
            holder.itemView.apply {
                findViewById<TextView>(R.id.taskName).text = currTask.name
                findViewById<TextView>(R.id.date).text = sdf.format(currTask.date)
                if (currTask.completed) findViewById<ImageView>(R.id.isDone).setImageResource(R.drawable.ic_check_on_foreground)
                else findViewById<ImageView>(R.id.isDone).setImageResource(R.drawable.ic_check_off_foreground)

                findViewById<ImageView>(R.id.isDone).setOnClickListener {
                    setCompletion(position)
                }

                if (currTask.flag) findViewById<ImageView>(R.id.flag).setImageResource(R.drawable.ic_flag_foreground)
                else findViewById<ImageView>(R.id.flag).setImageDrawable(null)
                findViewById<ConstraintLayout>(R.id.task)
                    .setOnLongClickListener {
                        val action = TasksDirections.actionTasksToTaskForm(currTask)
                        action.topicId = staticTopicId
                        findNavController().navigate(action)
                        true
                    }
            }
        }
    }

    override fun getItemCount(): Int {
        return tasks.size
    }


    private fun setCompletion(position: Int) {
        tasks[position].task.completed = !tasks[position].task.completed
        taskViewModel.updateTask(tasks[position].task)
        notifyItemChanged(position)
    }

    fun setData(tasksList: List<Task>){
        val data: MutableList<DataObject> = mutableListOf<DataObject>()

        val uncompletedTasks = tasksList.stream().filter{!it.completed}.toList()
        for (task in uncompletedTasks) {
            data.add(DataObject(task))
        }


        val completedTasks = tasksList.stream().filter{it.completed}.toList()
        if (completedTasks.isNotEmpty()) {
            data.add(DataObject("completed"))
            for (task in completedTasks) {
                data.add(DataObject(task))
            }
        }


        this.tasks = data.toList()
        notifyDataSetChanged()
    }

}