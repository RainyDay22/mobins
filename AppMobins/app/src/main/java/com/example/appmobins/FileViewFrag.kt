package com.example.appmobins

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.drawable.Icon.createWithResource
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.ListView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.commit
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class fileInfo(title:String, isFile:Boolean, size:Long, date:String){
    var _title:String =""
    var _isFile:Boolean = false
    var _size:Long = 0
    var _date:String ="" //unclear how the thing works yet

    init{
        _title = title
        _isFile = isFile
        _size = size
        _date = date
    }
}

fun convertLongToTime(time: Long): String {
    val date = Date(time)
    val format = SimpleDateFormat("yyyy.MM.dd HH:mm", Locale.getDefault())
    return format.format(date)
}

class FileViewFrag : Fragment() {
    private var path: String = "/data/data/com.example.appmobins/"

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val v = inflater.inflate(R.layout.fileview_frag, container, false)

        val listView = v.findViewById<ListView>(R.id.file_list)

        // Use the current directory as title
        path = getArguments()?.getString("path").toString()
        activity?.setTitle("File Viewer")
        val act = activity as AppCompatActivity
        act.supportActionBar?.setSubtitle(path) //more type finagling

        // Read all files sorted into the values-array
        val values: MutableList<fileInfo> = mutableListOf()
        val dir = File(path)
        if (!dir.canRead()) {
            val title = activity?.getTitle()
            Toast.makeText(activity?.getApplicationContext(), "$title (inaccessible)", Toast.LENGTH_LONG).show()
        }

        //store files into values container
        values.add(fileInfo("..", false, 0,"")) //parent dir
        val list = dir.list()
        if (list != null) {
            for (file in list) {
                if (!file.startsWith(".")) {
                    //formatting filename for File API functions
                    val filename = if (path.endsWith(File.separator)) {
                        path + file } else { path + File.separator + file }
                    if (file.endsWith(".mi2log")
                        or file.endsWith(".qmdl")){ //filter for log file extension

                        val fsize= File(filename).length() //in bytes
                        val fdate = File(filename).lastModified()

                        values.add(fileInfo(file, true, fsize,convertLongToTime(fdate)))
                    }
                    else{ //handle directory
                        //directory check
                        if (File(filename).isDirectory){
                            values.add(fileInfo(file, false, 0,""))
                        }
                    }
                }
            }
        }
//        values.sort() //optional

        // Put the data into the list
        class ListAdapter(context: Context, resource: Int, textViewResourceId:Int,  items: List<fileInfo>) :
            ArrayAdapter<fileInfo>(context, resource,textViewResourceId, items) {
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

                val file_info = getItem(position)

                if(file_info!=null){
                    val icon: ImageView = cv.findViewById(R.id.file_icon) //uhhh more sus type overriding
                    val name:TextView = cv.findViewById(R.id.file_title)
                    val size:TextView = cv.findViewById(R.id.file_size)
                    val date:TextView = cv.findViewById(R.id.file_date)

                    if(file_info._isFile) {
                        icon.setImageIcon(createWithResource(mContext,R.drawable.baseline_file_24))
                        size.setText((file_info._size).toString())
                        date.setText(file_info._date)
                    }
                    name.setText(file_info._title)

                }

                return cv!! //trust me it won't be null
            }
        }

        val adapter = ListAdapter(activity?.getApplicationContext()!!, R.layout.fileitem_layout, R.layout.fileitem_layout, values)
        listView.adapter = adapter


        //navigate into directories
        listView.onItemClickListener = AdapterView.OnItemClickListener { parent, view, position, id ->

            val file = (listView.adapter).getItem(position) as fileInfo
            var filename = file._title

            filename = if (filename==".."){ //handle parent
                if(path.endsWith(File.separator)){
                    path.substring(0,path.length-1) //remove last file separator
                }
                path.substring(0,path.lastIndexOf(File.separator))
            } else{ //normal file formatting
                if (path.endsWith(File.separator)) { //formatting
                    path + filename
                } else {
                    path + File.separator + filename
                }
            }

            if (File(filename).isDirectory) {
                // make new etx, make bundle, call fragment manager
                val thisBrowse = FileViewFrag()
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

    interface OnDataPass { //vestigial start
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
    } //vestigial end

//    override fun onCreateView( //vestigial edit text
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
