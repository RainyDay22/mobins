package com.example.appmobins

//from chaquoconsole
import android.app.Application
import android.content.Context
import android.content.Intent
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
import com.chaquo.python.Python
import com.chaquo.python.android.AndroidPlatform
import com.fox2code.androidansi.AnsiParser
import com.google.android.material.navigation.NavigationView
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.io.InputStream
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

class MainActivity : AppCompatActivity(), PrefFrag.OnDataPass, EtxFrag.OnDataPass {

    var dataList = mutableListOf<String>("line 1") //for debugging

    private lateinit var recyclerView: RecyclerView
    private lateinit var c_adapter: CustomAdapter

    lateinit var drawerLayout: DrawerLayout //menu ip, needed import
    lateinit var actionBarDrawerToggle: ActionBarDrawerToggle //menu ip, needed import

    lateinit var sharedPreferences: SharedPreferences
    var installMode : Boolean = true //flag is true if the app is run for the first time, flag for install mode


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.main_layout)

        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this)//persistent data

        GlobalVars.setGlobalVarValue("wow") //vestigial
        val gVal = GlobalVars.getGlobalVarValue() //vestigial

        //simulated output/program to run
        dataList = mutableListOf<String>("line 1", gVal!!) //debug

        //setting up python
        if (!Python.isStarted()) {
            Python.start(AndroidPlatform(this))
        }
        val py = Python.getInstance()
        val module = py.getModule("main")
        module.put("activity", this)

        //copy assets over, only run on installation aka not normalRun aka first run
        //assets are c and wireshark packages
        var settings:SharedPreferences= getSharedPreferences("PREFS_NAME", 0)
        installMode = settings.getBoolean("FIRST_RUN", true)
        if (installMode) {
            copyAssets()
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

        findViewById<Button>(R.id.run_button)
            .setOnClickListener {
                uiOutput(GlobalVars.getGlobalVarValue()!!
                        + ( sharedPreferences.all).toString() //just for debugging the preferences
                )
            }

        findViewById<Button>(R.id.travel_button)
            .setOnClickListener {
                val travelIntent =
                    Intent(this@MainActivity, PageActivity::class.java)
                startActivity(travelIntent)
                Log.d("NAV", "Tried to nav to other activity")

            }

        findViewById<Button>(R.id.clear_button)
            .setOnClickListener {
                c_adapter.updateList(mutableListOf<String>()) //overwrite list with emtpy list to display as terminal output
            }

        findViewById<Button>(R.id.Stop_button)
            .setOnClickListener {

                //halting through _stop_collection
                Log.d("halt", "halt start")
                module.callAttr("halt_daemon")
                Log.d("halt", "halt end")

                //writing
//                writeData("wahoo", "mytestfile.txt") //writing

                //reading
//                val output = readData("mytestfile.txt")
//                dataList.add(output)
//                dataArray = dataList.toTypedArray()
//                recyclerView.adapter = CustomAdapter(dataArray)
            }

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
        val logSize = sharedPreferences.getString("edit_text_preference", "500")
        return logSize!!.toFloat()
    }

    fun access_log_type():String{ //accessor for settings called by python script
        return sharedPreferences.getString("list_preference","All")+""
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
        Log.d("pass","hello " + data)
        //currently unused since settings info is accessed via persistent memory now

    }

    override fun onFragDestroyed(){ //when pages are closed and we return to mainActivity screen
        val to_restore = findViewById<FrameLayout>(R.id.main_frame)

        //only make visible if all fragments are cleared
        if (supportFragmentManager.getBackStackEntryCount()==0) {
            if (to_restore.getVisibility() != View.VISIBLE)
                to_restore.setVisibility(View.VISIBLE)
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
            R.id.item2 -> {
                Toast.makeText(this, "Open File Browser", Toast.LENGTH_SHORT).show()
                val files_dir = this.getFilesDir().getPath() //debug

                findViewById<FrameLayout>(R.id.main_frame).setVisibility(View.GONE)

                val myBrowse: EtxFrag? =
                    supportFragmentManager.findFragmentByTag("Browse") as EtxFrag?

                val this_browse = EtxFrag()
                val argBundle = Bundle() //init key value pair holder
                argBundle.putString("path", "/data/data/com.example.appmobins/") //hardcoded

                this_browse.setArguments(argBundle)

                if (myBrowse==null || !myBrowse.isVisible){ //avoid duplicates in frag backstack
                    supportFragmentManager.commit {
                        add(R.id.fragment_holder, this_browse, "Browse") //tag of actual Fragment
                        setReorderingAllowed(true) //not sure why this is needed

                        addToBackStack("Browse") // name of backStackEntry, one entry per commit

                    }
                }
            }
            R.id.item3 -> { //back button
                for (i in 1..supportFragmentManager.getBackStackEntryCount())
                {supportFragmentManager.popBackStack()} //wipes fragment backstack
            }
        }
        // Close the drawer after handling the click
        drawerLayout.closeDrawers()
    }

    private fun replaceFragment(fragment: Fragment){
        supportFragmentManager.commit {
            replace(R.id.main_frame, fragment)
            setReorderingAllowed(true) //not sure why this is needed
            addToBackStack("1st frag") // Name can be null
        }
    } //vestigial


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

