package com.dong.customviews.redpoint

import android.content.Context
import android.graphics.*
import android.graphics.drawable.AnimationDrawable
import androidx.appcompat.content.res.AppCompatResources
import android.util.AttributeSet
import android.view.Gravity
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.TextView
import com.dong.customviews.R

/**
 * 作者：zuo
 * 时间：2018/5/21 18:30
 */
class RedPointControlView:FrameLayout{
    constructor(context: Context?) : this(context,null)
    constructor(context: Context?, attrs: AttributeSet?) : this(context, attrs,0)
    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr){
        initView()
    }
    private val DEFAULT_RADIUS = 20F
    private lateinit var mPaint:Paint
    private var mTouch: Boolean = false
    private lateinit var startPoint: PointF
    private lateinit var curPoint:PointF
    private lateinit var mPath:Path
    private lateinit var mTextView:TextView
    private lateinit var mImageView:ImageView
    private var mRadius:Float = DEFAULT_RADIUS
    private var isAnimStart:Boolean = false

    private fun initView() {
        mPaint = Paint()
        mPaint.style = Paint.Style.FILL
        mPaint.color = Color.RED
        mPaint.isAntiAlias = true
        mPaint.isDither = true

        startPoint = PointF(300F,300F)
        curPoint = PointF()
        mPath = Path()
        mPath.fillType = Path.FillType.WINDING

        val params = ViewGroup.LayoutParams(LayoutParams.WRAP_CONTENT,LayoutParams.WRAP_CONTENT)
        mTextView = TextView(context)
        mTextView.setPadding(10,10,10,10)
        mTextView.background = AppCompatResources.getDrawable(context,R.drawable.red_corner_bg)
        mTextView.gravity = Gravity.CENTER
        mTextView.setTextColor(Color.WHITE)
        mTextView.text = "99+"
        mTextView.layoutParams = params

        mImageView = ImageView(context)
        mImageView.layoutParams = params
        mImageView.setImageDrawable(AppCompatResources.getDrawable(context,R.drawable.red_point_dismiss_bg))
        mImageView.visibility = View.INVISIBLE

        addView(mTextView)
        addView(mImageView)
    }

    @Suppress("DEPRECATION")
    override fun dispatchDraw(canvas: Canvas) {
        canvas.saveLayer(0F,0F,width.toFloat(),height.toFloat(),mPaint,Canvas.ALL_SAVE_FLAG)
        if(!mTouch || isAnimStart){
            mTextView.x = startPoint.x - mTextView.width / 2
            mTextView.y = startPoint.y - mTextView.height / 2
        }else{
            calculatePath()
            canvas.drawPath(mPath,mPaint)
            canvas.drawCircle(startPoint.x,startPoint.y,mRadius,mPaint)
            canvas.drawCircle(curPoint.x, curPoint.y, mRadius, mPaint)
            mTextView.x = curPoint.x - mTextView.width / 2
            mTextView.y = curPoint.y - mTextView.height / 2
        }
        canvas.restore()
        super.dispatchDraw(canvas)
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        when(event.action){
            MotionEvent.ACTION_DOWN -> {
                val rect = RectF()
                val local = IntArray(2)
                mTextView.getLocationOnScreen(local)
                rect.left = local[0].toFloat()
                rect.top = local[1].toFloat()
                rect.right = mTextView.width + local[0].toFloat()
                rect.bottom = mTextView.height + local[1].toFloat()
                if(rect.contains(event.rawX,event.rawY)){
                    mTouch = true
                }
            }
            MotionEvent.ACTION_UP ->{
                mTouch = false
            }
        }
        postInvalidate()
        curPoint.set(event.x,event.y) //此处设置x,y相对父控件，而rawX,rawY会产生偏差
        return true
    }

    private fun calculatePath(){
        val x = curPoint.x
        val y = curPoint.y
        val startX = startPoint.x
        val startY = startPoint.y
        val dx = x - startX
        val dy = y - startY
        val a = Math.atan((dy / dx).toDouble())
        val offsetX = (mRadius * Math.sin(a)).toFloat()
        val offsetY = (mRadius * Math.cos(a)).toFloat()

        val distance = Math.sqrt(Math.pow((x - startX).toDouble()/2,2.toDouble()) + Math.pow((y - startY).toDouble()/2,2.toDouble()))
        mRadius = (DEFAULT_RADIUS - distance / 15).toFloat()
        if(mRadius < 9F){
            isAnimStart = true
            mImageView.x = curPoint.x - mImageView.width
            mImageView.y = curPoint.y - mImageView.height
            mImageView.visibility = View.VISIBLE
            (mImageView.drawable as AnimationDrawable).start()

            mTextView.visibility = View.GONE
        }

        val x1 = startX + offsetX
        val y1 = startY - offsetY
        val x2 = x + offsetX
        val y2 = y - offsetY
        val x3 = x - offsetX
        val y3 = y + offsetY
        val x4 = startX - offsetX
        val y4 = startY + offsetY

        val archorX = (startX + x)/2
        val archorY = (startY + y)/2

        mPath.reset()
        mPath.moveTo(x1,y1)
        mPath.quadTo(archorX,archorY,x2,y2)
        mPath.lineTo(x3,y3)
        mPath.quadTo(archorX,archorY,x4,y4)
        mPath.lineTo(x1,y1)
    }
}