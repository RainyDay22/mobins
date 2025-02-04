package com.example.appmobins

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment

class EtxFrag : Fragment() {

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

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val v = inflater.inflate(R.layout.etx_frag, container, false)

        var saved_text = "type here"
        val edit_text = v.findViewById<EditText>(R.id.etx_box)
        edit_text.setHint(saved_text)
        edit_text.setOnClickListener{
            edit_text.isCursorVisible=true
        }

        val activity = getActivity()
        v.findViewById<Button>(R.id.etx_submit)
            .setOnClickListener {
                saved_text=edit_text.getText().toString()

                passData(saved_text) //trial

                edit_text.isCursorVisible = false
                activity?.getCurrentFocus()?.let { view ->
                    val imm = activity.getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager
                    imm?.hideSoftInputFromWindow(view.windowToken, 0)
                }
                Toast.makeText(activity?.getApplicationContext(), saved_text, Toast.LENGTH_LONG).show();//debug
            }

        v.findViewById<Button>(R.id.etx_cancel)
            .setOnClickListener {

                edit_text.isCursorVisible = false
                activity?.getCurrentFocus()?.let { view ->
                    val imm =
                        activity.getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager
                    imm?.hideSoftInputFromWindow(view.windowToken, 0)
                }
                Toast.makeText(activity?.getApplicationContext(), saved_text, Toast.LENGTH_LONG)
                    .show();//debug
            }

                return v
    }
}
