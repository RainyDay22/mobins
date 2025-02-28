package com.example.appmobins

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.drawable.Icon.createWithResource
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.ListView
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.commit
import com.chaquo.python.PyException
import com.chaquo.python.PyObject
import com.chaquo.python.Python
import com.chaquo.python.android.AndroidPlatform
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

//class logInfo(title:String, size:Long, date:String){
//    var _title:String =""
//    var _size:Long = 0
//    var _date:String ="" //unclear how the thing works yet
//
//    init{
//        _title = title
//        _size = size
//        _date = date
//    }
//}

class LogViewFrag : Fragment() {
    private var path: String = "/data/data/com.example.appmobins/" //tt
    private lateinit var act:MainActivity
    private var log_source: List<PyObject>? =null //will hold all the msg_logs returned by LogAnalyzer

    override fun onCreate(savedInstanceState: Bundle?) { //activity init
        super.onCreate(savedInstanceState)
        act = activity as MainActivity //cast from FragmentActivity to MainActivity (aka my own class)
        log_source = act.log_debug
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        val v = inflater.inflate(R.layout.logview_frag, container, false)

        val listView = v.findViewById<ListView>(R.id.log_list)

        // Use the current log file as subtitle?? tt
//        path = getArguments()?.getString("path").toString()

        act.supportActionBar?.setTitle("Log Browser")
//        act.supportActionBar?.setSubtitle(path)

        // Put the data into the list
        class ListAdapter(context: Context, resource: Int, textViewResourceId:Int,  items: List<PyObject>) :
            ArrayAdapter<PyObject>(context, resource,textViewResourceId, items) {
            private val resourceLayout: Int
            private val mContext: Context

            init {
                resourceLayout = textViewResourceId
                mContext = context
            }

            @SuppressLint("ViewHolder") //not sure why but it works
            override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
//                var cv = convertView
//
//                if (cv==null){
//                    val vi = LayoutInflater.from(mContext)
//                    cv = vi.inflate(resourceLayout, null)
//                } //not sure why but it works

                val vi = LayoutInflater.from(mContext)
                val cv = vi.inflate(resourceLayout, null)

                val log_obj: PyObject? = getItem(position)

                if(log_obj!=null){
                    val log_info: Map<String, String> = log_obj.asMap() as Map<String, String> //unchecked cast
                    val index: TextView = cv.findViewById(R.id.log_index)
                    val name:TextView = cv.findViewById(R.id.log_type)
//                    val size:TextView = cv.findViewById(R.id.log_size) //tt

                    //tt
//                    size.setText((log_info['Payload']).toString())
                    index.setText(position.toString())
                    name.setText(log_info["TypeID"].toString())

                }

                return cv!! //trust me it won't be null
            }
        }

        if (log_source!=null){
            val adapter = ListAdapter(act.getApplicationContext()!!, R.layout.logitem_layout, R.layout.logitem_layout, log_source!!)
            listView.adapter = adapter
        }
        else {
            act.runOnUiThread {
                Toast.makeText(
                    act.getApplicationContext(),
                    "bundle is weird",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }


        //navigate into directories
        listView.onItemClickListener = AdapterView.OnItemClickListener { parent, view, position, id ->
            //handle the dialog opening stuff here
        }

        return v
    }
}
