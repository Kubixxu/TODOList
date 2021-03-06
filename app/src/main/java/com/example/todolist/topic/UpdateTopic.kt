package com.example.todolist.topic

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Spinner
import android.widget.Toast
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.example.todolist.R
import com.example.todolist.databinding.FragmentUpdateTopicBinding
import com.example.todolist.model.Topic
import com.example.todolist.viewmodel.TopicViewModel
import kotlinx.android.synthetic.main.fragment_update_topic.*
import kotlinx.android.synthetic.main.fragment_update_topic.view.*

class UpdateTopic : Fragment() {
    private var _binding: FragmentUpdateTopicBinding? = null
    private val mTopicViewModel:TopicViewModel by activityViewModels()
    private val binding get() = _binding!!
    private lateinit var spinner: Spinner
    private lateinit var icons: Array<Int>
    private val args by navArgs<UpdateTopicArgs>()
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentUpdateTopicBinding.inflate(inflater, container, false)

        spinner = binding.root.findViewById(R.id.colorUpdateSpinner)
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
        binding.root.editTextUpdatePersonNameText.setText(args.currentTopic.name)
        spinner.setSelection(icons.indexOf(args.currentTopic.topicImageId))

        binding.root.accept_update_topic_fab.setOnClickListener {
            updateItem()
        }
        return binding.root
    }


    private fun updateItem() {
        val name = editTextUpdatePersonNameText.text.toString()
        val topicImageId = icons[spinner.selectedItemPosition]
        if(name != "" && spinner.selectedItemPosition != 0) {
            val updatedTopic = Topic(args.currentTopic.id, name, topicImageId)
            mTopicViewModel.updateTopic(updatedTopic)
            findNavController().navigate(R.id.action_topic_update_to_topic_list)
        }
        if (name == "") {
            binding.topicNameErrorTV.visibility = View.VISIBLE
        }
        if (spinner.selectedItemPosition == 0) {
            binding.topicIconErrorTV.visibility = View.VISIBLE
        }
    }


}
