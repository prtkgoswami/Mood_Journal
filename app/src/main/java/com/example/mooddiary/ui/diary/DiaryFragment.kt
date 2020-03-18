package com.example.mooddiary.ui.diary

import android.content.Intent
import android.graphics.Color
import android.graphics.Typeface
import android.os.Bundle
import android.os.Environment
import android.text.Layout
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.core.view.marginTop
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.example.mooddiary.*
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.reflect.TypeToken
import kotlinx.android.synthetic.main.fragment_diary.*
import java.io.BufferedReader
import java.io.File
import java.lang.Exception
import java.lang.reflect.Type
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class DiaryFragment : Fragment() {

    private lateinit var diaryViewModel: DiaryViewModel
    lateinit var root : View
    lateinit var selDate : String

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        diaryViewModel = ViewModelProviders.of(this).get(DiaryViewModel::class.java)
        root = inflater.inflate(R.layout.fragment_diary, container, false)

        var cv: CalendarView = root.findViewById(R.id.calendarView) as CalendarView

        // Getting Current Data
        var date : Date = Date(cv.date)
        val sdf2 = SimpleDateFormat("dMyyyy")
        var currDateOnly = sdf2.format(date)
        selDate = currDateOnly

        // Calendar View Date Change Function
        cv.setOnDateChangeListener { view, year, month, dayOfMonth ->
            val sdf = SimpleDateFormat("dMyyyy_hhmmss")
            val currDate = sdf.format(Date())
            selDate = dayOfMonth.toString() + (month+1).toString() + year.toString()
            Log.d(getString(R.string.debug_key), currDate + " || " + selDate + " || " + currDate.contains(selDate, ignoreCase = true).toString())
            refreshList(selDate)
        }

        return root
    }

    // Function to Re-Populate the Entries using the date selected
    private fun refreshList (selectedDate: String) {
        Log.d(getString(R.string.debug_key), "Refresh Called on ".plus(selectedDate))
        val dateContent : LinearLayout = root.findViewById(R.id.diaryScrollLinear) as LinearLayout

        try {
            dateContent.removeAllViews()
            Log.d(getString(R.string.debug_key), "All views removed")
        } catch (e:Exception) {}

        // Creating Json Reader object and reading the journal page file
        val gb: GsonBuilder = GsonBuilder()
        gb.setLenient()
        var gson = gb.create()
        var file: File = File(root.context.getExternalFilesDir(null), getString(R.string.journal_page_file_prefix) + selectedDate)

        if (!file.exists()) {
            val tv = TextView(this.context)
            tv.setText(getString(R.string.no_file_found))
            tv.gravity = Gravity.CENTER
            dateContent.addView(tv)
        }
        else {
            val br: BufferedReader = file.bufferedReader()
            val inputString = br.use { it.readText() }
            br.close()
            Log.d(getString(R.string.debug_key), inputString)

            var pageType = object : TypeToken<ArrayList<Page>>() {}.type
            var pageList = gson.fromJson<List<Page>>(inputString, pageType)

            if (pageList.size == 0) {
                val tv = TextView(this.context)
                tv.setText(getString(R.string.no_file_found))
                tv.gravity = Gravity.CENTER
                dateContent.addView(tv)
            }
            else {
                var iterator = pageList.iterator()

                // Creating Each Entry as a Button
                iterator.forEach {
                    // Fetching Data
                    var date = it.entry?.postDate.toString()
                    var time = it.entry?.postTime.toString()
                    var emotion = it.entry?.postEmotion.toString()
                    var data = it.entry?.postData.toString()

                    // Creating Frame
                    val entryFrame : LinearLayout = LinearLayout(this.context)
                    entryFrame.layoutParams = LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT)
                    entryFrame.orientation = LinearLayout.HORIZONTAL
                    entryFrame.setPadding(0,5, 0, 5)

                    // Creating Image View for emoticon
                    val emoImg : ImageView = ImageView(this.context)
                    emoImg.layoutParams = LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.WRAP_CONTENT, 120, 4F)
                    when (emotion) {
                        "happy" -> emoImg.setImageResource(R.drawable.emoji_happy)
                        "delight" -> emoImg.setImageResource(R.drawable.emoji_delight)
                        "excite" -> emoImg.setImageResource(R.drawable.emoji_excite)
                        "sad" -> emoImg.setImageResource(R.drawable.emoji_sad)
                        "awful" -> emoImg.setImageResource(R.drawable.emoji_awful)
                        "exhaust" -> emoImg.setImageResource(R.drawable.emoji_exhaust)
                        "anger" -> emoImg.setImageResource(R.drawable.emoji_anger)
                        "outrage" -> emoImg.setImageResource(R.drawable.emoji_outrage)
                        "scared" -> emoImg.setImageResource(R.drawable.emoji_scared)
                    }

                    // Creating Button for Entry
                    val btnEntry = Button(this.context)
                    btnEntry.layoutParams = LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT,1F)
                    btnEntry.textSize = 16f
                    btnEntry.text = time
                    btnEntry.gravity = Gravity.CENTER_VERTICAL or Gravity.START
                    btnEntry.setBackgroundColor(resources.getColor(R.color.colorAccent))
                    btnEntry.setTextColor(resources.getColor(R.color.white))
                    btnEntry.setPadding(10, 0, 0, 0)
                    btnEntry.setOnClickListener {
                        var mIntent: Intent = Intent(this.context, DiaryPageActivity::class.java)
                        mIntent.putExtra("jsonEmotion", emotion)
                        mIntent.putExtra("jsonDate", date)
                        mIntent.putExtra("jsonTime", time)
                        mIntent.putExtra("jsonData", data)
                        mIntent.putExtra("openFile", file)
                        startActivity(mIntent)
                    }

                    // Adding Image and Button to current entry Frame & adding to Entry List Frame
                    entryFrame.addView(emoImg)
                    entryFrame.addView(btnEntry)
                    dateContent.addView(entryFrame)
                }
            }
        }
    }

    // Saving the Selected Date
    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)

        outState?.run {
            putString("SelectedDate", selDate)
        }
        super.onSaveInstanceState(outState)

        Log.d(getString(R.string.debug_key), "Selected Date Saved : ".plus(selDate))
    }

    // Restoring Selected Date & populating the Date's Entries
    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        if (savedInstanceState != null) {
            selDate = savedInstanceState?.getString("SelectedDate") as String
            refreshList(selDate)
            Log.d(getString(R.string.debug_key), "State Restored")
        }
    }

    // Refreshing List on Resume
    override fun onResume() {
        super.onResume()
        Log.d(getString(R.string.debug_key), "onResume: ".plus(selDate))
        refreshList(selDate)
    }
}