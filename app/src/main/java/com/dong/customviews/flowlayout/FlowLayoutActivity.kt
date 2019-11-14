package com.dong.customviews.flowlayout

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.dong.customviews.R
import com.dong.customviews.utils.dp2px

class FlowLayoutActivity : AppCompatActivity() {

    private val list = mutableListOf<String>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_flow_layout)

        (0 until 15).forEach {
            list.add("非常好啊啊")
        }

        val flowLayout = findViewById<FlowLayout>(R.id.flow_layout)
        flowLayout.setVerticalSpace(dp2px(15f).toInt())
        flowLayout.setHorizontalSpace(dp2px(15f).toInt())
        flowLayout.addTags(list)

        val expandableLayout = findViewById<ExpandableFlowLayout>(R.id.expand_flow_layout)
        expandableLayout.setVerticalSpace(dp2px(15f).toInt())
        expandableLayout.setHorizontalSpace(dp2px(15f).toInt())
        expandableLayout.addTags(list)
    }
}
