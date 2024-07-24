package com.example.appmobins

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import androidx.activity.ComponentActivity

class PageActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?){
        super.onCreate(savedInstanceState)
        setContentView(R.layout.page_layout)

        findViewById<Button>(R.id.back_button)
            .setOnClickListener {
                Log.d("NAV", "Ended new activity")
                finish()
            }
    }
}
