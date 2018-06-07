package com.dong.customviews.tickview

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import com.dong.customviews.R
import kotlinx.android.synthetic.main.activity_tick.*

class TickActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_tick)


        start_anim.setOnClickListener {
            tick_view.setChecked(!tick_view.getChecked())
            start_anim.text = if(tick_view.getChecked()) "恢复" else "开始"
        }

        tick_view.setOnCheckChangeListener(object :TickView.OnCheckChangeListener {
            override fun checkChanged(isChecked: Boolean) {
                start_anim.text = if(tick_view.getChecked()) "恢复" else "开始"
            }
        })
    }
}
