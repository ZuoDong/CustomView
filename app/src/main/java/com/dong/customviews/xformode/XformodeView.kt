package com.dong.customviews.xformode

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.View
import com.dong.customviews.R
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.support.v7.content.res.AppCompatResources
import com.dong.customviews.utils.drawableToBitmap


/**
 * 作者：zuo
 * 时间：2018/5/17 17:06
 */
class XformodeView:View{

    constructor(context: Context?) : this(context,null)
    constructor(context: Context?, attrs: AttributeSet?) : this(context, attrs,0)
    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr){
        initView()
    }

    private lateinit var mBitPaint: Paint
    private lateinit var BmpDST:Bitmap
    private lateinit var BmpSRC: Bitmap
    private lateinit var BmpRevert: Bitmap

    private fun initView() {
        setLayerType(View.LAYER_TYPE_SOFTWARE,null)
        mBitPaint = Paint()
        BmpSRC = BitmapFactory.decodeResource(resources,R.mipmap.soho)
        BmpDST = AppCompatResources.getDrawable(context,R.drawable.white_grad_bg)!!.drawableToBitmap(BmpSRC.width,BmpSRC.height)

        val matrix = Matrix()
        matrix.setScale(1f, -1f)
        // 生成倒影图
        BmpRevert = Bitmap.createBitmap(BmpSRC, 0, 0, BmpSRC.width, BmpSRC.height, matrix, true)
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        //先画出小狗图片
        canvas.drawBitmap(BmpSRC, 0F, 0F, mBitPaint)

        //再画出倒影
        val layerId = canvas.saveLayer(0F, 0F, width.toFloat(), height.toFloat(), null, Canvas.ALL_SAVE_FLAG)
        canvas.translate(0F, BmpSRC.height.toFloat())

        canvas.drawBitmap(BmpDST, 0F, 0F, mBitPaint)
        mBitPaint.xfermode = PorterDuffXfermode(PorterDuff.Mode.SRC_ATOP)
        canvas.drawBitmap(BmpRevert, 0F, 0F, mBitPaint)

        mBitPaint.xfermode = null

        canvas.restoreToCount(layerId)
    }
}