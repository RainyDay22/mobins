package com.example.appmobins

import android.content.Context
import android.os.Bundle
import android.text.InputType
import androidx.preference.EditTextPreference
import androidx.preference.EditTextPreference.OnBindEditTextListener
import androidx.preference.PreferenceFragmentCompat


class PrefFrag : PreferenceFragmentCompat() {

    interface OnDataPass {
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


    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        addPreferencesFromResource(R.xml.preferences)
        val edit_pref =
            preferenceManager.findPreference<EditTextPreference>("edit_text_preference")

        edit_pref?.setOnBindEditTextListener(OnBindEditTextListener { editText -> //workaround to make keyboard numeric and put cursor at end of input
            editText.inputType = InputType.TYPE_CLASS_NUMBER
            editText.setSelection(editText.length())//placing cursor at the end of the text
        })

        //all input processing moved to main fragment and persistent data handling
        //see previous commits for how to handle preferences within the fragment

    }

}