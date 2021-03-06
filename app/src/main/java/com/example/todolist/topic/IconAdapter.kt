package com.example.todolist.topic

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.TextView
import com.example.todolist.R


class IconAdapter(context: Context, resource: Int, private val icons: Array<Int>, private val texts: Array<String>) : ArrayAdapter<Int>(context, resource, icons) {
    override fun getDropDownView(
        position: Int,
        convertView: View?,
        parent: ViewGroup
    ): View {
        return getCustomView(position, parent)
    }



    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        return getCustomView(position, parent)
    }

    private fun getCustomView(position: Int, parent: ViewGroup?): View {

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