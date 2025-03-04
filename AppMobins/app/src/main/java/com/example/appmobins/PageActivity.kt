package com.example.appmobins

import android.content.Intent
import android.os.Bundle
import android.text.method.ScrollingMovementMethod
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.ListView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import java.io.File
import java.util.Collections


class PageActivity : AppCompatActivity() {
    private var path: String? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.logcontent_frag)

        val pay_text: TextView = findViewById(R.id.log_payload)
        pay_text.setMovementMethod(ScrollingMovementMethod())
        pay_text.setText("JAVA IS A TECHNOLOGY OF CHOICE FOR BUILDING APPLICATIONS" +
                " USING MANAGED CODES THAT CAN EXECUTE ON MOBILE DEVICES.\n" +
                "\n" +
                "Android is an open source software platform and Linux-based" +
                " operating system for mobile devices. The Android platform " +
                "allows developers to write managed code using Java to manage " +
                "and control the Android device. Android applications can be" +
                " developed by using the Java programming language and the " +
                "Android SDK. So, familiarity with the basics of the Java " +
                "programming language is a prerequisite for programming on" +
                " the Android platform. This article discusses where Java fits" +
                " in mobile application development and how we can use Java and" +
                " Android SDK to write applications that can work on Android devices.\n" +
                "\n" +
                "THE CHOICE OF JAVA\n" +
                "\n "+
                "What made Java be the technology of choice for mobile development for the" +
                " Android platform? The Java Programming Language emerged in the mid-1990s;" +
                " it was created by James Gosling of Sun Microsystems. Incidentally," +
                " Sun Microsystems was since bought by Oracle." +
                " Java has been widely popular the world over, " +
                "primarily because of a vast array of features it provides. " +
                "Java’s promise of “Write once and run anywhere” was one of the major" +
                " factors for the success of Java over the past few decades.\n" +
                "\n" +
                "Java even made inroads into embedded processors technology as well;" +
                " the Java Mobile Edition was built for creating applications " +
                "that can run on mobile devices. All these, added to Java’s meteoric rise," +
                " were the prime factors that attributed to the decision of adopting " +
                "Java as the primary development language for building " +
                "applications that run on Android. Java programs are secure because" +
                " they run within a sandbox environment. Programs written in Java are compiled " +
                "into intermediate code known as bytecode. This bytecode is then executed inside" +
                " the context of the Java Virtual Machine. You can learn more about Java from" +
                " this link.\n" +
                "\n" +
                "USING JAVA FOR BUILDING MOBILE APPLICATIONS\n")

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
