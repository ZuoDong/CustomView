package com.dong.customviews.pathmeasure

import android.animation.Animator
import android.animation.ValueAnimator
import android.annotation.SuppressLint
import android.content.Context
import android.graphics.*
import android.os.Handler
import android.os.Message
import android.util.AttributeSet
import android.util.Log
import android.view.View

/**
 * 作者：zuo
 * 时间：2018/6/28 10:35
 */
class SearchView:View{
    constructor(context: Context?) : this(context,null)
    constructor(context: Context?, attrs: AttributeSet?) : this(context, attrs,0)
    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr){
        initAll()
    }

    private val TAG = "SearchView"
    private val mPaint = Paint()
    private val defaultDuration = 2000L
    private val startAnimator = ValueAnimator.ofFloat(0f,1f).setDuration(defaultDuration)
    private val searchAnimator = ValueAnimator.ofFloat(0f,1f).setDuration(defaultDuration)
    private val endAnimator = ValueAnimator.ofFloat(1f,0f).setDuration(defaultDuration)
    private lateinit var mUpdateListener:ValueAnimator.AnimatorUpdateListener
    private lateinit var mAnimatorListener:Animator.AnimatorListener
    private var currentValue:Float = 0f
    private val startPath = Path()
    private val searchPath = Path()
    private val pathMeasure = PathMeasure()
    private var viewWidth = 0
    private var outerRadius:Float = 0f
    private var innerRadius:Float = 0f
    private var currentState:State = State.NONE
    private var isOver = false
    private var count = 0

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        val size = MeasureSpec.getSize(widthMeasureSpec)
        setMeasuredDimension(size,size)
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        viewWidth = w
        outerRadius = w.toFloat() / 8
        innerRadius = outerRadius - 50
        generatePath()
    }

    private fun generatePath() {
        val innerLeft = viewWidth.toFloat() / 2 - innerRadius
        val innerRight = viewWidth.toFloat() / 2 + innerRadius
        val innerRectF = RectF(innerLeft,innerLeft,innerRight,innerRight)

        val outerLeft = viewWidth.toFloat() / 2 - outerRadius
        val outerRight = viewWidth.toFloat() / 2 + outerRadius
        val outerRectF = RectF(outerLeft,outerLeft,outerRight,outerRight)
        startPath.addArc(innerRectF,45f,359.9f)
        searchPath.addArc(outerRectF,45f,-359.9f)

        val pos = FloatArray(2)
        pathMeasure.setPath(searchPath,false)
        pathMeasure.getPosTan(0f,pos,null)

        startPath.lineTo(pos[0],pos[1])
    }

    private fun initAll() {
        initPaint()
        initListener()
        initAnimator()

        currentState = State.STARTING
        startAnimator.start()
    }

    private val mHandle = object :Handler(){
        override fun handleMessage(msg: Message?) {
            super.handleMessage(msg)
            handleNextAnim()
        }
    }

    private fun initListener() {
        mUpdateListener = ValueAnimator.AnimatorUpdateListener { animation ->
            currentValue = animation.animatedValue as Float
            invalidate()
        }

        mAnimatorListener = object :Animator.AnimatorListener{
            override fun onAnimationRepeat(animation: Animator?) {
                Log.i(TAG,"onAnimationRepeat")
            }

            override fun onAnimationEnd(animation: Animator?) {
                Log.i(TAG,"onAnimationEnd")
                mHandle.sendEmptyMessage(0)
            }

            override fun onAnimationCancel(animation: Animator?) {
                Log.i(TAG,"onAnimationCancel")
            }

            override fun onAnimationStart(animation: Animator?) {
                Log.i(TAG,"onAnimationStart")
            }

        }
    }

    private fun handleNextAnim() {
        if (currentState == State.STARTING) {
            isOver = false
            currentState = State.SEARCHING
            searchAnimator.start()
        }else if(currentState == State.SEARCHING){
            if(!isOver){
                searchAnimator.start()
                count++
                if(count >= 2-1){
                    isOver = true
                }
                Log.i(TAG,"" + searchAnimator.isRunning + " " + count)
            }else{
                currentState = State.ENDING
                endAnimator.start()
            }
        }else if(currentState == State.ENDING){
            currentState = State.NONE
        }
    }

    private fun initAnimator() {
        startAnimator.addUpdateListener(mUpdateListener)
        searchAnimator.addUpdateListener(mUpdateListener)
        endAnimator.addUpdateListener(mUpdateListener)

        startAnimator.addListener(mAnimatorListener)
        searchAnimator.addListener(mAnimatorListener)
        endAnimator.addListener(mAnimatorListener)
    }

    private fun initPaint() {
        mPaint.isAntiAlias = true
        mPaint.style = Paint.Style.STROKE
        mPaint.color = Color.WHITE
        mPaint.strokeWidth = 5f
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        drawSearch(canvas)
    }

    private fun drawSearch(canvas: Canvas?) {
        canvas?.drawColor(Color.parseColor("#0082D7"))
        if(currentState == State.NONE){
            canvas?.drawPath(startPath,mPaint)
        }else if(currentState == State.STARTING){
            val dst = Path()
            pathMeasure.setPath(startPath,false)
            pathMeasure.getSegment(pathMeasure.length * currentValue,pathMeasure.length,dst,true)
            canvas?.drawPath(dst,mPaint)
        }else if(currentState == State.SEARCHING){
            val dst2 = Path()
            pathMeasure.setPath(searchPath,false)

            val stop = pathMeasure.length * currentValue
            val start = stop - (0.5 - Math.abs(currentValue - 0.5))* 200f
            pathMeasure.getSegment(start.toFloat(),stop,dst2,true)
            canvas?.drawPath(dst2,mPaint)
        }else if(currentState == State.ENDING){
            pathMeasure.setPath(startPath,false)
            val dst3 = Path()
            pathMeasure.getSegment(pathMeasure.length * currentValue,pathMeasure.length,dst3,true)
            canvas?.drawPath(dst3,mPaint)
        }
    }

    enum class State{
        NONE,STARTING,SEARCHING,ENDING
    }
}