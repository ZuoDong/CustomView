package com.dong.customviews.xformode

import android.content.Context
import android.util.AttributeSet
import android.view.View
import com.dong.customviews.utils.drawTextBitmap
import android.animation.ValueAnimator
import android.graphics.*
import android.view.animation.LinearInterpolator
import android.opengl.ETC1.getHeight
import android.opengl.ETC1.getWidth
import android.graphics.PorterDuff
import android.graphics.PorterDuffXfermode
import android.graphics.Canvas.ALL_SAVE_FLAG







/**
 * 作者：zuo
 * 时间：2018/5/18 17:57
 *
 * 波浪文字
 */
class WaveTextView:View{


    private val WIDTH_DEF:Int = 500
    private val HEIGHT_DEF:Int = 334
    private val mItemWaveLength = 450
    private var dx = 0

    private lateinit var mPath:Path
    private lateinit var mPaint:Paint
    private lateinit var BmpSRC:Bitmap
    private lateinit var BmpDST:Bitmap

    constructor(context: Context?) : this(context,null)
    constructor(context: Context?, attrs: AttributeSet?) : this(context, attrs,0)
    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr){
        initView()
    }

    private fun initView() {
        mPath = Path()
        mPaint = Paint()
        mPaint.color = Color.GREEN
        mPaint.style = Paint.Style.FILL_AND_STROKE

        BmpSRC = drawTextBitmap(WIDTH_DEF,HEIGHT_DEF,"ZUODONG")
        BmpDST = Bitmap.createBitmap(WIDTH_DEF, HEIGHT_DEF, Bitmap.Config.ARGB_8888)

        startAnim()
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        generageWavePath()

        //先清空bitmap上的图像,然后再画上Path
        val c = Canvas(BmpDST)
        c.drawColor(Color.BLACK, PorterDuff.Mode.CLEAR)
        c.drawPath(mPath, mPaint)

        canvas.drawBitmap(BmpSRC, 0F, 0F, mPaint)
        val layerId = canvas.saveLayer(0F, 0F, width.toFloat(), height.toFloat(), null, Canvas.ALL_SAVE_FLAG)
        canvas.drawBitmap(BmpDST, 0F, 0F, mPaint)
        mPaint.xfermode = PorterDuffXfermode(PorterDuff.Mode.DST_IN)
        canvas.drawBitmap(BmpSRC, 0F, 0F, mPaint)
        mPaint.xfermode = null
        canvas.restoreToCount(layerId)
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        setMeasuredDimension(500,334)
    }

    private fun generageWavePath() {
        mPath.reset()
        val originY = (BmpSRC.height / 2).toFloat()
        val halfWaveLen = (mItemWaveLength / 2).toFloat()
        mPath.moveTo((-mItemWaveLength + dx).toFloat(), originY)
        var i = -mItemWaveLength
        while (i <= width + mItemWaveLength) {
            mPath.rQuadTo(halfWaveLen / 2, (-50).toFloat(), halfWaveLen, 0F)
            mPath.rQuadTo(halfWaveLen / 2, 50F, halfWaveLen, 0F)
            i += mItemWaveLength
        }
        mPath.lineTo(WIDTH_DEF.toFloat(), HEIGHT_DEF.toFloat())
        mPath.lineTo(0F, HEIGHT_DEF.toFloat())
        mPath.close()
    }

    fun startAnim() {
        val animator = ValueAnimator.ofInt(0, mItemWaveLength)
        animator.duration = 2000
        animator.repeatCount = ValueAnimator.INFINITE
        animator.interpolator = LinearInterpolator()
        animator.addUpdateListener { animation ->
            dx = animation.animatedValue as Int
            postInvalidate()
        }
        animator.start()
    }
}