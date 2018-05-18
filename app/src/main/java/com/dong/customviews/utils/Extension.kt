package com.dong.customviews.utils

import android.graphics.*
import android.graphics.drawable.Drawable

/**
 * 作者：zuo
 * 时间：2018/5/18 11:14
 */
fun Drawable.drawableToBitmap(w:Int,h:Int): Bitmap {
    // 取 drawable 的颜色格式
    val config = if (this.opacity != PixelFormat.OPAQUE)
        Bitmap.Config.ARGB_8888
    else
        Bitmap.Config.RGB_565
    // 建立对应 bitmap
    val bitmap = Bitmap.createBitmap(w, h, config)
    // 建立对应 bitmap 的画布
    val canvas = Canvas(bitmap)
    this.setBounds(0, 0, w, h)
    // 把 drawable 内容画到画布中
    this.draw(canvas)
    return bitmap
}

fun drawTextBitmap(w:Int,h:Int,s:String):Bitmap{
    val bitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888)
    val paint = Paint()
    paint.color = Color.WHITE
    paint.style = Paint.Style.FILL
    paint.strokeWidth = 50F
    paint.textSize = 150F
    val canvas = Canvas(bitmap)
    canvas.drawText(s,0F,h.toFloat()/2,paint)
    return  bitmap
}