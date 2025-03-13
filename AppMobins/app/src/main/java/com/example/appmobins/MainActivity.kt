package com.example.appmobins

//from chaquoconsole
import GraphFrag
import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.FrameLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment
import androidx.fragment.app.commit
import androidx.preference.PreferenceManager
import androidx.recyclerview.widget.RecyclerView
import com.chaquo.python.PyException
import com.chaquo.python.PyObject
import com.chaquo.python.Python
import com.chaquo.python.android.AndroidPlatform
import com.fox2code.androidansi.AnsiParser
import com.google.android.material.navigation.NavigationView
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.io.InputStream
import java.io.OutputStream

class MainActivity : AppCompatActivity(), PrefFrag.OnDataPass,
    FileViewFrag.OnDataPass, GraphFrag.OnDataPass {

    var dataList = mutableListOf<String>("")

    private lateinit var recyclerView: RecyclerView
    private lateinit var c_adapter: CustomAdapter

    lateinit var drawerLayout: DrawerLayout //menu
    lateinit var actionBarDrawerToggle: ActionBarDrawerToggle //menu

    lateinit var pyInstance:Python

    lateinit var sharedPreferences: SharedPreferences
    val default_logsize = "500" //bytes
    val default_logtype = "All"
    var installMode : Boolean = true //flag is true if the app is run for the first time, flag for install mode

    var log_store:List<PyObject>?=null//used to let fileviewer pass object to log analyzer
    var graph_info=""


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.main_layout)

        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this)//persistent data

        dataList = mutableListOf<String>()

        //setting up python
        if (!Python.isStarted()) {
            Python.start(AndroidPlatform(this))
        }
        pyInstance= Python.getInstance()
        val module = pyInstance.getModule("main")
        module.put("activity", this) //pass itself to python in the "acivity" variable

        //copy assets over, only run on installation aka not normalRun aka first run
        //assets are c and wireshark packages
        val settings:SharedPreferences= getSharedPreferences("PREFS_NAME", 0)
        copyAssets()


        installMode = settings.getBoolean("FIRST_RUN", true)
        if (installMode) {
            copyAssets() //into files internal data folder
            Log.d("ASSETS", "copyAssets finished running")

            val spEditor:SharedPreferences.Editor = settings.edit()
            spEditor.putBoolean("FIRST_RUN", false)
            spEditor.apply()
        }


        //menu
        // drawer layout instance to toggle the menu icon to open
        // drawer and back button to close drawer
        drawerLayout = findViewById(R.id.drawerLayout)
        actionBarDrawerToggle =
            ActionBarDrawerToggle(this, drawerLayout, R.string.nav_open, R.string.nav_close)

        // pass the Open and Close toggle for the drawer layout listener
        // to toggle the button
        drawerLayout.addDrawerListener(actionBarDrawerToggle)
        actionBarDrawerToggle.syncState()

        // to make the Navigation drawer icon always appear on the action bar
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        // Set up NavigationView
        val navigationView: NavigationView = findViewById(R.id.nav_view)
        navigationView.setNavigationItemSelectedListener { menuItem ->
            handleNavigationItem(menuItem)
            true
        }


        //console init
        recyclerView = findViewById(R.id.recycler_view)
        c_adapter = CustomAdapter(dataList)
        recyclerView.adapter = c_adapter

//        findViewById<Button>(R.id.run_button) //debug
//            .setOnClickListener {
//                uiOutput(//GlobalVars.getGlobalVarValue()!!+
//                         ( sharedPreferences.all).toString() //just for debugging the preferences
//                )
//            }

        findViewById<Button>(R.id.sandbox_button) //debug
            .setOnClickListener {

//                //start another activity
//                val travelIntent =
//                    Intent(this@MainActivity, PageActivity::class.java)
//                startActivity(travelIntent)

                val bucketSize=10

                //run python
                val graph_module = pyInstance.getModule("getlog_graphdata")
                val ref_log_file = "bler_sample.mi2log"// "data_sample.mi2log"
                val log_res = graph_module.callAttr("getlog_graphdata", "/data/data/com.example.appmobins/files/"+ref_log_file,bucketSize)
                graph_info = log_res.toString()
                Log.d("graff", graph_info)

                //pack info into bundle
                val argBundle = Bundle() //init key value pair holder
                argBundle.putString("graph_file", ref_log_file)
                argBundle.putString("graph_info", graph_info)
                argBundle.putInt("bucket_size", bucketSize)

                findViewById<FrameLayout>(R.id.main_frame).setVisibility(View.GONE)

                //make new, set args
                val thisGraph = GraphFrag()
                thisGraph.arguments = argBundle

                //transactions
                supportFragmentManager.commit {

                    replace(R.id.fragment_holder, thisGraph, "Graph") //tag of actual Fragment
                    setReorderingAllowed(true) //not sure why this is needed

                    addToBackStack("Graph") // name of backStackEntry, one entry per commit

                }


            }

        findViewById<Button>(R.id.clear_button)
            .setOnClickListener {
                c_adapter.updateList(mutableListOf<String>()) //overwrite list with empty list to display as terminal output
            }

        findViewById<Button>(R.id.stop_button)
            .setOnClickListener {

                //halting through _stop_collection
                Log.d("halt", "halt start")
                module.callAttr("halt_daemon")
                Log.d("halt", "halt end")

                //writing
//                writeData("wahoo", "mytestfile.txt") //writing

                //reading
//                val output = readData("mytestfile.txt")
            }

        //runs MobileInsight core, OnlineMonitor
        findViewById<Button>(R.id.python_button).setOnClickListener {
            val pythread = Thread {
                // Simulate some work in the background
                Log.d("MainActivity", "Thread is running...")
                try {
                    val pystr = module.callAttr("main") //function call w arg
                    uiOutput(pystr.toJava(String::class.java))
                } catch (e: PyException) {
                    uiOutput("Error: ${e.message}")
                }

            }
            pythread.start()
        }

    }

    fun access_log_size():Float{ //accessor for settings called by python script
        val logSize = sharedPreferences.getString("edit_text_preference", default_logsize)
        return logSize!!.toFloat()
    }

    fun access_log_type():String{ //accessor for settings called by python script
        return sharedPreferences.getString("list_preference",default_logtype)+""
    }

    fun uiOutput(text: String?) { //console printer, called by kotlin and python
        runOnUiThread {
            c_adapter.addItems(listOf(text!!))
            c_adapter.addItems(listOf(" "))
            recyclerView.smoothScrollToPosition(c_adapter.getItemCount()-1)
            c_adapter.removeLast()
        }
    }

    override fun onDataPass(data: Pair<String, String>) { //fragment info pass
        Log.d("pass", data.toString())
        //currently unused since settings info is accessed via persistent memory now
    }

    override fun onFragDestroyed(){ //when pages are closed and we return to mainActivity screen
        val to_restore = findViewById<FrameLayout>(R.id.main_frame)

//fragment backstack debugging
//        var bstack_entry_name ="nahhh"
//        val bstack_ind = supportFragmentManager.getBackStackEntryCount()-1
//            if(bstack_ind>=0) {
//                val bstack_entry = supportFragmentManager.getBackStackEntryAt(bstack_ind)
//                bstack_entry_name = bstack_entry.getName()!!
//            }
//
//        Log.d("wuuuu",(bstack_ind+1).toString()+"===="+bstack_entry_name)

        //only make visible if all fragments are cleared
        if (supportFragmentManager.getBackStackEntryCount()==0) {
            if (to_restore.getVisibility() != View.VISIBLE)
                to_restore.setVisibility(View.VISIBLE)
            supportActionBar?.setTitle(R.string.app_name) //reset Appbar contents to mirror navigation
            supportActionBar?.setSubtitle("")
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean { //menu
        return if (actionBarDrawerToggle.onOptionsItemSelected(item)) {
            true
        } else super.onOptionsItemSelected(item)
    }

    private fun handleNavigationItem(menuItem: MenuItem) { //menu
        when (menuItem.itemId) {
            R.id.item1 -> { //settings page
                findViewById<FrameLayout>(R.id.main_frame).setVisibility(View.GONE)

                val myPref: PrefFrag? =
                    supportFragmentManager.findFragmentByTag("Pref") as PrefFrag?

//                val bstack_ind = supportFragmentManager.getBackStackEntryCount()-1 //fragment backstack debugging
//                if(bstack_ind>=0){
//                    val bstack_entry = supportFragmentManager.getBackStackEntryAt(bstack_ind)
//                    val huh:Fragment? = supportFragmentManager.findFragmentByTag(bstack_entry.getName())
//                    Log.d("froggie",
//                        bstack_entry.getName()
//                                +"**"
//                                +huh.toString()
//                                + "**"
//                                + huh?.isVisible.toString())
//                }

                if (myPref==null || !myPref.isVisible){ //avoid duplicates in frag backstack
                    supportFragmentManager.commit {

                        replace(R.id.fragment_holder, PrefFrag(), "Pref") //tag of actual Fragment
                        setReorderingAllowed(true) //not sure why this is needed

                        addToBackStack("Pref") // name of backStackEntry, one entry per commit

                    }
                }

            }
            R.id.item2 -> { //file browser
                val files_dir = this.getFilesDir().getPath() //debug

                findViewById<FrameLayout>(R.id.main_frame).setVisibility(View.GONE)

                val myBrowse: FileViewFrag? =
                    supportFragmentManager.findFragmentByTag("Browse") as FileViewFrag?

                val this_browse = FileViewFrag()
                val argBundle = Bundle() //init key value pair holder
                argBundle.putString("path", files_dir)//"/data/data/com.example.appmobins/") //hardcoded

                this_browse.setArguments(argBundle)

                if (myBrowse==null || !myBrowse.isVisible){ //avoid duplicates in frag backstack
                    supportFragmentManager.commit {
                        replace(R.id.fragment_holder, this_browse, "Browse") //tag of actual Fragment
                        setReorderingAllowed(true) //not sure why this is needed

                        addToBackStack("Browse") // name of backStackEntry, one entry per commit

                    }
                }
            }
            R.id.item3 -> { //home button
                for (i in 1..supportFragmentManager.getBackStackEntryCount())
                {supportFragmentManager.popBackStackImmediate()} //wipes fragment backstack
                onFragDestroyed() //manually call to deal with logfragments and logcontent fragments //TODO test again to check if needed
            }
        }
        // Close the drawer after handling the click
        drawerLayout.closeDrawers()
    }


    private fun copyAssets() { //for copying c and wireshark onto device at install time
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

            } catch (e: IOException) {
                Log.e("ASSETS", "Failed to copy asset file: $filename", e)
            }
        }
    }

    @Throws(IOException::class)
    private fun copyFile(`in`: InputStream, out: OutputStream) { //helper funciton to copyAssets
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
    } //vestigial

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
    } //vestigial
}

class CustomAdapter(private var lineList: MutableList<String>) : //manage console display
    RecyclerView.Adapter<CustomAdapter.ItemViewHolder>() {

    class ItemViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val lineTextView: TextView = itemView.findViewById(R.id.item_text)

        fun bind(word: String) {
            AnsiParser.setAnsiText(lineTextView, word,
            AnsiParser.FLAG_PARSE_DISABLE_SUBSCRIPT) //ansi coloring
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.consoleitem_layout, parent, false)
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

    fun removeLast() {
        val end_ind = lineList.size-1
        lineList.removeAt(end_ind)
        notifyItemRemoved(end_ind)
        notifyItemRangeChanged(end_ind, lineList.size)
    }

    // Update the list and notify the adapter
    fun updateList(newLineList: MutableList<String>) {
        lineList = newLineList
        notifyDataSetChanged()
    }
}

