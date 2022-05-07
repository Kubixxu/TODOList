package com.example.todolist.task

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.content.Context
import android.os.Bundle
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.navigation.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.example.todolist.R
import com.example.todolist.model.Task
import com.example.todolist.topic.TopicListDirections
import kotlinx.android.synthetic.main.tasks_list.view.*
import kotlinx.android.synthetic.main.topic.view.*
import java.time.format.DateTimeFormatter

class TasksListAdapter(private val context: Context?, private var topicId: Int?
) : RecyclerView.Adapter<TasksListAdapter.TaskViewHolder>() {

    class TaskViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)

    var tasks: List<Task> = emptyList()

    companion object {
        var staticTopicId = 0
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TaskViewHolder {
        return TaskViewHolder(
            LayoutInflater.from(parent.context).inflate(
                R.layout.task,
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: TaskViewHolder, position: Int) {
        staticTopicId = topicId!!
        val currTask = tasks[position]
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

    override fun getItemCount(): Int {
        return tasks.size
    }


    private fun setCompletion(position: Int) {
        tasks[position].completed = !tasks[position].completed
        notifyItemChanged(position)
    }

    fun setData(tasksList: List<Task>){
        this.tasks = tasksList
        notifyDataSetChanged()
    }

}