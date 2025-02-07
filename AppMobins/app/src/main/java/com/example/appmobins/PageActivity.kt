package com.example.appmobins

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.preference.PreferenceFragmentCompat


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


        var saved_text = "type here"
        val edit_text = findViewById<EditText>(R.id.etx_box)
        edit_text.setHint(saved_text)
        edit_text.setOnClickListener{
            edit_text.isCursorVisible=true
        }

        findViewById<Button>(R.id.etx_submit)
            .setOnClickListener {
                saved_text=edit_text.getText().toString()
                edit_text.isCursorVisible = false
                this.currentFocus?.let { view ->
                    val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager
                    imm?.hideSoftInputFromWindow(view.windowToken, 0)
                }
                Toast.makeText(getApplicationContext(), saved_text, Toast.LENGTH_LONG).show();//display the text that you entered in edit text
            }

        findViewById<Button>(R.id.etx_cancel)
            .setOnClickListener {
                edit_text.isCursorVisible = false
                this.currentFocus?.let { view ->
                    val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager
                    imm?.hideSoftInputFromWindow(view.windowToken, 0)
                }
                edit_text.setText(saved_text)
                Toast.makeText(getApplicationContext(), saved_text, Toast.LENGTH_LONG).show();//display the text that you entered in edit text
            }


    }
}
