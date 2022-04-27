package com.example.todolist

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.topic.view.*

class TopicAdapter(
    private val topics: MutableList<Topic>
) : RecyclerView.Adapter<TopicAdapter.TopicViewHolder>() {

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
        val currTopic = topics[position]
        holder.itemView.apply {
            topicName.text = currTopic.name
            numberOfItems.text = "${currTopic.taskNumber} items"
            topicIcon.setImageResource(currTopic.topicImageId)
        }
    }

    override fun getItemCount(): Int {
        return topics.size
    }

    fun addTopic(topic: Topic) {
        topics.add(topic)
        notifyItemInserted(topics.size - 1)
    }

    fun decreaseTaskNumber(position: Int) {
        topics[position].taskNumber -= 1
        notifyItemChanged(position)
    }

    fun increaseTaskNumber(position: Int) {
        topics[position].taskNumber += 1
        notifyItemChanged(position)
    }
}