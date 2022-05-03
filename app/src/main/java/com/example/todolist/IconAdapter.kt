package com.example.todolist

import android.content.Context
import android.content.res.Resources
import android.graphics.drawable.Drawable
import android.media.Image
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.TextView
import kotlinx.android.synthetic.main.icon_with_text.view.*


class IconAdapter(context: Context, resource: Int, val icons: Array<Int>, val texts: Array<String>) : ArrayAdapter<Int>(context, resource, icons) {
    override fun getDropDownView(
        position: Int,
        convertView: View?,
        parent: ViewGroup
    ): View {
        return getCustomView(position, convertView, parent)
    }



    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        return getCustomView(position, convertView, parent)
    }

    // Function to return our custom View (View with an image and text)
    fun getCustomView(position: Int, convertView: View?, parent: ViewGroup?): View {

        val inflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val row: View = inflater.inflate(R.layout.icon_with_text, parent, false)
        val icon: ImageView = row.findViewById(R.id.imageView)
        val text: TextView = row.findViewById(R.id.textView)
        if (position != 0) {
            icon.setBackgroundResource(icons[position])
        }
        text.text = texts[position]

        return row
    }
}