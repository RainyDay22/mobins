import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.example.appmobins.MainActivity
import com.example.appmobins.R
import com.github.mikephil.charting.charts.BarChart
import com.github.mikephil.charting.components.XAxis.XAxisPosition
import com.github.mikephil.charting.components.YAxis.YAxisLabelPosition
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.interfaces.datasets.IBarDataSet


class GraphFrag : Fragment() {
    private lateinit var act: MainActivity
    private var graph_source ="some_file_name" //will hold all the msg_logs returned by LogAnalyzer
    private lateinit var chart: BarChart

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val v = inflater.inflate(R.layout.graph_frag, container, false)
        //set the strings to the xml holders
        val text1: TextView = v.findViewById(R.id.text1)
        val text2: TextView = v.findViewById(R.id.text2)
        chart = v.findViewById(R.id.chart)

        //chart.setOnChartValueSelectedListener(this)
        chart.setDrawBarShadow(false)
        chart.setDrawValueAboveBar(true)
        chart.description.isEnabled = false
        chart.setMaxVisibleValueCount(60)
        //chart.setPinchZoom(false)

        chart.setDrawGridBackground(true)

        val xAxis = chart.xAxis
        xAxis.position = XAxisPosition.BOTTOM
        xAxis.setDrawGridLines(false)
        xAxis.granularity = 1f //f is to cast to/declare it as float
        xAxis.labelCount = 60
        xAxis.axisMinimum = 0f

        val leftAxis = chart.axisLeft
//        leftAxis.granularity = 0.5f //f is to cast to/declare it as float
        leftAxis.labelCount = 10
//        leftAxis.spaceTop = 15f
        leftAxis.axisMinimum = 0f // this replaces setStartAtZero(true)

        val rightAxis = chart.axisRight
        rightAxis.granularity = 1f //f is to cast to/declare it as float
//        rightAxis.labelCount = 10
        rightAxis.axisMinimum = 0f // this replaces setStartAtZero(true)


        setData()
        text1.setText(graph_source)

        return v
    }

    fun setData() {
        val barValues:ArrayList<BarEntry> = ArrayList()
        val hardValues = listOf(1f,2f,3f,4f,5f)

        for (h in hardValues){
            barValues.add(BarEntry(h,h))
        }

        val set = BarDataSet(barValues,"dummy set")
        set.setDrawIcons(false)

        set.setColors(R.color.dustyPurple)

        val dataSets = ArrayList<IBarDataSet>()
        dataSets.add(set)
        val data = BarData(dataSets)
        data.setValueTextSize(10f)
        data.setBarWidth(0.4f)
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

    override fun onAttach(context: Context) {
        super.onAttach(context)
        dataPasser = context as OnDataPass

        act = activity as MainActivity
        act.supportActionBar?.setTitle("Analysis Graph")
        act.supportActionBar?.setSubtitle(graph_source)
    }
}