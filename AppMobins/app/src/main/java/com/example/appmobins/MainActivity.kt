package com.example.appmobins

//from chaquoconsole
import android.app.Application
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment
import androidx.fragment.app.commit
import androidx.recyclerview.widget.RecyclerView
import com.chaquo.python.PyException
import com.chaquo.python.PyObject
import com.chaquo.python.Python
import com.chaquo.python.android.AndroidPlatform
import com.google.android.material.navigation.NavigationView
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

    var dataList = mutableListOf<String>("line 1")

    private lateinit var recyclerView: RecyclerView
    private lateinit var c_adapter: CustomAdapter

    lateinit var drawerLayout: DrawerLayout //menu ip, needed import
    lateinit var actionBarDrawerToggle: ActionBarDrawerToggle //menu ip, needed import

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.main_layout)

        GlobalVars.setGlobalVarValue("wow")
        val gVal = GlobalVars.getGlobalVarValue()

        //simulated output/program to run
        dataList = mutableListOf<String>("line 1", gVal!!)
        var dataArray = dataList.toTypedArray()

        //setting up python
        if (!Python.isStarted()) {
            Python.start(AndroidPlatform(this))
        }
        val py = Python.getInstance()
        val module = py.getModule("main")
        module.put("activity", this)

        //copy assets over
        copyAssets()
        Log.d("ASSETS", "copyAssets finished running")


        //menu ip
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
        //scroll init
        recyclerView = findViewById(R.id.recycler_view)
        c_adapter = CustomAdapter(dataArray)
        recyclerView.adapter = c_adapter

        findViewById<Button>(R.id.run_button)
            .setOnClickListener {
                Log.d("BUTTONS", "User tapped the Runbutton")
                output(GlobalVars.getGlobalVarValue()!!)
            }

        findViewById<Button>(R.id.travel_button)
            .setOnClickListener {
                val travelIntent =
                    Intent(this@MainActivity, PageActivity::class.java)//PageActivity::class.java)
                startActivity(travelIntent)
                Log.d("NAV", "Tried to nav to other activity")

            }

        findViewById<Button>(R.id.clear_button)
            .setOnClickListener {
                c_adapter.updateList(arrayOf<String>()) //overwrite list with emtpy list to display as terminal output
            }

//        findViewById<Button>(R.id.removeLast_button)
//            .setOnClickListener {
//                dataList.removeLastOrNull()
//                dataArray = dataList.toTypedArray()
//                recyclerView.adapter = CustomAdapter(dataArray)
//            }

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
                    output(pystr.toJava(String::class.java))
                } catch (e: PyException) {

                    output("Error: ${e.message}") //pykot
                }

            }
            pythread.start()  // Start the thread
        }

    }

    fun output(text: String?) {
        runOnUiThread {
            c_adapter.addItems(listOf(text!!))
            Log.d("output", "output function")
        }
    }



    override fun onOptionsItemSelected(item: MenuItem): Boolean { //menu
        return if (actionBarDrawerToggle.onOptionsItemSelected(item)) {
            true
        } else super.onOptionsItemSelected(item)
    }

    private fun handleNavigationItem(menuItem: MenuItem) { //menu
        when (menuItem.itemId) {
            R.id.item1 -> {
                loadFragment(PageFrag())
//                Toast.makeText(this, "Home selected", Toast.LENGTH_SHORT).show()
//                val travelIntent = Intent(this@MainActivity, PageActivity::class.java)//PageActivity::class.java)
//                startActivity(travelIntent)
//                Log.d("NAV", "Tried to nav to other activity")
            }
            R.id.item2 -> {
                Toast.makeText(this, "Profile selected", Toast.LENGTH_SHORT).show()
            }
            R.id.item3 -> {
                Toast.makeText(this, "Settings selected", Toast.LENGTH_SHORT).show()
            }
        }
        // Close the drawer after handling the click
        drawerLayout.closeDrawers()
    }

    private fun loadFragment(fragment: Fragment){
        supportFragmentManager.commit {
            replace(R.id.main_frag_frame, fragment)
            setReorderingAllowed(true)
            addToBackStack("name") // Name can be null
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

