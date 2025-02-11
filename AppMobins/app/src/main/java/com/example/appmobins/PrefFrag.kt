package com.example.appmobins

import android.content.Context
import android.os.Bundle
import android.text.InputType
import androidx.preference.EditTextPreference
import androidx.preference.EditTextPreference.OnBindEditTextListener
import androidx.preference.PreferenceFragmentCompat


class PrefFrag : PreferenceFragmentCompat() {

    interface OnDataPass {
        fun onDataPass(data: Pair<String, String>)
        fun onFragDestroyed()
    }

    lateinit var dataPasser: OnDataPass

    override fun onDestroyView(){
        dataPasser.onFragDestroyed()
        super.onDestroyView()
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        dataPasser = context as OnDataPass
    }

    fun passData(data: Pair<String, String>){
        dataPasser.onDataPass(data)
    }

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        addPreferencesFromResource(R.xml.preferences)
        val edit_pref =
            preferenceManager.findPreference<EditTextPreference>("edit_text_preference")

        edit_pref?.setOnBindEditTextListener(OnBindEditTextListener { editText -> //workaround to make keyboard numeric and put cursor at end of input
            editText.inputType = InputType.TYPE_CLASS_NUMBER
            editText.setSelection(editText.length())//placing cursor at the end of the text
        })


//        val p_manager = getPreferenceManager()
//        val p_list = p_manager.findPreference<ListPreference>("list_preference")
//        p_list?.setOnPreferenceChangeListener { _ , newValue ->
//            val index = newValue as String
//            val list_options = listOf<String>(
//                "5G Control Plane",
//                "LTE Control Plane",
//                "LTE PHY",
//                "LTE Control/PHY",
//                "LTE Control/Data Plane",
//                "LTE Control/Data/PHY",
//                "LTE/3G Control Plane",
//                "All"
//            )
//            val result:String = list_options[index.toInt()-1]
//
//            Toast.makeText(activity?.getApplicationContext(), result, Toast.LENGTH_LONG).show()
//            passData(Pair("log_type", result))
//            true
//        }
//
//
//        val p_switch = p_manager.findPreference<SwitchPreference>("switch_preference")
//
//        p_switch?.setOnPreferenceChangeListener { _ ,  isChecked -> //the parameters are (Preference, new Value)
//            val message = if (isChecked as Boolean) "Switch1:ON" else "Switch1:OFF"
//            Toast.makeText(activity?.getApplicationContext(), message, Toast.LENGTH_LONG).show()
//            passData(Pair("log_single", isChecked.toString()))
//            true
//        }
    }

}