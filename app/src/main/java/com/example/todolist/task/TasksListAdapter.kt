package com.example.todolist.task

import android.animation.Animator
import android.annotation.SuppressLint
import android.content.Context
import android.graphics.BitmapFactory
import android.graphics.Color
import android.media.MediaPlayer
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.navigation.findNavController
import androidx.navigation.fragment.FragmentNavigatorExtras
import androidx.recyclerview.widget.RecyclerView
import com.airbnb.lottie.LottieAnimationView
import com.example.todolist.R
import com.example.todolist.model.Task
import com.example.todolist.viewmodel.TaskViewModel
import java.io.File
import java.io.FileInputStream
import java.io.FileNotFoundException
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import kotlin.streams.toList


class TasksListAdapter(private val context: Context?, private var topicId: Int?,
                       private val taskViewModel: TaskViewModel
) : RecyclerView.Adapter<TasksListAdapter.TaskViewHolder>() {

    class TaskViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)
    var tasks: List<DataObject> = emptyList()
    private var mediaPlayer: MediaPlayer? = null

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

    @SuppressLint("CutPasteId")
    override fun onBindViewHolder(holder: TaskViewHolder, @SuppressLint("RecyclerView") position: Int) {
        staticTopicId = topicId!!
        val data = tasks[position]
        if (!data.isHeader) {
            val currTask = tasks[position].task
            val sdf = DateTimeFormatter.ofPattern("dd-MM-yyyy")
            holder.itemView.apply {
                val isDone: LottieAnimationView = findViewById(R.id.isDone)
                isDone.setOnClickListener {
                    if (!tasks[position].task.completed) {
                        isDone.speed = 2f
                        isDone.playAnimation()
                    } else {
                        isDone.speed = -2f
                        isDone.playAnimation()
                    }
                    currTask.completed = !currTask.completed
                    isDone.addAnimatorListener(object: Animator.AnimatorListener {
                        override fun onAnimationStart(animation: Animator?) {
                        }

                        override fun onAnimationEnd(animation: Animator?) {
                            completeTask(currTask)
                        }

                        override fun onAnimationCancel(animation: Animator?) {
                        }

                        override fun onAnimationRepeat(animation: Animator?) {
                        }


                    })
                }

                if (currTask.completed) isDone.progress = 1f else isDone.progress = 0f

                findViewById<TextView>(R.id.taskName).text = currTask.name
                if (currTask.date != null) {
                    findViewById<TextView>(R.id.date).visibility = View.VISIBLE
                    findViewById<TextView>(R.id.date).text = sdf.format(currTask.date)
                    val diff: Long? = currTask.date?.toEpochDay()?.minus(LocalDate.now().toEpochDay())
                    if (diff!! <= 2) findViewById<TextView>(R.id.date).setTextColor(Color.RED)
                    else findViewById<TextView>(R.id.date).setTextColor(resources.getColor(R.color.date_color))
                } else {
                    findViewById<TextView>(R.id.date).visibility = View.GONE
                }

                if (!currTask.flag) findViewById<ImageView>(R.id.flag).visibility = View.GONE
                else {
                    findViewById<ImageView>(R.id.flag).visibility = View.VISIBLE
                }

                val voice = findViewById<LottieAnimationView>(R.id.voice_record)
                if (!currTask.voiceRecordPath.equals("null") && currTask.voiceRecordPath != null) {
                    voice.visibility = View.VISIBLE
                    voice.setOnClickListener {
                        playAudio(currTask.voiceRecordPath, voice)
                    }
                }
                else voice.visibility = View.GONE

                if (currTask.imagePath != null) {
                    val smallImageIV = findViewById<ImageView>(R.id.userSmallImage)
                    loadImageFromInternalMem(currTask.imagePath!!, smallImageIV)
                    smallImageIV.visibility = View.VISIBLE
                    smallImageIV.transitionName = "show_full_img_transition$position"
                    smallImageIV.setOnClickListener {

                        val extras = FragmentNavigatorExtras(smallImageIV to smallImageIV.transitionName)

                        val action = TasksDirections.actionTasksToImageFullScreen(currTask.imagePath!!, position)
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

    @SuppressLint("NotifyDataSetChanged")
    private fun completeTask(currTask: Task) {
        taskViewModel.updateTask(currTask)
        notifyDataSetChanged()
    }

    private fun playAudio(audio_file_path: String?, voice: LottieAnimationView) {
        try {
            val file = audio_file_path?.let { File(it) }

            val uri = Uri.fromFile(file)
            mediaPlayer = MediaPlayer.create(context, uri)
            mediaPlayer?.start()

            voice.loop(true)
            voice.playAnimation()

            mediaPlayer?.setOnCompletionListener {
                voice.cancelAnimation()
                voice.frame = 0
            }

        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override fun getItemCount(): Int {
        return tasks.size
    }

    @SuppressLint("NotifyDataSetChanged")
    fun setData(tasksList: List<Task>){
        val data: MutableList<DataObject> = mutableListOf()

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