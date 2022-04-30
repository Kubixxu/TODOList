package com.example.todolist

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import android.content.Context
import androidx.fragment.app.FragmentTransaction
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment.findNavController
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.example.todolist.Tasks.Companion.tasksList
import java.time.format.DateTimeFormatter

class TasksListAdapter(
    private val tasks: MutableList<Task>, private val context: Context?
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

        }
    }

    override fun getItemCount(): Int {
        return tasks.size
    }


    private fun setCompletion(position: Int) {
        tasks[position].completed = !tasks[position].completed
        notifyItemChanged(position)
    }

    fun deleteTask(position: Int) {
        val currTask = tasks[position]
        tasksList.remove(currTask)
        refresh()
        notifyItemChanged(position)
    }

    private fun refresh() {
        context.let {
            val fragmentManager = (context as? AppCompatActivity)?.supportFragmentManager
            fragmentManager?.let {
                val currentFragment = fragmentManager.findFragmentById(R.id.FirstFragment)
                currentFragment?.let {
                    val fragmentTransaction = fragmentManager.beginTransaction()
                    fragmentTransaction.detach(it)
                    fragmentTransaction.attach(it)
                    fragmentTransaction.commit()
                }
            }
        }
    }


}