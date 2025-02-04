package com.example.appmobins

import android.content.Context
import android.os.Bundle
import androidx.preference.PreferenceFragmentCompat


class PrefFrag : PreferenceFragmentCompat() {

    interface OnDataPass {
        fun onDataPass(data: String)
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

    fun passData(data: String){
        dataPasser.onDataPass(data)
    }

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        addPreferencesFromResource(R.xml.preferences)
    }

}