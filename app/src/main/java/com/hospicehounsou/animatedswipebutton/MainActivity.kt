package com.hospicehounsou.animatedswipebutton

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        findViewById<AnimatedSwipeButton>(R.id.swipe_button).setListener {
            println(it)
        }

    }
}