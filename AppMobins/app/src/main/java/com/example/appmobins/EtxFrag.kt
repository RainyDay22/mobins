package com.example.appmobins

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.ListView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.commit
import java.io.File

class EtxFrag : Fragment() {
    private var path: String? = "/data/data/com.example.appmobins/"

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val v = inflater.inflate(R.layout.etx_frag, container, false)

        val listView = v.findViewById<ListView>(R.id.file_list)

        // Use the current directory as title
        path = getArguments()?.getString("path")
        activity?.setTitle(path)

        // Read all files sorted into the values-array
        val values: MutableList<String> = mutableListOf()
        val dir = path?.let { File(it) }
        if (dir != null) {
            if (!dir.canRead()) {
                val title = activity?.getTitle()
                Toast.makeText(activity?.getApplicationContext(), "$title (inaccessible)", Toast.LENGTH_LONG).show()
            }
        }

        val list = dir?.list()
        if (list != null) {
            for (file in list) {
                if (!file.startsWith(".")) {
                    values.add(file)
                }
            }
        }
        values.sort()

        // Put the data into the list
        val adapter: ArrayAdapter<*> = ArrayAdapter(
            activity?.getApplicationContext()!!,
            R.layout.item_layout, R.id.item_text, values
        )
        listView.adapter = adapter


        //navigate into directories
        listView.onItemClickListener = AdapterView.OnItemClickListener { parent, view, position, id ->

            var filename = (listView.adapter).getItem(position) as String
            filename = if (path!!.endsWith(File.separator)) {
                path + filename
            } else {
                path + File.separator + filename
            }
            if (File(filename).isDirectory) {
                // make new etx, make bundle, call fragment manager
                val thisBrowse = EtxFrag()
                val argBundle = Bundle()
                argBundle.putString("path", filename)

                thisBrowse.setArguments(argBundle) //pass new file forward to next frag

                //fragment transaction
                val supportFragmentManager = activity?.getSupportFragmentManager()
                supportFragmentManager?.commit {
                    replace(R.id.fragment_holder, thisBrowse, "r_browse")
                    setReorderingAllowed(true)

                    addToBackStack("r_browse")
                }

                } else {
                Toast.makeText(activity?.getApplicationContext(), "$filename is not a directory", Toast.LENGTH_LONG).show()
            }
        }

        return v
    }

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

    fun passData(data: Pair<String,String>){
        dataPasser.onDataPass(data)
    }

//    override fun onCreateView(
//        inflater: LayoutInflater, container: ViewGroup?,
//        savedInstanceState: Bundle?
//    ): View {
//        val v = inflater.inflate(R.layout.etx_frag, container, false)
//
//        var saved_text = "type here"
//        val edit_text = v.findViewById<EditText>(R.id.etx_box)
//        edit_text.setHint(saved_text)
//        edit_text.setOnClickListener{
//            edit_text.isCursorVisible=true
//        }
//
//        val activity = getActivity()
//        v.findViewById<Button>(R.id.etx_submit)
//            .setOnClickListener {
//
//                if(saved_text!=edit_text.getText().toString()) {
//                    saved_text = edit_text.getText().toString()
//                    passData(Pair("log_cut_size",saved_text))
//                }
//
//                edit_text.isCursorVisible = false
//                activity?.getCurrentFocus()?.let { view ->
//                    val imm = activity.getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager
//                    imm?.hideSoftInputFromWindow(view.windowToken, 0)
//                }
//                Toast.makeText(activity?.getApplicationContext(), saved_text, Toast.LENGTH_LONG).show();//debug
//            }
//
//        v.findViewById<Button>(R.id.etx_cancel)
//            .setOnClickListener {
//
//                edit_text.isCursorVisible = false
//                activity?.getCurrentFocus()?.let { view ->
//                    val imm =
//                        activity.getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager
//                    imm?.hideSoftInputFromWindow(view.windowToken, 0)
//                }
//                Toast.makeText(activity?.getApplicationContext(), saved_text, Toast.LENGTH_LONG)
//                    .show();//debug
//            }
//
//                return v
//    }
}
