package com.example.appmobins

//from chaquoconsole
import android.app.Application
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import com.chaquo.python.PyException
import com.chaquo.python.PyObject
import com.chaquo.python.Python
import com.chaquo.python.android.AndroidPlatform
import java.io.BufferedReader
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.io.InputStream
import java.io.InputStreamReader
import java.io.OutputStream

class GlobalVars : Application() {
    companion object {
        private var mGlobalVarValue: String? = null
        private var switchState: Boolean = false
        fun getGlobalVarValue(): String? {
            return mGlobalVarValue
        }

        fun getSwitchState(): Boolean{return switchState}

        fun setGlobalVarValue(str: String?) {
            mGlobalVarValue = str
        }

        fun setSwitchState(state: Boolean){
            switchState=state}
    }
}

class MainActivity : AppCompatActivity() {

    var dataList= mutableListOf<String>("line 1")

    private lateinit var sys: PyObject
    private lateinit var stdout: PyObject
    private var realStdout: PyObject? = null

    private lateinit var recyclerView:RecyclerView
    private lateinit var c_adapter:CustomAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.main_layout)

        GlobalVars.setGlobalVarValue("wow")
        val gVal = GlobalVars.getGlobalVarValue()

        Log.d("kotlin", "probably another null problem")
        //simulated output/program to run
//        val dataList= mutableListOf<String>("line 1", gVal!!)
        dataList= mutableListOf<String>("line 1", gVal!!)
        var dataArray = dataList.toTypedArray()

        //setting up python
        if (! Python.isStarted()) {
            Python.start(AndroidPlatform(this))
        }
        val py = Python.getInstance()
        val module = py.getModule("main")

        //copy assets over
        copyAssets()
        Log.d("ASSETS", "copyAssets finished running")


        //scroll init
//        var recyclerView: RecyclerView = findViewById(R.id.recycler_view)
        recyclerView = findViewById(R.id.recycler_view)
        c_adapter = CustomAdapter(dataArray)
        recyclerView.adapter = c_adapter

//        output2("hiya", recyclerView)

        //io section
        val console = py.getModule("stdio_redirect")
//        val sys = py.getModule("sys")
        sys = py.getModule("sys")
//        val stdout: PyObject
//        val stderr: PyObject
//        val realStdout: PyObject?
//        val realStderr: PyObject?

        Log.d("std_io", "entered io func")
        realStdout = sys["stdout"]
//        realStderr = sys["stderr"]
        fun redirectOutput(stream: PyObject?, methodName: String): PyObject {
            Log.d("std_io", "entered redirect func")
            return console.callAttr("ConsoleOutputStream", stream, this, methodName)//, recyclerView)
//            return console.callAttr("ConsoleOutputStream", stream, this, methodName)
        }
        stdout = redirectOutput(realStdout, "output")
//        stderr = redirectOutput(realStderr, "outputError")
        Log.d("std_io", "end io func")

        findViewById<Button>(R.id.run_button)
            .setOnClickListener {
                Log.d("BUTTONS", "User tapped the Runbutton")

//                dataList.add(GlobalVars.getGlobalVarValue()!!)
//                dataArray = dataList.toTypedArray()
//                recyclerView.adapter = CustomAdapter(dataArray)
                c_adapter.addItems(listOf(GlobalVars.getGlobalVarValue()!!))
            }

//        findViewById<Button>(R.id.travel_button)
//            .setOnClickListener {
//                val travelIntent = Intent(this@MainActivity, PageActivity::class.java)
//                startActivity(travelIntent)
//                Log.d("NAV", "Tried to nav to other activity")
//
//            }
//
//        findViewById<Button>(R.id.clear_button)
//            .setOnClickListener {
//                dataList.clear()
//                dataArray = dataList.toTypedArray()
//                recyclerView.adapter = CustomAdapter(dataArray)
//            }

//        findViewById<Button>(R.id.removeLast_button)
//            .setOnClickListener {
//                dataList.removeLastOrNull()
//                dataArray = dataList.toTypedArray()
//                recyclerView.adapter = CustomAdapter(dataArray)
//            }

        findViewById<Button>(R.id.Stop_button)
            .setOnClickListener {

                // stopping diag_revealer
                stopCollection()

                //writing
//                writeData("wahoo", "mytestfile.txt") //writing

                //reading
//                val output = readData("mytestfile.txt")
//                dataList.add(output)
//                dataArray = dataList.toTypedArray()
//                recyclerView.adapter = CustomAdapter(dataArray)
            }

        findViewById<Button>(R.id.python_button).setOnClickListener {
            val thread = Thread {
                // Simulate some work in the background
                Log.d("MainActivity", "Thread is running...")
                try {
                    val pystr = module.callAttr("main") //function call w arg
                    dataList.add(pystr.toJava(String::class.java))
                    } catch (e: PyException) {
//                    Toast.makeText(this, e.message, Toast.LENGTH_LONG).show()
                    e.message?.let { it1 -> dataList.add(it1) }
                }

                // Update the UI on the main thread
                runOnUiThread {
                    dataArray = dataList.toTypedArray()
                    recyclerView.adapter = CustomAdapter(dataArray)
                    Log.d("MainActivity", "Updating UI from the background thread!")
                }
            }
            thread.start()  // Start the thread
        }

    }

    fun output(text:String?) {
//        dataList.add(text.toString())
//        val dataArray = dataList.toTypedArray()
//        recyclerView.adapter = CustomAdapter(dataArray)
        c_adapter.addItems(listOf(text!!))
        Log.d("output", "output function")
    }

//    fun output(text:String?){
//        Log.d("output", "huhhhhh "+text)
//        Toast.makeText(this, text, Toast.LENGTH_LONG).show()
//    }

    override fun onResume() {
        super.onResume()
        sys["stdout"] = stdout
    }

    override fun onPause() {
        super.onPause()
        if (!isChangingConfigurations) {
            sys["stdout"] = realStdout
        }
    }

    private fun copyAssets() {
        Log.d("ASSETS", "start copyassets run")

        val assetManager =  assets //getAssets()
        var files: Array<String>? = null
        try {
            files = assetManager.list("")
        } catch (e: IOException) {
            Log.e("ASSETS", "Failed to get asset file list.", e)
        }
        for (filename in files!!) {
            var `in`: InputStream? = null
            var out: OutputStream? = null
            try {
                `in` = assetManager.open(filename)

//                val outDir = Environment.getExternalStorageDirectory().absolutePath + "/X/Y/Z/" //external storage
                val outDir = getFilesDir().getAbsolutePath() //internal storage
                val outFile = File(outDir, filename)
                out = FileOutputStream(outFile)
                copyFile(`in`, out)
                `in`.close()
                `in` = null
                out.flush()
                out.close()
                out = null
                Log.d("ASSETS", "copied over $filename")
                /////////// AHHHHHH exec permission
                if (!outFile.canExecute()) {
                    Log.d("ASSETS_p", "File is not executable, trying to make it executable ...")
                    if (outFile.setExecutable(true, false)) {
                        Log.d("ASSETS_p", "File is executable")
                    } else {
                        Log.d("ASSETS_p", "Failed to make the File executable")
                    }
                } else {
                    Log.d("ASSETS_p", "File is already executable");
                }

                //////////AHHHH

            } catch (e: IOException) {
                Log.e("ASSETS", "Failed to copy asset file: $filename", e)
            }
        }
    }

    @Throws(IOException::class)
    private fun copyFile(`in`: InputStream, out: OutputStream) {
        val buffer = ByteArray(1024)
        var read: Int
        while (`in`.read(buffer).also { read = it } != -1) {
            out.write(buffer, 0, read)
        }
    }

    private fun writeData(words:String, filename: String) {
        val data = words
        if (data.isBlank()) {
            Toast.makeText(this, "Input is empty", Toast.LENGTH_SHORT).show()
            return
        }

        try {
            openFileOutput(filename, Context.MODE_PRIVATE).use { fos ->
                fos.write(data.toByteArray())
            }
            Toast.makeText(this, "Data written to file", Toast.LENGTH_SHORT).show()
        } catch (e: IOException) {
            e.printStackTrace()
            Toast.makeText(this, "Failed to write data", Toast.LENGTH_SHORT).show()
        }
    }

    private fun readData(filename :String) :String{
        var text_out:String="did not read yet";
        try {
            val content = openFileInput(filename).bufferedReader().use { it.readText() }
            text_out = content;
            Toast.makeText(this, "Data read from file", Toast.LENGTH_SHORT).show()
        } catch (e: IOException) {
            e.printStackTrace()
            Toast.makeText(this, "Failed to read data", Toast.LENGTH_SHORT).show()
        }
        return text_out
    }
}

class CustomAdapter(private var lineList: Array<String>) :
    RecyclerView.Adapter<CustomAdapter.ItemViewHolder>() {

    class ItemViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val lineTextView: TextView = itemView.findViewById(R.id.item_text)

        fun bind(word: String) {
            lineTextView.text = word
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_layout, parent, false)
        return ItemViewHolder(view)
    }

    override fun getItemCount(): Int {
        return lineList.size
    }

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        holder.bind(lineList[position])
    }

    fun addItems(newItems: List<String>) {
        val startPosition = lineList.size
        lineList += newItems
        notifyItemRangeInserted(startPosition, newItems.size)
    }

    // Update the list and notify the adapter
    fun updateList(newLineList: Array<String>) {
        lineList = newLineList
        notifyDataSetChanged()
    }
}


fun stopCollection() {
    // Run the "ps" command
    val processBuilder = ProcessBuilder("su","-c","ps","-e")
    val process = processBuilder.start()
    Log.d("stopbutton","built process")

    // Capture the output
    val res = BufferedReader(InputStreamReader(process.inputStream)).readLines()

    for (item in res) {
        Log.d("stopbutton", item)
        if (item.contains("diag_revealer")) {
                // Split the line to extract the PID
                val pid = item.split("\\s+".toRegex())[1]
                val cmd = "kill $pid"
                Log.d("stopbutton", "kill command is "+cmd)

                // Run the "kill" command
                ProcessBuilder("su", "-c", cmd).start()
            }
        }
    }

