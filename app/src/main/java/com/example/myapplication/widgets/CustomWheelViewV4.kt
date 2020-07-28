package com.example.myapplication.widgets

import android.content.Context
import android.graphics.Canvas
import android.util.AttributeSet
import android.view.MotionEvent
import com.contrarywind.view.WheelView

class CustomWheelViewV4 @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null
) : WheelView(context, attrs) {
    private var preCurrentItem = -1
    private var preTotalScrollY = -1f
    var onWheelScrolledListener: OnWheelScrolledListener? = null

    override fun onTouchEvent(event: MotionEvent): Boolean {
        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                parent.requestDisallowInterceptTouchEvent(true)
            }
            MotionEvent.ACTION_UP -> {
                parent.requestDisallowInterceptTouchEvent(false)
            }
            MotionEvent.ACTION_MOVE -> {

            }
        }
        return super.onTouchEvent(event)
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        if (preCurrentItem >= 0) {
            onWheelScrolledListener?.onWheelScrolled(preCurrentItem, currentItem, totalScrollY - preTotalScrollY)
        }
        preCurrentItem = currentItem
        preTotalScrollY = totalScrollY
    }

    interface OnWheelScrolledListener {
        fun onWheelScrolled(oldVal: Int, newVal: Int, dy: Float)
    }
}