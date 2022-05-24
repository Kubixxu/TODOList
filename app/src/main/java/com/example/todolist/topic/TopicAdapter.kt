package com.example.todolist.topic

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.example.todolist.R
import com.example.todolist.model.Topic
import kotlinx.android.synthetic.main.topic.view.*

class TopicAdapter : RecyclerView.Adapter<TopicAdapter.TopicViewHolder>() {

    var topics: List<Pair<Topic, Int>> = emptyList()
    class TopicViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TopicViewHolder {
        return TopicViewHolder(
            LayoutInflater.from(parent.context).inflate(
                R.layout.topic,
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: TopicViewHolder, position: Int) {
        val currTopic = topics[position].first
        holder.itemView.apply {
            topicName.text = currTopic.name
            numberOfItems.text = if (topics[position].second != 1) topics[position].second.toString() + " items" else topics[position].second.toString() + " item"
            topicIcon.setImageResource(currTopic.topicImageId)
            topicView.setOnLongClickListener {
                val action =
                    TopicListDirections.actionTopicListToTopicUpdate(currTopic, currTopic.name)
                findNavController().navigate(action)
                true
            }

            topicView.setOnClickListener {
                val action = TopicListDirections.actionTopicListToTasks()
                action.topicId = currTopic.id
                findNavController().navigate(action)
            }
        }
    }

    override fun getItemCount(): Int {
        return topics.size
    }

    @SuppressLint("NotifyDataSetChanged")
    fun setData(topicsList: List<Pair<Topic, Int>>){
        this.topics = topicsList
        notifyDataSetChanged()
    }
}