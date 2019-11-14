package com.dong.customviews.cameratansform

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.dong.customviews.R
import kotlinx.android.synthetic.main.activity_camera_trans.*

class CameraTransActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_camera_trans)

        img.setOnClickListener {
            val centerX = img.width / 2.0f
            val centerY = img.height / 2.0f

            val animation = Rotate3dAnimation(this,0f,360f,centerX,centerY,0f,false)
            animation.duration = 3000
            animation.fillAfter = true
            img.startAnimation(animation)
        }
    }
}
