package com.example.todolist

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.todolist.viewmodel.TopicViewModel
import com.example.todolist.databinding.TopicListBinding


class TopicList : Fragment() {

    private lateinit var topicListAdapter: TopicAdapter
    private lateinit var mTopicViewModel: TopicViewModel
    private var _binding: TopicListBinding? = null
    //private val topicList = ArrayList<Topic>(mutableListOf(Topic("Eating", R.drawable.ic_baseline_local_pizza_24, 0),
        //Topic("More eating", R.drawable.ic_baseline_local_pizza_24, 0)))


    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

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
        val emptyArrowImg1 : ImageView = binding.root.findViewById(R.id.empty_point_arrow1)
        val emptyArrowImg2 : ImageView = binding.root.findViewById(R.id.empty_point_arrow2)
        mTopicViewModel = ViewModelProvider(this).get(TopicViewModel::class.java)
        mTopicViewModel.readAllData.observe(viewLifecycleOwner, Observer {
            topic -> topicListAdapter.setData(topic); if (topic.isEmpty()) {
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

        //topicListAdapter.setData(topicList)


        recyclerView.adapter = topicListAdapter
        recyclerView.layoutManager = LinearLayoutManager(context)
        //Log.d("INVOKED", "onCreateView has been invoked!")
        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.createTopicFab.setOnClickListener {
            findNavController().navigate(R.id.action_FirstFragment_to_SecondFragment)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}