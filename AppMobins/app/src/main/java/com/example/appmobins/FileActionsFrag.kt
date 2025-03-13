import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.commit
import com.chaquo.python.PyException
import com.chaquo.python.PyObject
import com.chaquo.python.Python
import com.chaquo.python.android.AndroidPlatform
import com.example.appmobins.LogViewFrag
import com.example.appmobins.MainActivity
import com.example.appmobins.R

class FileActionsFrag : Fragment() {
    private lateinit var act:MainActivity
    private var file:String? = ""
    private var file_title:String?=""
    private var mac_info:String? = ""
    private var rlc_info:String? =""
    val bucketSize = 10


    private lateinit var file_pymodule: PyObject

    override fun onCreate(savedInstanceState: Bundle?) { //activity init
        super.onCreate(savedInstanceState)
        act = activity as MainActivity //cast from FragmentActivity to MainActivity (aka my own class)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val v = inflater.inflate(R.layout.fileaction_frag, container, false)
        //set the strings to the xml holders
        val launch_analyzer: TextView = v.findViewById(R.id.launch_analyzer)
        val graph_mac: TextView = v.findViewById(R.id.graph_mac)
        val graph_rlc: TextView = v.findViewById(R.id.graph_rlc)

        act.supportActionBar?.setTitle("Choose File Action")
        act.supportActionBar?.setSubtitle("")

        file = getArguments()?.getString("file")
        file_title = getArguments()?.getString("file_title")

        launch_analyzer.setOnClickListener{
            //launch analyzer
            //start python
            if (!Python.isStarted()) {
                Python.start(AndroidPlatform(act))
            }
            file_pymodule = act.pyInstance.getModule("read_milog")

            Toast.makeText(act.getApplicationContext(), "Thread launched, please wait", Toast.LENGTH_SHORT).show()


            var logList: MutableList<PyObject>? = null //hold run outputs
            val pythread = Thread {
                try {
                    val pystr = file_pymodule.callAttr("read_milog", file) //function call w arg

                    logList = pystr.asList() //cast

                    act.runOnUiThread{
                        act.log_store = logList //storing so that logview fragment can access it

                        //launch logview frag
                        val thisLog = LogViewFrag()

                        val argBundle = Bundle() //init key value pair holder
                        argBundle.putString("title", file_title)
                        thisLog.arguments=argBundle

                        //fragment transaction
                        val supportFragmentManager = act.supportFragmentManager
                        supportFragmentManager.commit {
                            replace(R.id.fragment_holder, thisLog, "l_log")
                            setReorderingAllowed(true)
                            addToBackStack("l_log")
                        }

                    }
                } catch (e: PyException) {
                    Log.d("analysis_frag","Error: ${e.message}")
                }
            }
            pythread.start()

        }

        graph_mac.setOnClickListener{
            Toast.makeText(act.getApplicationContext(), "MAC Thread launched, please wait", Toast.LENGTH_SHORT).show()

            //launch graph python, if mac info is empty
            if(mac_info =="") {
                //run python
                val graph_module = act.pyInstance.getModule("getlog_graphdata")
                val log_res = graph_module.callAttr(
                    "getlog_graphdata",
                    file,
                    bucketSize
                )
                val py_output= (log_res.toString()).split("=")
                mac_info=py_output[0]
                rlc_info=py_output[1]
            }

            //open graph frag
            //pack info into bundle
            val argBundle = Bundle()
            argBundle.putString("delay_type", "MAC")
            argBundle.putString("graph_file", file_title)
            argBundle.putString("graph_info", mac_info)
            argBundle.putInt("bucket_size", bucketSize)

            //make new, set args
            val thisGraph = GraphFrag()
            thisGraph.arguments = argBundle

            //transactions
            act.supportFragmentManager.commit {
                replace(R.id.fragment_holder, thisGraph, "g_graph") //tag of actual Fragment
                setReorderingAllowed(true)
                addToBackStack("g_graph") // name of backStackEntry, one entry per commit
            }
        }

        graph_rlc.setOnClickListener{
            Toast.makeText(act.getApplicationContext(), "RLC Thread launched, please wait", Toast.LENGTH_SHORT).show()

            //launch graph python, if rlc info is empty
            if(rlc_info =="") {
                //run python
                val graph_module = act.pyInstance.getModule("getlog_graphdata")
                val log_res = graph_module.callAttr(
                    "getlog_graphdata",
                    file,
                    bucketSize
                )
                val py_output= (log_res.toString()).split("=")
                mac_info=py_output[0]
                rlc_info=py_output[1]
            }

            //open graph frag
            //pack info into bundle
            val argBundle = Bundle() //init key value pair holder
            argBundle.putString("delay_type", "RLC")
            argBundle.putString("graph_file", file_title)
            argBundle.putString("graph_info", rlc_info)
            argBundle.putInt("bucket_size", bucketSize)

            //make new, set args
            val thisGraph = GraphFrag()
            thisGraph.arguments = argBundle

            //transactions
            act.supportFragmentManager.commit {
                replace(R.id.fragment_holder, thisGraph, "g_graph") //tag of actual Fragment
                setReorderingAllowed(true)
                addToBackStack("g_graph") // name of backStackEntry, one entry per commit
            }
        }

        return v
    }
}