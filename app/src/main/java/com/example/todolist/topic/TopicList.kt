package com.example.todolist.topic

import android.app.AlertDialog
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.OnBackPressedCallback
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.todolist.R
import com.example.todolist.viewmodel.TopicViewModel
import com.example.todolist.databinding.TopicListBinding
import com.example.todolist.model.Topic


class TopicList : Fragment() {

    private lateinit var topicListAdapter: TopicAdapter
    private lateinit var mTopicViewModel: TopicViewModel
    private var _binding: TopicListBinding? = null
    //private val topicList = ArrayList<Topic>(mutableListOf(Topic("Eating", R.drawable.ic_baseline_local_pizza_24, 0),
        //Topic("More eating", R.drawable.ic_baseline_local_pizza_24, 0)))


    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val callback = object : OnBackPressedCallback(true) {

            override fun handleOnBackPressed() {

            }

        }
        requireActivity().onBackPressedDispatcher.addCallback(this,  callback)
    }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        _binding = TopicListBinding.inflate(inflater, container, false)
        topicListAdapter = TopicAdapter()
        val recyclerView : RecyclerView = binding.root.findViewById(R.id.rvTopicItems)
        val emptyImage : ImageView = binding.root.findViewById(R.id.empty_list_img)
        val emptyText1 : TextView = binding.root.findViewById(R.id.empty_textView1)
        val emptyText2 : TextView = binding.root.findViewById(R.id.empty_textView2)
        val emptyArrowImg2 : ImageView = binding.root.findViewById(R.id.empty_point_arrow2)
        mTopicViewModel = ViewModelProvider(this).get(TopicViewModel::class.java)
        mTopicViewModel.readAllData.observe(viewLifecycleOwner, Observer {
            topic ->
            topicListAdapter.setData(changeTopicMapToList(topic)); if (topic.isEmpty()) {
            recyclerView.visibility = View.GONE
            emptyImage.visibility = View.VISIBLE
            emptyText1.visibility = View.VISIBLE
            emptyText2.visibility = View.VISIBLE
            emptyArrowImg2.visibility = View.VISIBLE
        } else {
            recyclerView.visibility = View.VISIBLE
            emptyImage.visibility = View.GONE
            emptyText1.visibility = View.GONE
            emptyText2.visibility = View.GONE
            emptyArrowImg2.visibility = View.GONE
        }
        })
        /*
        mTopicViewModel.readAllData.observe(viewLifecycleOwner, Observer {
                topic -> var topicList = emptyList<Pair<Topic, Int>>()
                for (elem in topic.keys) {
            topicListAdapter.setData(topic)
                    if (topic.isEmpty()) {
                    recyclerView.visibility = View.GONE
                    emptyImage.visibility = View.VISIBLE
                    emptyText1.visibility = View.VISIBLE
                    emptyText2.visibility = View.VISIBLE
                    emptyArrowImg1.visibility = View.VISIBLE
                    emptyArrowImg2.visibility = View.VISIBLE
                    } else {
                        recyclerView.visibility = View.VISIBLE
                        emptyImage.visibility = View.GONE
                        emptyText1.visibility = View.GONE
                        emptyText2.visibility = View.GONE
                        emptyArrowImg1.visibility = View.GONE
                        emptyArrowImg2.visibility = View.GONE
                    }
                })
        */
        //topicListAdapter.setData(topicList)


        recyclerView.adapter = topicListAdapter
        recyclerView.layoutManager = LinearLayoutManager(context)
        var itemTouchHelper = ItemTouchHelper(SwipeToDeleteTopic(topicListAdapter, this))
        itemTouchHelper.attachToRecyclerView(recyclerView)
        //Log.d("INVOKED", "onCreateView has been invoked!")
        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.createTopicFab.setOnClickListener {
            findNavController().navigate(R.id.action_topic_list_to_topic_addition)

        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    fun deleteItem(position: Int) {
        val builder = AlertDialog.Builder(requireContext())
        builder.setPositiveButton("Yes") {_,_ -> mTopicViewModel.deleteTopic(topicListAdapter.topics[position].first)}
        builder.setNegativeButton("No") {_,_ -> }
        builder.setTitle("Delete topic ${topicListAdapter.topics[position].first.name}?")
        builder.setMessage("Are you sure you want to delete topic ${topicListAdapter.topics[position].first.name}?")
        builder.create().show()
        changeTopicData()
        //mTopicViewModel.
    }
    private fun changeTopicData() {
        val recyclerView : RecyclerView = binding.root.findViewById(R.id.rvTopicItems)
        val emptyImage : ImageView = binding.root.findViewById(R.id.empty_list_img)
        val emptyText1 : TextView = binding.root.findViewById(R.id.empty_textView1)
        val emptyText2 : TextView = binding.root.findViewById(R.id.empty_textView2)
        val emptyArrowImg2 : ImageView = binding.root.findViewById(R.id.empty_point_arrow2)
        mTopicViewModel.readAllData.observe(viewLifecycleOwner, Observer {
                topic -> topicListAdapter.setData(changeTopicMapToList(topic)); if (topic.isEmpty()) {
            recyclerView.visibility = View.GONE
            emptyImage.visibility = View.VISIBLE
            emptyText1.visibility = View.VISIBLE
            emptyText2.visibility = View.VISIBLE
            emptyArrowImg2.visibility = View.VISIBLE
        } else {
            recyclerView.visibility = View.VISIBLE
            emptyImage.visibility = View.GONE
            emptyText1.visibility = View.GONE
            emptyText2.visibility = View.GONE
            emptyArrowImg2.visibility = View.GONE
        }
        })
    }
    private fun changeTopicMapToList(topicMap: Map<Topic, Int>): List<Pair<Topic, Int>> {
        val topicList =  ArrayList<Pair<Topic, Int>>()
        topicMap.forEach { entry -> topicList.add(Pair(entry.key, entry.value)) }
        return topicList
    }
}