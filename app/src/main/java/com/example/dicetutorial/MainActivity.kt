package com.example.dicetutorial

import android.nfc.tech.MifareUltralight
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.dicetutorial.ui.theme.DicetutorialTheme
import org.w3c.dom.Text
import androidx.compose.foundation.lazy.items


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            DicetutorialTheme {
                ConsoleArea(
                    Modifier
                        //.fillMaxSize()
                        .wrapContentSize())
                //DiceRollerApp()
            }
        }
    }
}


@Preview
@Composable
fun DiceRollerApp(){
    DiceWithButtonAndImage(
        Modifier
            .fillMaxSize()
            .wrapContentSize(Alignment.Center)
    )
}

@Composable
fun ConsoleArea(modifier:Modifier= Modifier
    ){
    var ahh by remember { mutableStateOf("blah") }
    var hi = remember {mutableListOf<String>("wah")}
    var count by remember { mutableStateOf(0) }
    Log.v("list", "holds ${hi}")
    var newstr:String

    Column (
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    )
    {
        Text(text = ahh)
        Row {
            Button(onClick = {
                newstr = fib(1,1,20,true)
                hi.add(newstr)
                count=count+1
                Log.v("list", "holds ${hi}")
                ahh = "lines printed ${count}"
            }){
                Text(text = "hit me")
            }
            Spacer(modifier = modifier)
            Button(onClick = {
                count =0
                ahh = "reset occurred"
                hi = mutableListOf<String>("bwah")
            }){
                Text(text = "reset")
            }
        }

        LazyColumn(modifier = modifier
            .heightIn(0.dp, 232.dp) //mention max height here
            .widthIn(0.dp, 232.dp) //mention max width here
            ){
            items(hi){
                line -> Text(text = line)
            }
        }
        Text(text = "bottom bottom")
    }

}

fun fib(l:Int, b4l:Int, len:Int, rep_last:Boolean):String{

    var last = l
    var b4last = b4l
    var temp:Int
    var accum =""

    if(rep_last){
        accum= accum+"${b4last} ${last} "
    }

    repeat(len-2){
        temp = last
        last = last+b4last
        b4last = temp
        accum = accum +"${last} "
    }
    return accum
}


@Composable
fun DiceWithButtonAndImage(modifier: Modifier = Modifier){
    var result by remember { mutableStateOf(1) }
    val imageResource = when (result){
        1 -> R.drawable.dice_1
        2 -> R.drawable.dice_2
        3 -> R.drawable.dice_3
        4 -> R.drawable.dice_4
        5 -> R.drawable.dice_5
        else -> R.drawable.dice_6
    }

    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ){
        Image(
            painter = painterResource(id = imageResource), contentDescription = result.toString())
        Spacer(modifier = Modifier.height(16.dp))
        Button(onClick = {result=(1..6).random()} ){
            Text(stringResource(id = R.string.roll))
        }
    }
}
