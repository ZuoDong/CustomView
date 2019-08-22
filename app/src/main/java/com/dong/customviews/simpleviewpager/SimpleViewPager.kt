package com.dong.customviews.simpleviewpager

import android.content.Context
import android.util.AttributeSet
import android.util.Log
import android.view.MotionEvent
import android.view.ViewConfiguration
import android.view.ViewGroup
import android.widget.Scroller

/**
 * 作者：zuo
 * 时间：2019/7/31 16:04
 */
class SimpleViewPager:ViewGroup{
    constructor(context: Context?) : this(context,null)
    constructor(context: Context?, attrs: AttributeSet?) : this(context, attrs,0)
    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr)

    private val TAG = "SimpleViewPager"
    private val mScoller = Scroller(context)
    private val mTouchSlop = ViewConfiguration.get(context).scaledTouchSlop

    private var lastMoveX = 0f

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        (0 until childCount).forEach {
            measureChild(getChildAt(it),widthMeasureSpec,heightMeasureSpec)
        }
    }

    override fun onLayout(changed: Boolean, l: Int, t: Int, r: Int, b: Int) {
        (0 until childCount).forEach {
            getChildAt(it).layout(it * measuredWidth,0,(it + 1) * measuredWidth,measuredHeight)
        }
    }

    override fun onInterceptTouchEvent(ev: MotionEvent?): Boolean {
        when(ev?.action){
            MotionEvent.ACTION_DOWN ->{
                lastMoveX = ev.x
                Log.i(TAG,"onInterceptTouchEvent : ACTION_DOWN : ev.x = ${ev.x}")
            }
            MotionEvent.ACTION_MOVE ->{
                val dx = Math.abs(ev.x - lastMoveX)
                lastMoveX = ev.x
                Log.i(TAG,"onInterceptTouchEvent : ACTION_MOVE : ev.x = ${ev.x} dx = $dx")
                if(dx > mTouchSlop){
                    Log.i(TAG,"onInterceptTouchEvent : ACTION_MOVE : return true")
                    return true
                }
            }
        }
        return super.onInterceptTouchEvent(ev)
    }

    override fun onTouchEvent(event: MotionEvent?): Boolean {
        when(event?.action){
            MotionEvent.ACTION_MOVE ->{
                val dx = lastMoveX - event.x
                Log.i(TAG,"onTouchEvent : ACTION_MOVE : lastMove = $lastMoveX  ev.x = ${event.x} dx = $dx scrollX = $scrollX")
                if(scrollX + dx < 0){
                    scrollTo(0,0)
                    return true
                }
                if(scrollX + dx > (childCount - 1) * measuredWidth){
                    scrollTo((childCount - 1) * measuredWidth,0)
                    return true
                }
                scrollBy(dx.toInt(),0)
                lastMoveX = event.x
            }
            MotionEvent.ACTION_UP ->{
                val position = (scrollX + measuredWidth / 2) /  measuredWidth
                mScoller.startScroll(scrollX,0, position * measuredWidth - scrollX,0)
                postInvalidate()
            }
        }
        return true
    }

    override fun computeScroll() {
        if(mScoller.computeScrollOffset()){
            scrollTo(mScoller.currX,mScoller.currY)
            postInvalidate()
        }
    }
}