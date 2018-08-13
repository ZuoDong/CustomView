package com.dong.customviews.foldlayout

import android.content.Context
import android.graphics.Canvas
import android.util.AttributeSet
import android.util.Log
import android.view.GestureDetector
import android.view.MotionEvent

/**
 * 作者：zuo
 * 时间：2018/8/13 17:33
 */
class TouchFoldLayout:FoldLayout{
    constructor(context: Context?) : this(context,null)
    constructor(context: Context?, attrs: AttributeSet?) : this(context, attrs,0)
    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr)


    private var mTranslation = -1f
    private val gestureDector:GestureDetector by lazy { GestureDetector(context,ScrollGestrueDect()) }

    override fun dispatchDraw(canvas: Canvas?) {
        if(mTranslation == -1f) mTranslation = width.toFloat()
        super.dispatchDraw(canvas)
    }

    override fun onTouchEvent(event: MotionEvent?): Boolean {
        return gestureDector.onTouchEvent(event)
    }

    inner class ScrollGestrueDect:GestureDetector.SimpleOnGestureListener(){
        override fun onDown(e: MotionEvent?): Boolean {
            return true
        }

        override fun onScroll(e1: MotionEvent?, e2: MotionEvent?, distanceX: Float, distanceY: Float): Boolean {
            Log.i("TouchFoldLayout","distanceX -> $distanceX")
            mTranslation -= distanceX
            if(mTranslation < 0){
                mTranslation = 0f
            }
            if(mTranslation > width){
                mTranslation = width.toFloat()
            }
            val factor = Math.abs(mTranslation / width)
            setFactor(factor)
            return true
        }
    }
}