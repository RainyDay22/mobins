package com.example.appmobins

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.ListView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import java.io.File
import java.util.Collections


class PageActivity : AppCompatActivity() {
    private var path: String? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.fileview_frag)
        val listView = findViewById<ListView>(R.id.file_list)

        // Use the current directory as title
        path = "/data/data/com.example.appmobins/"
        if (intent.hasExtra("path")) {
            //^ recursive response to intent being called in this activity
            path = intent.getStringExtra("path")
        }
        title = path

        // Read all files sorted into the values-array
        val values: MutableList<String> = mutableListOf()
        val dir = path?.let { File(it) }
        if (dir != null) {
            if (!dir.canRead()) {
                title = "$title (inaccessible)"
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
            this,
            R.layout.item_layout, R.id.item_text, values
        )
        listView.adapter = adapter


        listView.onItemClickListener = AdapterView.OnItemClickListener { parent, view, position, id ->

            var filename = (listView.adapter).getItem(position) as String
            filename = if (path!!.endsWith(File.separator)) {
                path + filename
            } else {
                path + File.separator + filename
            }
            if (File(filename).isDirectory) {
                val intent = Intent(this@PageActivity, PageActivity::class.java)
                intent.putExtra("path", filename)
                startActivity(intent)
            } else {
                Toast.makeText(this, "$filename is not a directory", Toast.LENGTH_LONG).show()
            }
        }
    }

//    override fun onListItemClick(l: ListView, v: View, position: Int, id: Long) {
//        var filename = listAdapter.getItem(position) as String
//        filename = if (path!!.endsWith(File.separator)) {
//            path + filename
//        } else {
//            path + File.separator + filename
//        }
//        if (File(filename).isDirectory) {
//            val intent = Intent(this, ListFileActivity::class.java)
//            intent.putExtra("path", filename)
//            startActivity(intent)
//        } else {
//            Toast.makeText(this, "$filename is not a directory", Toast.LENGTH_LONG).show()
//        }
//    }
}
//class PageActivity : AppCompatActivity() {
//    override fun onCreate(savedInstanceState: Bundle?){
//        super.onCreate(savedInstanceState)
//        setContentView(R.layout.page_layout)
//
//        findViewById<Button>(R.id.back_button)
//            .setOnClickListener {
//                Log.d("NAV", "Ended new activity")
//                finish()
//            }
//
//        findViewById<com.google.android.material.switchmaterial.SwitchMaterial>(R.id.material_switch)
//            .setChecked(GlobalVars.getSwitchState())
//
//        findViewById<com.google.android.material.switchmaterial.SwitchMaterial>(R.id.material_switch)
//            .setOnCheckedChangeListener { _, isChecked ->
//            if (isChecked) {
//                // The switch is checked.
//                Log.d("Switch", "switch toggled on")
//                GlobalVars.setGlobalVarValue("changed!")
//                Log.d("GlobalVars", GlobalVars.getGlobalVarValue()!!)
//            } else {
//                // The switch isn't checked.
//                Log.d("Switch", "switch toggled off")
//                GlobalVars.setGlobalVarValue("changed back!")
//                Log.d("GlobalVars", GlobalVars.getGlobalVarValue()!!)
//            }
//                GlobalVars.setSwitchState(isChecked)
//            }
//
//
//        var saved_text = "type here"
//        val edit_text = findViewById<EditText>(R.id.etx_box)
//        edit_text.setHint(saved_text)
//        edit_text.setOnClickListener{
//            edit_text.isCursorVisible=true
//        }
//
//        findViewById<Button>(R.id.etx_submit)
//            .setOnClickListener {
//                saved_text=edit_text.getText().toString()
//                edit_text.isCursorVisible = false
//                this.currentFocus?.let { view ->
//                    val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager
//                    imm?.hideSoftInputFromWindow(view.windowToken, 0)
//                }
//                Toast.makeText(getApplicationContext(), saved_text, Toast.LENGTH_LONG).show();//display the text that you entered in edit text
//            }
//
//        findViewById<Button>(R.id.etx_cancel)
//            .setOnClickListener {
//                edit_text.isCursorVisible = false
//                this.currentFocus?.let { view ->
//                    val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager
//                    imm?.hideSoftInputFromWindow(view.windowToken, 0)
//                }
//                edit_text.setText(saved_text)
//                Toast.makeText(getApplicationContext(), saved_text, Toast.LENGTH_LONG).show();//display the text that you entered in edit text
//            }
//
//
//    }
//}
