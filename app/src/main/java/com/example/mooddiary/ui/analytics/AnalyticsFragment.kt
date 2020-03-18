package com.example.mooddiary.ui.analytics

 import android.content.Context
 import android.content.SharedPreferences
 import android.graphics.Color
 import android.os.Bundle
 import android.util.Log
 import android.view.LayoutInflater
 import android.view.View
 import android.view.ViewGroup
 import android.widget.TextView
 import androidx.fragment.app.Fragment
 import androidx.lifecycle.ViewModelProviders
 import com.example.mooddiary.R
 import com.github.mikephil.charting.charts.BarChart
 import com.github.mikephil.charting.charts.HorizontalBarChart
 import com.github.mikephil.charting.charts.PieChart
 import com.github.mikephil.charting.components.Legend
 import com.github.mikephil.charting.components.LegendEntry
 import com.github.mikephil.charting.components.XAxis
 import com.github.mikephil.charting.data.*
 import com.github.mikephil.charting.formatter.IndexAxisValueFormatter
 import com.github.mikephil.charting.formatter.LargeValueFormatter
 import com.google.gson.Gson
 import com.google.gson.reflect.TypeToken
 import java.io.BufferedReader
 import java.io.File

class AnalyticsFragment : Fragment() {
    private lateinit var analyticsViewModel: AnalyticsViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        analyticsViewModel = ViewModelProviders.of(this).get(AnalyticsViewModel::class.java)
        val root = inflater.inflate(R.layout.fragment_analytics, container, false)

        // Setting Array of Colors
        var colorArr : IntArray = intArrayOf(R.color.colorAnger, R.color.colorAwful, R.color.colorDelight, R.color.colorExcite,
            R.color.colorExhaust, R.color.colorHappy, R.color.colorOutrage, R.color.colorSad, R.color.colorScared)

        // Setting Weekly Snapshot & Greeting
        val sharedPref : SharedPreferences =
            this.activity?.getSharedPreferences(getString(R.string.user_pref_file_name), Context.MODE_PRIVATE) as SharedPreferences
        var name = sharedPref.getString(getString(R.string.user_pref_name_key), "")
        val greeting : TextView = root.findViewById(R.id.analytic_greeting)
        val status : TextView = root.findViewById(R.id.analytic_status)
        greeting.text = getString(R.string.analytic_greeting_prefix).plus(" ").plus(name?.capitalize()).plus("!")

        // Fetching Analytics & Fetching Individual Objects
        var analyticsFileName = getString(R.string.analytics_file_name)
        var gson = Gson()
        var analyticsFile = File(root.context.getExternalFilesDir(null), analyticsFileName)
        var br : BufferedReader = analyticsFile.bufferedReader()
        var inputString = br.use { it.readText() }
        var mapType = object : TypeToken<Map<String, Any>> () {}.type
        var analyticMap : Map<String, Any> = gson.fromJson(inputString, mapType)
        var analytic_overall : Map<String, Int> = analyticMap.getValue("overall_analytics") as Map<String, Int>
        var total_overall = analytic_overall.values.sum().toFloat()
        var analytic_weekly : ArrayList<Map<String, Int>> = analyticMap.getValue("weekly_analytics") as ArrayList<Map<String, Int>>
        var weekScore : Int = 0
        var weekEntryCount : Int = 0
        Log.d(getString(R.string.debug_key), analytic_weekly.toString())

        var pieChart : PieChart = root.findViewById(R.id.piechart)
        var barChart : HorizontalBarChart = root.findViewById(R.id.barchart)
        var noOVState : TextView = root.findViewById(R.id.tv_no_ovstats)
        var pieChartTitle : TextView = root.findViewById(R.id.pieChartTitle)
        var barChartTitle : TextView = root.findViewById(R.id.barChartTitle)

        if (total_overall == 0f) {
            // No Analytics Available
            pieChart.visibility = View.INVISIBLE
            barChart.visibility = View.INVISIBLE
            pieChartTitle.visibility = View.INVISIBLE
            barChartTitle.visibility = View.INVISIBLE
            greeting.visibility = View.INVISIBLE
            status.visibility = View.INVISIBLE
        }
        else {
            noOVState.visibility = View.GONE
            pieChart.visibility = View.VISIBLE
            barChart.visibility = View.VISIBLE
            pieChartTitle.visibility = View.VISIBLE
            barChartTitle.visibility = View.VISIBLE
            greeting.visibility = View.VISIBLE
            status.visibility = View.VISIBLE

            // Overall Analytics - Pie Chart
            var pieEntries : ArrayList<PieEntry> = ArrayList()
            var pieIterator = analytic_overall.iterator()
            var selectedColorIndex : ArrayList<Int> = ArrayList()
            var selectedColors : ArrayList<Int> = ArrayList()
            var temp = 0f
            pieIterator.forEach {
                var percent = (it.value/total_overall) * 100
                if (percent != 0f) {
                    var pe = PieEntry(percent, "")
                    selectedColorIndex.add(temp.toInt())
                    pieEntries.add(pe)
                }
                temp += 1f
            }
            selectedColorIndex.iterator().forEach { selectedColors.add(colorArr[it]) }
            var set: PieDataSet = PieDataSet(pieEntries, getString(R.string.overall_analytics_label))
            set.setColors(selectedColors.toIntArray(), this.context)
            set.valueTextColor = Color.BLACK
            set.sliceSpace = 5f
            var data : PieData = PieData(set)
            data.setValueTextSize(0F)
            pieChart.data = data
            pieChart.setEntryLabelColor(Color.BLACK)
            pieChart.setEntryLabelTextSize(12f)
            pieChart.description.isEnabled = false
            pieChart.setUsePercentValues(true)
            pieChart.invalidate()

            // Creating Legends for Emotions
            var legend = pieChart.legend
            legend.verticalAlignment = Legend.LegendVerticalAlignment.CENTER
            legend.horizontalAlignment = Legend.LegendHorizontalAlignment.RIGHT
            legend.orientation = Legend.LegendOrientation.VERTICAL
            legend.setDrawInside(false)
            var legendEntries = arrayListOf<LegendEntry>()
            legendEntries.add(LegendEntry("Anger", Legend.LegendForm.SQUARE, 8f, 8f,
                null, resources.getColor(R.color.colorAnger)))
            legendEntries.add(LegendEntry("Awful", Legend.LegendForm.SQUARE, 8f, 8f,
                null, resources.getColor(R.color.colorAwful)))
            legendEntries.add(LegendEntry("Happy", Legend.LegendForm.SQUARE, 8f, 8f,
                null, resources.getColor(R.color.colorHappy)))
            legendEntries.add(LegendEntry("Delight", Legend.LegendForm.SQUARE, 8f, 8f,
                null, resources.getColor(R.color.colorDelight)))
            legendEntries.add(LegendEntry("Excite", Legend.LegendForm.SQUARE, 8f, 8f,
                null, resources.getColor(R.color.colorExcite)))
            legendEntries.add(LegendEntry("Outrage", Legend.LegendForm.SQUARE, 8f, 8f,
                null, resources.getColor(R.color.colorOutrage)))
            legendEntries.add(LegendEntry("Sad", Legend.LegendForm.SQUARE, 8f, 8f,
                null, resources.getColor(R.color.colorSad)))
            legendEntries.add(LegendEntry("Scared", Legend.LegendForm.SQUARE, 8f, 8f,
                null, resources.getColor(R.color.colorScared)))
            legendEntries.add(LegendEntry("Exhausted", Legend.LegendForm.SQUARE, 8f, 8f,
                null, resources.getColor(R.color.colorExhaust)))
            legend.setCustom(legendEntries)
            legend.yOffset = 3f
            legend.xOffset = 3f
            legend.yEntrySpace = 3f
            legend.xEntrySpace = 5f
            legend.textSize = 10f

            // Weekly Analytics - Bar Chart
            var barChart = root.findViewById<BarChart>(R.id.barchart)
            barChart.setDrawValueAboveBar(true)
            barChart.description.isEnabled = false
            barChart.setDrawGridBackground(false)
            barChart.xAxis.setDrawGridLines(false)
            barChart.axisLeft.isEnabled = false
            barChart.setPinchZoom(false)

            var xAxisValues = ArrayList<String>()
            xAxisValues.add("Mon")
            xAxisValues.add("Tue")
            xAxisValues.add("Wed")
            xAxisValues.add("Thu")
            xAxisValues.add("Fri")
            xAxisValues.add("Sat")
            xAxisValues.add("Sun")

            val emoRatings = HashMap<String, Int>()
            emoRatings["outrage"] = -4
            emoRatings["awful"] = -3
            emoRatings["anger"] = -2
            emoRatings["sad"] = -1
            emoRatings["scared"] = 0
            emoRatings["exhausted"] = 0
            emoRatings["happy"] = 1
            emoRatings["delight"] = 3
            emoRatings["excite"] = 4

            var yValueGroup = ArrayList<BarEntry>()
            var day = 1
            for (item in analytic_weekly) {
                var yArray : ArrayList<Float> = ArrayList()
                var iter = item.iterator()
                iter.forEach {
                    yArray.add(it.value.toFloat())
                    if(it.value > 0) {
                        weekScore += emoRatings[it.key.toString()] as Int * it.value
                        weekEntryCount += 1 * it.value
                    }
                }
                yValueGroup.add(BarEntry(day.toFloat(), yArray.toFloatArray()))
                day++
            }
            // Log.d(getString(R.string.debug_key), yValueGroup.toString())

            var barDataSet = BarDataSet(yValueGroup, "")
            barDataSet.setColors(resources.getColor(R.color.colorAnger),
                                resources.getColor(R.color.colorAwful),
                                resources.getColor(R.color.colorDelight),
                                resources.getColor(R.color.colorExcite),
                                resources.getColor(R.color.colorExhaust),
                                resources.getColor(R.color.colorHappy),
                                resources.getColor(R.color.colorOutrage),
                                resources.getColor(R.color.colorSad),
                                resources.getColor(R.color.colorScared))
            barDataSet.setDrawIcons(false)
            barDataSet.setDrawValues(false)
            var barData = BarData(barDataSet)
            barChart.description.isEnabled = false
            barChart.description.textSize = 0f
            barData.setValueFormatter(LargeValueFormatter())
            barChart.data = barData
            barChart.xAxis.axisMinimum = 0f
            barChart.xAxis.axisMaximum = 7f
            barChart.data.isHighlightEnabled = false
            barChart.legend.isEnabled = false
            barChart.setViewPortOffsets(0f,0f,0f,0f)
            barChart.invalidate()

            val xAxis = barChart.xAxis
            xAxis.setCenterAxisLabels(true)
            xAxis.setDrawAxisLine(false)
            xAxis.textSize = 9f
            xAxis.position = XAxis.XAxisPosition.BOTTOM
            xAxis.valueFormatter = IndexAxisValueFormatter(xAxisValues)
            xAxis.labelCount = 7
            xAxis.setAvoidFirstLastClipping(true)

            val rightAxis = barChart.axisRight
            rightAxis.isEnabled = false

            barChart.axisLeft.isEnabled = false
            barChart.setTouchEnabled(false)
            barChart.isDragEnabled = false
            barChart.setScaleEnabled(false)
            barChart.isScaleXEnabled = false
            barChart.isScaleYEnabled = false
            barChart.data = barData

            // Calculating Emotion Score & Displaying as Weekly Snapshot
            var avgEmotion = weekScore/weekEntryCount
            var overallEmotion = "Neutral"
            if (avgEmotion < 0) {
                overallEmotion = "Negative"
            }
            else if (avgEmotion > 0) {
                overallEmotion = "Positive"
            }
            Log.d(getString(R.string.debug_key), weekScore.toString() + "|" + weekEntryCount.toString())
            Log.d(getString(R.string.debug_key), "Avg Score : ".plus(avgEmotion.toString()))
            Log.d(getString(R.string.debug_key), "Overall Emotion : ".plus(overallEmotion))
            when (overallEmotion) {
                "Positive" -> status.text = getString(R.string.positive_statement)
                "Negative" -> status.text = getString(R.string.negative_statement)
                "Neutral" -> status.text = getString(R.string.neutral_statement)
            }
        }

        return root
    }
}