package com.example.todolist

import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import androidx.navigation.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.example.todolist.model.Topic
import kotlinx.android.synthetic.main.topic.view.*

class TopicAdapter() : RecyclerView.Adapter<TopicAdapter.TopicViewHolder>() {

    var topics: List<Topic> = emptyList()
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
            numberOfItems.text = "0 items"
            topicIcon.setImageResource(currTopic.topicImageId)
            topicView.setOnLongClickListener {
                val action = TopicListDirections.actionFirstFragmentToUpdateTopic(currTopic)
                //action.
                findNavController().navigate(action)
                true
            }
        }
    }

    override fun getItemCount(): Int {
        return topics.size
    }

    fun setData(topicsList: List<Topic>){
        this.topics = topicsList
        //Log.d("INVOKED", "setData has been invoked!")
        //Log.d("INVOKED", "Given list: " + topicsList)
        notifyDataSetChanged()
    }

    fun itemDeleted(pos: Int) {
        //topics.remo
    }

}