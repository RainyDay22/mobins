package com.example.appmobins

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
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

    }
}

class PageFrag : PreferenceFragmentCompat() {

    interface OnDataPass {
        fun onDataPass(data: String)
    }

    lateinit var dataPasser: OnDataPass

    override fun onAttach(context: Context) {
        super.onAttach(context)
        dataPasser = context as OnDataPass
    }

    fun passData(data: String){
        dataPasser.onDataPass(data)
    }

//    override fun onCreateView(
//        inflater: LayoutInflater, container: ViewGroup?,
//        savedInstanceState: Bundle?
//    ): View {
//        val v = inflater.inflate(R.layout.page_layout, container, false)
//
////        v!!.findViewById<Button>(R.id.back_button)
////            .setOnClickListener {
////                Log.d("NAV", "Ended new activity")
////                requireActivity().finish()
////            }
////
////        v.findViewById<com.google.android.material.switchmaterial.SwitchMaterial>(R.id.material_switch)
////            .setChecked(GlobalVars.getSwitchState())
////
////        v.findViewById<com.google.android.material.switchmaterial.SwitchMaterial>(R.id.material_switch)
////            .setOnCheckedChangeListener { _, isChecked ->
////                if (isChecked) {
////                    // The switch is checked.
////                    Log.d("Switch", "switch toggled on")
////                    passData("fragment dataaa")
////                    GlobalVars.setGlobalVarValue("changed!")
////                    Log.d("GlobalVars", GlobalVars.getGlobalVarValue()!!)
////                } else {
////                    // The switch isn't checked.
////                    Log.d("Switch", "switch toggled off")
////                    GlobalVars.setGlobalVarValue("changed back!")
////                    Log.d("GlobalVars", GlobalVars.getGlobalVarValue()!!)
////                }
////                GlobalVars.setSwitchState(isChecked)
////            }
//        return v
//    }

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        addPreferencesFromResource(R.xml.preferences)
    }

}
