package com.example.todolist.topic

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Spinner
import android.widget.Toast
import androidx.core.widget.doOnTextChanged
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.example.todolist.R
import com.example.todolist.viewmodel.TopicViewModel
import com.example.todolist.model.Topic
import com.example.todolist.databinding.FragmentAddTopicBinding



class AddTopic : Fragment() {

    private var _binding: FragmentAddTopicBinding? = null
    private lateinit var mTopicViewModel: TopicViewModel
    private val binding get() = _binding!!
    private lateinit var spinner: Spinner
    private lateinit var icons: Array<Int>


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentAddTopicBinding.inflate(inflater, container, false)
        spinner = binding.root.findViewById(R.id.colorSpinner)
        mTopicViewModel = ViewModelProvider(this)[TopicViewModel::class.java]
        val iconTexts = arrayOf("Icon", "", "", "", "", "", "")
        icons = arrayOf(0,
            R.drawable.school,
            R.drawable.ic_baseline_local_pizza_24,
            R.drawable.ic_baseline_golf_course_24,
            R.drawable.ic_baseline_handyman_24,
            R.drawable.ic_baseline_family_restroom_24,
            R.drawable.ic_baseline_trending_up_24
        )
        val spinnerAdapter: IconAdapter =
            context?.let { IconAdapter(it, R.layout.icon_with_text, icons, iconTexts) }!!
        spinner.adapter = spinnerAdapter

        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.acceptCreateTopicFab.setOnClickListener {
            if(binding.editTextTextPersonName.text.toString() != "" && spinner.selectedItemPosition != 0) {
                insertTopicToDatabase()
                findNavController().navigate(R.id.action_topic_addition_to_topic_list)
            }
            if (binding.editTextTextPersonName.text.toString() == "") {
                binding.topicNameErrorTV.visibility = View.VISIBLE
            }
            if (spinner.selectedItemPosition == 0) {
                binding.topicIconErrorTV.visibility = View.VISIBLE
            }

        }

        binding.editTextTextPersonName.doOnTextChanged { text, _, _, _ ->
            if (text == "")
                binding.topicNameErrorTV.visibility = View.VISIBLE
            else
                binding.topicNameErrorTV.visibility = View.GONE
        }

    }

    private fun insertTopicToDatabase() {
        val topicName = binding.editTextTextPersonName.text.toString()
        val imageId = icons[spinner.selectedItemPosition]
        val topic = Topic(0, topicName, imageId)
        mTopicViewModel.addTopic(topic)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}