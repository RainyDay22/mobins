package com.example.appmobins

import android.app.Application
import android.os.Bundle
import android.util.Log
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import com.example.appmobins.GlobalVars

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
            .setChecked(GlobalVars.getSwitchState())

        findViewById<com.google.android.material.switchmaterial.SwitchMaterial>(R.id.material_switch)
            .setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                // The switch is checked.
                Log.d("Switch", "switch toggled on")
                GlobalVars.setGlobalVarValue("changed!")
                Log.d("GlobalVars", GlobalVars.getGlobalVarValue()!!)
            } else {
                // The switch isn't checked.
                Log.d("Switch", "switch toggled off")
                GlobalVars.setGlobalVarValue("changed back!")
                Log.d("GlobalVars", GlobalVars.getGlobalVarValue()!!)
            }
                GlobalVars.setSwitchState(isChecked)
            }

    }
}
