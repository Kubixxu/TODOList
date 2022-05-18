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
        var norm = (amp.toInt() / 7).coerceAtMost(200).toFloat()
        ampList.add(norm)

        spikes.clear()
        var amps = ampList.takeLast(maxSpikes)
        for(i in amps.indices) {
            var left = sw - i*(w + d)
            var top = sh / 2 - amps[i] / 2
            var right = left + w
            var bottom = top + amps[i]

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