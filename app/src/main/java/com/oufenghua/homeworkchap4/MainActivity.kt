package com.oufenghua.homeworkchap4

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import kotlinx.android.synthetic.main.activity_main.*
import java.util.*

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        clock.mTimeCallBack = {
            time_tv.text = "${it[Calendar.YEAR]}-${it[Calendar.MONTH]}-${it[Calendar.DAY_OF_MONTH] + 1} ${it[Calendar.HOUR_OF_DAY]}:${it[Calendar.MINUTE]}:${it[Calendar.SECOND]}"
        }
    }
}
