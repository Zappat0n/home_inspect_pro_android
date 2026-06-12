package com.homeinspectpro

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Path
import android.view.MotionEvent
import android.view.View

class SignatureView(context: Context) : View(context) {
    private val path = Path()
    private val paint = Paint().apply {
        color = Color.BLACK
        style = Paint.Style.STROKE
        strokeWidth = 4f
        strokeCap = Paint.Cap.ROUND
        strokeJoin = Paint.Join.ROUND
        isAntiAlias = true
    }
    private val paths = mutableListOf<Pair<Path, Paint>>()
    private var currentX = 0f
    private var currentY = 0f

    override fun onTouchEvent(event: MotionEvent): Boolean {
        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                path.reset()
                path.moveTo(event.x, event.y)
                currentX = event.x
                currentY = event.y
                return true
            }
            MotionEvent.ACTION_MOVE -> {
                val x = event.x
                val y = event.y
                val cx = (currentX + x) / 2
                val cy = (currentY + y) / 2
                path.quadTo(currentX, currentY, cx, cy)
                currentX = x
                currentY = y
                invalidate()
                return true
            }
            MotionEvent.ACTION_UP -> {
                path.lineTo(event.x, event.y)
                paths.add(Path(path) to Paint(paint))
                path.reset()
                invalidate()
                return true
            }
        }
        return super.onTouchEvent(event)
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        for ((savedPath, savedPaint) in paths) {
            canvas.drawPath(savedPath, savedPaint)
        }
        canvas.drawPath(path, paint)
    }

    fun getBitmap(): Bitmap {
        val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)
        canvas.drawColor(Color.WHITE)
        for ((savedPath, savedPaint) in paths) {
            canvas.drawPath(savedPath, savedPaint)
        }
        return bitmap
    }

    fun clear() {
        paths.clear()
        path.reset()
        invalidate()
    }
}
