import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.example.appmobins.MainActivity
import com.example.appmobins.R
import com.github.mikephil.charting.charts.BarChart
import com.github.mikephil.charting.components.XAxis.XAxisPosition
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.interfaces.datasets.IBarDataSet

//enum class DelayType() {MAC, RCL}

class GraphFrag : Fragment() {
    private lateinit var act: MainActivity
    private var graph_source:String? ="some_file_name" //will hold all the msg_logs returned by LogAnalyzer
    private var delay_type:String? = "MAC" //should only take 2 values ("MAC" or "RLC")
    //a safer implementation would use enums (see above)

    private lateinit var chart: BarChart
    private var bucketSize:Int = 1

    private var x_val=listOf(0f)
    private var y_val= listOf(0f)
    private var retx_avg = "0.0"

    override fun onAttach(context: Context) {
        super.onAttach(context)
        dataPasser = context as OnDataPass

        act = activity as MainActivity

        graph_source = getArguments()?.getString("graph_file")
        delay_type = getArguments()?.getString("delay_type")
        val graph_str = getArguments()?.getString("graph_info")
        parseStringToText(graph_str)
        bucketSize = requireArguments().getInt("bucket_size")

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val v = inflater.inflate(R.layout.graph_frag, container, false)

        act.supportActionBar?.setTitle(delay_type+" Delay Analysis")
        act.supportActionBar?.setSubtitle(graph_source)

        //set the strings to the xml holders
        val text1: TextView = v.findViewById(R.id.text1)
        text1.setText("Average "+delay_type+ " retx delay is: "+ retx_avg)

        //add colorful visual cues
        if(delay_type=="MAC"){
            text1.setBackgroundColor(
                ContextCompat.getColor(requireContext(), R.color.sage))}
        else if(delay_type=="RLC"){
            text1.setBackgroundColor(
                ContextCompat.getColor(requireContext(), R.color.orange))}

        //chart appearance settings
        chart = v.findViewById(R.id.chart)

        //chart.setOnChartValueSelectedListener(this)
        chart.setDrawBarShadow(false)
        chart.setDrawValueAboveBar(true)
        chart.setFitBars(true)


        chart.description.isEnabled = false
        chart.setDrawGridBackground(true)

        val xAxis = chart.xAxis
        xAxis.position = XAxisPosition.BOTTOM
        xAxis.setDrawGridLines(false)
        xAxis.granularity = 1f //f is to cast to/declare it as float
        xAxis.labelCount = 60

        xAxis.setDrawGridLines(true)

        val leftAxis = chart.axisLeft
        leftAxis.granularity = 1f //f is to cast to/declare it as float
//        leftAxis.labelCount = 10
        leftAxis.axisMinimum = 0f // this replaces setStartAtZero(true)

        val rightAxis = chart.axisRight
//        rightAxis.granularity = 1f //f is to cast to/declare it as float
        rightAxis.labelCount = 1
        rightAxis.setDrawGridLines(false)
        rightAxis.axisMinimum = 0f // this replaces setStartAtZero(true)

        setData()

        return v
    }

    private fun parseStringToText (data_str:String?){
        if(data_str==null){return} //do nothing if null
        // format is x,values/y,values/avg

        val l1List = data_str.split('/')
        x_val = (l1List[0].split(',')).map {it.toFloat()}
        y_val = (l1List[1].split(',')).map {it.toFloat()}
        retx_avg = l1List[2]

    }

    fun setData() {
        val barValues:ArrayList<BarEntry> = ArrayList()

        for (i in x_val.indices){
            barValues.add(BarEntry(x_val[i],y_val[i]))
        }

        val set = BarDataSet(barValues,"example set")
        set.setDrawIcons(false)

        set.setColors(R.color.dustyPurple)

        val dataSets = ArrayList<IBarDataSet>()
        dataSets.add(set)
        val data = BarData(dataSets)
        data.setValueTextSize(10f)
        data.setBarWidth((bucketSize*0.8).toFloat())
        chart.setData(data)


//
//        BarDataSet set1;
//
//        if (chart.getData() != null &&
//                chart.getData().getDataSetCount() > 0) { //aka update
//            set1 = (BarDataSet) chart.getData().getDataSetByIndex(0);
//            set1.setValues(values);
//            chart.getData().notifyDataChanged();
//            chart.notifyDataSetChanged();
//
//        } else { //aka init
//            set1 = new BarDataSet(values, "The year 2017");
//            set1.setDrawIcons(false);
//            ArrayList<IBarDataSet> dataSets = new ArrayList<>();
//            dataSets.add(set1);
//
//            BarData data = new BarData(dataSets);
//            data.setValueTextSize(10f);
//            data.setBarWidth(0.9f);
//
//            chart.setData(data);
//        }
    }

    interface OnDataPass {
        fun onFragDestroyed()
    }

    lateinit var dataPasser: OnDataPass

    override fun onDestroyView(){
        dataPasser.onFragDestroyed()
        super.onDestroyView()
    }

}