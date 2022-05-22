package com.example.todolist.task

import android.graphics.BitmapFactory
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.navigation.fragment.navArgs

import com.example.todolist.R
import com.example.todolist.databinding.ImageFullScreenBinding
import com.example.todolist.databinding.TaskFormBinding
import com.example.todolist.topic.UpdateTopicArgs
import kotlinx.android.synthetic.main.image_full_screen.fullScreenImageView;
import java.io.File
import java.io.FileInputStream
import java.io.FileNotFoundException


class ImageFullScreen : Fragment() {

    private val args by navArgs<ImageFullScreenArgs>()
    private var _binding: ImageFullScreenBinding? = null
    private val binding get() = _binding!!
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = ImageFullScreenBinding.inflate(inflater, container, false)
        loadImageFromInternalMem(args.imgPath, binding.fullScreenImageView)
        return binding.root
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