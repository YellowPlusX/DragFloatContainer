package com.freeman.widget.dragfloatcontainer

import android.content.Context
import android.util.AttributeSet
import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.view.ViewConfiguration
import android.widget.FrameLayout
import kotlin.math.abs

/**
 * Created by Freeman on 2020/10/13
 */
class DragFloatContainer @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : FrameLayout(context, attrs, defStyleAttr) {

    companion object {
        private const val TAG = "DragFloatContainer"
    }

    private var originalX: Float = Float.MIN_VALUE
    private var originalY: Float = Float.MIN_VALUE

    private var downX: Int = 0
    private var downY: Int = 0

    private var locationX: Int = 0
    private var locationY: Int = 0

    private var parentWidth = 0
    private var parentHeight = 0

    private var dragging = false

    var loggable = true

    override fun onInterceptTouchEvent(event: MotionEvent?): Boolean {
        if (event == null) {
            return super.onInterceptTouchEvent(event)
        }
        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                getViewLocation()
                downX = event.rawX.toInt()
                downY = event.rawY.toInt()
                log("onInterceptTouchEvent ACTION_DOWN : downX = $downX, downY = $downY")
                dragging = false
                if (originalX == Float.MIN_VALUE && originalY == Float.MIN_VALUE) {
                    originalX = x
                    originalY = y
                }
            }
            MotionEvent.ACTION_MOVE -> {
                val offsetX = abs(downX - event.rawX)
                val offsetY = abs(downY - event.rawY)
                Log.i(
                    TAG,
                    "onInterceptTouchEvent ACTION_MOVE : offsetX = $offsetX, offsetY = $offsetY"
                )
                dragging = offsetX >= ViewConfiguration.get(context).scaledTouchSlop
                        || offsetY >= ViewConfiguration.get(context).scaledTouchSlop
            }
            MotionEvent.ACTION_CANCEL, MotionEvent.ACTION_UP -> {
                log("onInterceptTouchEvent ACTION_UP")
                dragging = false
            }
        }
        log("dragging $dragging")
        return dragging
    }

    override fun onTouchEvent(event: MotionEvent?): Boolean {
        if (event == null) {
            return super.onTouchEvent(event)
        }
        if (visibility == View.GONE || visibility == View.INVISIBLE) {
            return false
        }
        if (parentWidth == 0 || parentHeight == 0) {
            getParentSize()
        }
        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                log("onTouchEvent : MotionEvent.ACTION_DOWN")
                getViewLocation()
                downX = event.rawX.toInt()
                downY = event.rawY.toInt()
                log("onTouchEvent : downX = $downX, downY = $downY")
            }
            MotionEvent.ACTION_MOVE -> {
                log("onTouchEvent : MotionEvent.ACTION_MOVE")
                val offsetX = ensureLocationOffSetX(event.rawX.toInt() - downX)
                val offsetY = ensureLocationOffSetY(event.rawY.toInt() - downY)
                log("onTouchEvent: offsetX = $offsetX, offsetY = $offsetY")
                translationX = offsetX.toFloat()
                translationY = offsetY.toFloat()
            }
            MotionEvent.ACTION_CANCEL, MotionEvent.ACTION_UP -> {
                log("onTouchEvent : MotionEvent.ACTION_UP")
                val offsetX = ensureLocationOffSetX(event.rawX.toInt() - downX)
                val offsetY = ensureLocationOffSetY(event.rawY.toInt() - downY)
                fixTheLocationAtLast(offsetX, offsetY)
            }
        }
        return super.onTouchEvent(event)
    }

    private fun fixTheLocationAtLast(offsetX: Int, offsetY: Int) {
        translationX = 0f
        translationY = 0f
        val lp = layoutParams as MarginLayoutParams
        lp.marginStart = lp.marginStart + offsetX
        lp.topMargin = lp.topMargin + offsetY
        layoutParams = lp
    }

    private fun getParentSize() {
        val parent = parent as View
        parentWidth = parent.width
        parentHeight = parent.height
        log("parent.width = $parentWidth, parent.height = $parentHeight")
    }

    private fun getViewLocation() {
        locationX = x.toInt()
        locationY = y.toInt()
        log("locationX = $locationX, locationY = $locationY")
    }

    private fun ensureLocationOffSetX(offsetX: Int): Int {
        val newLocationX = locationX + offsetX
        // Make sure the view does not beyond the edge of parent
        if (newLocationX <= 0) {
            return -locationX
        }
        if (newLocationX >= parentWidth - width) {
            return offsetX - (newLocationX - parentWidth + width)
        }
        return offsetX
    }

    private fun ensureLocationOffSetY(offsetY: Int): Int {
        val newLocationY = locationY + offsetY
        // Make sure the view does not beyond the edge of parent
        if (newLocationY <= 0) {
            return -locationY
        }
        if (newLocationY >= parentHeight - height) {
            return offsetY - (newLocationY - parentHeight + height)
        }
        return offsetY
    }

    fun setOriginalX(originalX: Float) {
        this.originalX = originalX
    }

    fun setOriginY(originalY: Float) {
        this.originalY = originalY
    }

    fun reset() {
        translationX = 0f
        translationY = 0f
        val lp = layoutParams as MarginLayoutParams
        lp.marginStart = originalX.toInt()
        lp.topMargin = originalY.toInt()
        layoutParams = lp
    }

    private fun log(log: String) {
        if (loggable) {
            Log.i(TAG, log)
        }
    }
}