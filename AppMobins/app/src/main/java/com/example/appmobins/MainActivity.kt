package com.example.appmobins

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.recyclerview.widget.RecyclerView
import com.example.appmobins.ui.theme.AppMobinsTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.main_layout)

        val dataList= mutableListOf<String>("line 1", "line 2", "line 3","line 1", "line 2", "line 3","line 1", "line 2", "line 3","line 1", "line 2", "line 3","line 1", "line 2", "line 3","line 1", "line 2", "line 3")
//        var dataList= mutableListOf<String>()
        dataList.add(fib(1,1,5,true))
        var dataArray = dataList.toTypedArray()


        val recyclerView: RecyclerView = findViewById(R.id.recycler_view)
        recyclerView.adapter = CustomAdapter(dataArray)


//        setContent {
//            AppMobinsTheme {
//                // A surface container using the 'background' color from the theme
//                Surface(
//                    modifier = Modifier.fillMaxSize(),
//                    color = MaterialTheme.colorScheme.background
//                ) {
//                    Greeting("Android")
//                }
//            }
//        }
    }
}

class CustomAdapter(val lineList: Array<String>) :
    RecyclerView.Adapter<CustomAdapter.ItemViewHolder>() {

    // Describes an item view and its place within the RecyclerView
    class ItemViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val lineTextView: TextView = itemView.findViewById(R.id.item_text)

        fun bind(word: String) {
            lineTextView.text = word
        }
    }

    // Returns a new ViewHolder
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_layout, parent, false)

        return ItemViewHolder(view)
    }

    // Returns size of data list
    override fun getItemCount(): Int {
        return lineList.size
    }

    // Displays data at a certain position
    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        holder.bind(lineList[position])
    }
}

fun fib(l:Int, b4l:Int, len:Int, rep_last:Boolean):String{

    var last = l
    var b4last = b4l
    var temp:Int
    var accum =""

    if(rep_last){ accum= accum+"${b4last} ${last} "}

    repeat(len-2){
        temp = last
        last = last+b4last
        b4last = temp
        accum = accum +"${last} "
    }
    return accum
}

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(
        text = "Hello $name!",
        modifier = modifier
    )
}

//@Preview(showBackground = true) //quite useless in the context of not using Jetpack Compose
//@Composable
//fun GreetingPreview() {
//    AppMobinsTheme {
//        Greeting("Android")
//    }
//}