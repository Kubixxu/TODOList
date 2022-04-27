package com.example.todolist

import android.icu.text.SimpleDateFormat
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class TasksListAdapter(
    private val tasks: MutableList<Task>
) : RecyclerView.Adapter<TasksListAdapter.TaskViewHolder>() {

    class TaskViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TaskViewHolder {
        return TaskViewHolder(
            LayoutInflater.from(parent.context).inflate(
                R.layout.fragment_task,
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: TaskViewHolder, position: Int) {
        val currTopic = tasks[position]
        val sdf = SimpleDateFormat("dd.MM.yyyy")
        holder.itemView.apply {
            findViewById<TextView>(R.id.taskName).text = currTopic.name
            findViewById<TextView>(R.id.date).text = sdf.format(currTopic.date)
            if (currTopic.completed) findViewById<ImageView>(R.id.isDone).setImageResource(R.drawable.ic_check_on_foreground)
            else findViewById<ImageView>(R.id.isDone).setImageResource(R.drawable.ic_check_off_foreground)

            if (currTopic.priority == 1) findViewById<ImageView>(R.id.flag).setImageResource(R.drawable.ic_flag_foreground)
            else findViewById<ImageView>(R.id.flag).setImageDrawable(null)

        }
    }

    override fun getItemCount(): Int {
        return tasks.size
    }


    fun setDone(position: Int) {
        tasks[position].completed = true
        notifyItemChanged(position)
    }

    fun setUndone(position: Int) {
        tasks[position].completed = false
        notifyItemChanged(position)
    }

}