package com.example.appmobins

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import androidx.activity.ComponentActivity
import androidx.appcompat.app.AppCompatActivity

class PageActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?){
        super.onCreate(savedInstanceState)
        setContentView(R.layout.page_layout)

        findViewById<Button>(R.id.back_button)
            .setOnClickListener {
                Log.d("NAV", "Ended new activity")
                finish()
            }


        findViewById<com.google.android.material.switchmaterial.SwitchMaterial>(R.id.material_switch)
            .setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                // The switch is checked.
                Log.d("Switch", "switch toggled on")
            } else {
                // The switch isn't checked.
                Log.d("Switch", "switch toggled off")
            }
            }

    }
}
