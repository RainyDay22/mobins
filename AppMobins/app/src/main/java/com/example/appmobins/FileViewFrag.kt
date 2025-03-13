package com.example.appmobins

import FileActionsFrag
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
import androidx.fragment.app.Fragment
import androidx.fragment.app.commit
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

//This page describes the FileBrowser page
class fileInfo(title:String, isFile:Boolean, size:Long, date:String){
    var _title:String =""
    var _isFile:Boolean = false
    var _size:Long = 0
    var _date:String =""

    init{
        _title = title
        _isFile = isFile
        _size = size
        _date = date
    }
}

fun convertLongToTime(time: Long): String { //standard Kotlin formatting
    val date = Date(time)
    val format = SimpleDateFormat("yyyy.MM.dd HH:mm", Locale.getDefault())
    return format.format(date)
}

class FileViewFrag : Fragment() {
    private var path: String = "/data/data/com.example.appmobins/" //hardcoded
    private lateinit var act:MainActivity

    override fun onCreate(savedInstanceState: Bundle?) { //python set up
        super.onCreate(savedInstanceState)
        act = activity as MainActivity //cast from FragmentActivity to MainActivity (aka my own class)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val v = inflater.inflate(R.layout.fileview_frag, container, false)

        val listView = v.findViewById<ListView>(R.id.file_list)

        // Use the current directory as subtitle
        path = getArguments()?.getString("path").toString()

        act.supportActionBar?.setTitle("File Viewer")
        act.supportActionBar?.setSubtitle(path) //more type finagling

        // Read all files sorted into the values array
        val values: MutableList<fileInfo> = mutableListOf()
        val dir = File(path)
        if (!dir.canRead()) {
            val title = act.getTitle()
            Toast.makeText(act.getApplicationContext(), "$title (inaccessible)", Toast.LENGTH_LONG).show()
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

                    if (file.endsWith(".mi2log") //handle file
                        or file.endsWith(".qmdl")){ //filter for log file extension

                        val fsize= File(filename).length() //in bytes
                        val fdate = File(filename).lastModified()

                        values.add(fileInfo(file, true, fsize, convertLongToTime(fdate)))
                    }
                    else{ //handle directory
                        //directory check
                        if (File(filename).isDirectory){
                            values.add(fileInfo(file, false, 0,""))
                            //currently we don't save size or date information avout directories
                        }
                    }
                }
            }
        }
        val sorted_values=values.sortedWith(compareBy<fileInfo>{it._title}.thenBy{it._date}) //optional

        // Put the data into the list
        class ListAdapter(context: Context, resource: Int, textViewResourceId:Int,  items: List<fileInfo>) :
            ArrayAdapter<fileInfo>(context, resource,textViewResourceId, items) {
            private val resourceLayout: Int
            private val mContext: Context

            init {
                resourceLayout = textViewResourceId
                mContext = context
            }

            @SuppressLint("ViewHolder") //there might be a better way of writing this
            override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {

                val vi = LayoutInflater.from(mContext)
                val cv = vi.inflate(resourceLayout, null)

                val file_info = getItem(position)

                if(file_info!=null){
                    //fetch xml elements
                    val icon: ImageView = cv.findViewById(R.id.file_icon)
                    val name:TextView = cv.findViewById(R.id.file_title)
                    val size:TextView = cv.findViewById(R.id.file_size)
                    val date:TextView = cv.findViewById(R.id.file_date)

                    //populate xml elements
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

        val adapter = ListAdapter(act.getApplicationContext()!!,
            R.layout.fileitem_layout,
            R.layout.fileitem_layout, sorted_values) //pass 'values' directly if chose no sorting
        listView.adapter = adapter


        //navigate into if directories, launch log analysis if files
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
                // make new fragment, make bundle, call fragment manager
                val thisBrowse = FileViewFrag()
                val argBundle = Bundle()
                argBundle.putString("path", filename)

                thisBrowse.setArguments(argBundle) //pass new file forward to next frag

                //fragment transaction
                val supportFragmentManager = act.supportFragmentManager
                supportFragmentManager.commit {
                    replace(R.id.fragment_holder, thisBrowse, "r_browse")
                    setReorderingAllowed(true)

                    addToBackStack("r_browse")
                }

                }
            else {
                //fragment transaction
                val thisActions = FileActionsFrag()

                //pack info into bundle
                val argBundle = Bundle() //init key value pair holder
                argBundle.putString("file", filename)
                argBundle.putString("file_title", file._title)
                thisActions.arguments = argBundle

                val supportFragmentManager = act.supportFragmentManager
                supportFragmentManager.commit {
                    replace(R.id.fragment_holder, thisActions, "f_actions")
                    setReorderingAllowed(true)
                    addToBackStack("f_actions")
                }
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

    fun passData(data: Pair<String,String>){ //currently unused
        dataPasser.onDataPass(data)
    }

}
