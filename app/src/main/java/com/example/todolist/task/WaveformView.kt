package com.example.todolist.task

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.View

class WaveformView(context: Context?, attrs: AttributeSet?) : View(context, attrs) {
    private var paint = Paint()
    private var ampList = ArrayList<Float>()
    private var spikes = ArrayList<RectF>()

    private var radius = 6f
    private var w = 9f
    private var d = 6f

    private var sw = 0f
    private var sh = 200f

    private var maxSpikes = 0

    init {
        paint.color = Color.rgb(244, 81, 30)

        sw = resources.displayMetrics.widthPixels.toFloat()

        maxSpikes = (sw / (d+w)).toInt()
    }

    fun addAmplitude(amp: Float) {
        val norm = (amp.toInt() / 20).coerceAtMost(200).coerceAtLeast(10).toFloat()
        ampList.add(norm)

        spikes.clear()
        val amps = ampList.takeLast(maxSpikes)
        for(i in amps.indices) {
            val left = sw - i*(w + d)
            val top = sh / 2 - amps[i] / 2
            val right = left + w
            val bottom = top + amps[i]

            spikes.add(RectF(left, top, right, bottom))
        }

        invalidate()
    }

    override fun draw(canvas: Canvas?) {
        super.draw(canvas)
        spikes.forEach {
            canvas?.drawRoundRect(it, radius, radius, paint)
        }
    }
}