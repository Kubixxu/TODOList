package com.example.todolist.task

import android.content.Context
import android.graphics.BitmapFactory
import android.media.MediaPlayer
import android.net.Uri
import android.os.Build
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.app.ActivityOptionsCompat
import androidx.core.view.ViewCompat
import androidx.navigation.findNavController
import androidx.navigation.fragment.FragmentNavigator
import androidx.navigation.fragment.FragmentNavigatorExtras
import androidx.recyclerview.widget.RecyclerView
import com.example.todolist.R
import com.example.todolist.model.Task
import com.example.todolist.topic.TopicListDirections
import com.example.todolist.viewmodel.TaskViewModel
import kotlinx.android.synthetic.main.task_form.*
import kotlinx.android.synthetic.main.tasks_list.view.*
import kotlinx.android.synthetic.main.topic.view.*
import java.io.File
import java.io.FileInputStream
import java.io.FileNotFoundException
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
                var voice = findViewById<ImageView>(R.id.voice_record)
                println(currTask.voiceRecordPath)
                if (!currTask.voiceRecordPath.equals("null") && currTask.voiceRecordPath != null) {
                    voice.setImageResource(R.drawable.ic_mic_foreground)
                    voice.setOnClickListener {
                        playAudio(currTask.voiceRecordPath)
                    }
                }
                else voice.setImageDrawable(null)

                if (currTask.imagePath != null) {
                    val smallImageIV = findViewById<ImageView>(R.id.userSmallImage)
                    loadImageFromInternalMem(currTask.imagePath!!, smallImageIV)
                    smallImageIV.visibility = View.VISIBLE
                    smallImageIV.setOnClickListener {

                        val extras = FragmentNavigatorExtras(smallImageIV to "show_full_img_transition")

                        val action = TasksDirections.actionTasksToImageFullScreen(currTask.imagePath!!)
                        findNavController().navigate(action, extras)

                    }

                } else {
                    findViewById<ImageView>(R.id.userSmallImage).setImageDrawable(null)
                    findViewById<ImageView>(R.id.userSmallImage).visibility = View.GONE
                }

                findViewById<ConstraintLayout>(R.id.task)
                    .setOnLongClickListener {
                        val action = TasksDirections.actionTasksToTaskForm()
                        action.topicId = staticTopicId
                        action.currentTask = currTask
                        findNavController().navigate(action)
                        true
                    }
            }
        }
    }

    private fun playAudio(audio_file_path: String?) {
        try {
            val file = File(audio_file_path)

            val uri = Uri.fromFile(file)
            val media_player = MediaPlayer.create(context, uri)
            media_player.start()

        } catch (e: Exception) {
            e.printStackTrace()
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

    private fun loadImageFromInternalMem(path: String, iv: ImageView) {
        try {
            val f = File(path)
            val b = BitmapFactory.decodeStream(FileInputStream(f))
            iv.setImageBitmap(b)
        } catch (e: FileNotFoundException) {
            e.printStackTrace()
        }
    }

}