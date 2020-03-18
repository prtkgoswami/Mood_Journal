package com.example.mooddiary.ui.new_entry

import android.app.DatePickerDialog
import android.content.Context
import android.content.DialogInterface
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.*
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import com.example.mooddiary.Analytics
import com.example.mooddiary.DiaryEntry
import com.example.mooddiary.Page
import com.example.mooddiary.R
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.gson.Gson
import kotlinx.android.synthetic.main.fragment_new_entry.*
import java.io.BufferedReader
import java.io.File
import java.lang.Exception
import java.text.SimpleDateFormat
import java.util.*


class NewEntryFragment : Fragment() {

    private lateinit var newEntryViewModel: NewEntryViewModel
    private lateinit var emotion : String

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        newEntryViewModel = ViewModelProviders.of(this).get(NewEntryViewModel::class.java)
        val root = inflater.inflate(R.layout.fragment_new_entry, container, false)
        val bhappy: ImageButton = root.findViewById(R.id.btnHappy) as ImageButton
        val bdelight: ImageButton = root.findViewById<Button>(R.id.btnDelight) as ImageButton
        val bexcite: ImageButton = root.findViewById<Button>(R.id.btnExcite) as ImageButton
        val bsad: ImageButton = root.findViewById<Button>(R.id.btnSad) as ImageButton
        val bawful: ImageButton = root.findViewById<Button>(R.id.btnAwful) as ImageButton
        val bexhaust: ImageButton = root.findViewById<Button>(R.id.btnExhast) as ImageButton
        val banger: ImageButton = root.findViewById<Button>(R.id.btnAngry) as ImageButton
        val boutrage: ImageButton = root.findViewById<Button>(R.id.btnOutrage) as ImageButton
        val bscared: ImageButton = root.findViewById<Button>(R.id.btnScared) as ImageButton
        val savebtn: FloatingActionButton = root.findViewById(R.id.saveBtn) as FloatingActionButton
        val dateInput: EditText = root.findViewById(R.id.etDate) as EditText
        lateinit var picker: DatePickerDialog
        emotion = ""

        // Getting all formats of the Current Date
        val sdfDateText = SimpleDateFormat("d/M/yyyy")
        val sdfDate = SimpleDateFormat("dMyyyy")
        val sdfDay = SimpleDateFormat("u")
        dateInput.setText(sdfDateText.format(Date()))
        var currDateOnly = sdfDate.format(Date())
        var dayIndex = sdfDay.format(Date()).toInt() - 1
        Log.d(getString(R.string.debug_key), dayIndex.toString() + "||" + currDateOnly)

        // Individual Emotion Button Handlers
        bhappy.setOnClickListener {
            resetView(emotion, false)
            emotion = getString(R.string.emotion_happy)
            Log.d(getString(R.string.debug_key), "$emotion Clicked")
            bhappy.setBackgroundColor(resources.getColor(R.color.btnColorHighlighr))
            inpDiary.requestFocus()
        }
        bdelight.setOnClickListener {
            resetView(emotion, false)
            emotion = getString(R.string.emotion_delight)
            Log.d(getString(R.string.debug_key), "$emotion Clicked")
            bdelight.setBackgroundColor(resources.getColor(R.color.btnColorHighlighr))
            inpDiary.requestFocus()
        }
        bexcite.setOnClickListener {
            resetView(emotion, false)
            emotion = getString(R.string.emotion_excite)
            Log.d(getString(R.string.debug_key), "$emotion Clicked")
            bexcite.setBackgroundColor(resources.getColor(R.color.btnColorHighlighr))
            inpDiary.requestFocus()
        }
        bsad.setOnClickListener {
            resetView(emotion, false)
            emotion = getString(R.string.emotion_sad)
            Log.d(getString(R.string.debug_key), "$emotion Clicked")
            bsad.setBackgroundColor(resources.getColor(R.color.btnColorHighlighr))
            inpDiary.requestFocus()
        }
        bawful.setOnClickListener {
            resetView(emotion, false)
            emotion = getString(R.string.emotion_awful)
            Log.d(getString(R.string.debug_key), "$emotion Clicked")
            bawful.setBackgroundColor(resources.getColor(R.color.btnColorHighlighr))
            inpDiary.requestFocus()
        }
        bexhaust.setOnClickListener {
            resetView(emotion, false)
            emotion = getString(R.string.emotion_exhaust)
            Log.d(getString(R.string.debug_key), "$emotion Clicked")
            bexhaust.setBackgroundColor(resources.getColor(R.color.btnColorHighlighr))
            inpDiary.requestFocus()
        }
        banger.setOnClickListener {
            resetView(emotion, false)
            emotion = getString(R.string.emotion_anger)
            Log.d(getString(R.string.debug_key), "$emotion Clicked")
            banger.setBackgroundColor(resources.getColor(R.color.btnColorHighlighr))
            inpDiary.requestFocus()
        }
        boutrage.setOnClickListener {
            resetView(emotion, false)
            emotion = getString(R.string.emotion_outrage)
            Log.d(getString(R.string.debug_key), "$emotion Clicked")
            boutrage.setBackgroundColor(resources.getColor(R.color.btnColorHighlighr))
            inpDiary.requestFocus()
        }
        bscared.setOnClickListener {
            resetView(emotion, false)
            emotion = getString(R.string.emotion_scared)
            Log.d(getString(R.string.debug_key), "$emotion Clicked")
            bscared.setBackgroundColor(resources.getColor(R.color.btnColorHighlighr))
            inpDiary.requestFocus()
        }

        // Save Entry Button Handler
        savebtn.setOnClickListener {
            Log.d(getString(R.string.debug_key), "Diary Save Button Clicked")
            Log.d(getString(R.string.debug_key), dayIndex.toString() + " " + currDateOnly.toString() + " " + emotion)

            if (emotion.isNullOrEmpty()) {
                Toast.makeText(this.context, "Please Choose an Emotion.", Toast.LENGTH_SHORT).show()
            }
            else {
                var diaryText = inpDiary.text.toString()
                emotion = emotion.toLowerCase()

                // File Names
                var pageFileName = getString(R.string.journal_page_file_prefix) + currDateOnly
                var analyticsFileName = getString(R.string.analytics_file_name)

                // Creating JSON Data
                var jsonData = DiaryEntry(emotion, diaryText)
                var diaryPage = Page(jsonData)
                var gson = Gson()
                var contxt = root.context

                try {
                    // Retrieving File Object
                    val file = File(contxt.getExternalFilesDir(null), pageFileName)
                    if (file.exists()) {
                        Log.d(getString(R.string.debug_key), "Adding to Existing File - " + pageFileName)

                        // Reading from File
                        val br: BufferedReader = file.bufferedReader()
                        val inputString = br.use { it.readText() }
                        Log.d(getString(R.string.debug_key), inputString)
                        val tempList = gson.fromJson(inputString, Array<Page>::class.java).asList()
                        var pageList = ArrayList(tempList)

                        // Writing to File
                        pageList.add(diaryPage)
                        val entryString = gson.toJson(pageList)
                        Log.d(getString(R.string.debug_key), entryString)
                        file.writeText(entryString)
                        Log.d(getString(R.string.debug_key), "File Open in Append Mode")
                    }
                    else {
                        // Writing to New File
                        Log.d(getString(R.string.debug_key), "New File Created - " + pageFileName)
                        var pageList: ArrayList<Page> = ArrayList<Page>()
                        pageList.add(diaryPage)
                        var jsonString = gson.toJson(pageList)
                        file.writeText(jsonString)
                        Log.d(getString(R.string.debug_key), "File Open in Write Mode")
                    }

                    // Updating Analytics File
                    var analyticsFile = File(contxt.getExternalFilesDir(null), analyticsFileName)
                    var br : BufferedReader = analyticsFile.bufferedReader()
                    var inputString = br.use { it.readText() }
                    var analytic = gson.fromJson<Analytics>(inputString, Analytics::class.java)

                    // Updating Counts for each emotion
                    // 1. Fetch Previous emotion counts
                    // 2. Increment the counts
                    when (emotion) {
                        "happy" -> {
                            var weekEmoCount : Int = analytic.weekly_analytics?.get(dayIndex)?.happy as Int
                            var overEmoCount : Int = analytic.overall_analytics?.happy as Int
                            analytic.weekly_analytics!!.get(dayIndex).happy = weekEmoCount + 1
                            analytic.overall_analytics?.happy = overEmoCount+1
                        }
                        "delight" -> {
                            var weekEmoCount : Int = analytic.weekly_analytics?.get(dayIndex)?.delight as Int
                            var overEmoCount : Int = analytic.overall_analytics?.delight as Int
                            analytic.weekly_analytics!!.get(dayIndex).delight = weekEmoCount+1
                            analytic.overall_analytics?.delight = overEmoCount+1
                        }
                        "excite" -> {
                            var weekEmoCount : Int = analytic.weekly_analytics?.get(dayIndex)?.excite as Int
                            var overEmoCount : Int = analytic.overall_analytics?.excite as Int
                            analytic.weekly_analytics!!.get(dayIndex).excite = weekEmoCount+1
                            analytic.overall_analytics?.excite = overEmoCount+1
                        }
                        "sad" -> {
                            var weekEmoCount : Int = analytic.weekly_analytics?.get(dayIndex)?.sad as Int
                            var overEmoCount : Int = analytic.overall_analytics?.sad as Int
                            analytic.weekly_analytics?.get(dayIndex)?.sad = weekEmoCount+1
                            analytic.overall_analytics?.sad = overEmoCount+1
                        }
                        "awful" -> {
                            var weekEmoCount : Int = analytic.weekly_analytics?.get(dayIndex)?.awful as Int
                            var overEmoCount : Int = analytic.overall_analytics?.awful as Int
                            analytic.weekly_analytics?.get(dayIndex)?.awful = weekEmoCount+1
                            analytic.overall_analytics?.awful = overEmoCount+1
                        }
                        "exhaust" -> {
                            var weekEmoCount : Int = analytic.weekly_analytics?.get(dayIndex)?.exhausted as Int
                            var overEmoCount : Int = analytic.overall_analytics?.exhausted as Int
                            analytic.weekly_analytics?.get(dayIndex)?.exhausted = weekEmoCount+1
                            analytic.overall_analytics?.exhausted = overEmoCount+1
                        }
                        "anger" -> {
                            var weekEmoCount : Int = analytic.weekly_analytics?.get(dayIndex)?.anger as Int
                            var overEmoCount : Int = analytic.overall_analytics?.anger as Int
                            analytic.weekly_analytics?.get(dayIndex)?.anger = weekEmoCount+1
                            analytic.overall_analytics?.anger = overEmoCount+1
                        }
                        "outrage" -> {
                            var weekEmoCount : Int = analytic.weekly_analytics?.get(dayIndex)?.outrage as Int
                            var overEmoCount : Int = analytic.overall_analytics?.outrage as Int
                            analytic.weekly_analytics?.get(dayIndex)?.outrage = weekEmoCount+1
                            analytic.overall_analytics?.outrage = overEmoCount+1
                        }
                        "scared" -> {
                            var weekEmoCount : Int = analytic.weekly_analytics?.get(dayIndex)?.scared as Int
                            var overEmoCount : Int = analytic.overall_analytics?.scared as Int
                            analytic.weekly_analytics?.get(dayIndex)?.scared = weekEmoCount+1
                            analytic.overall_analytics?.scared = overEmoCount+1
                        }
                    }

                    // Writing the Updated Analytics back to File
                    var jsonString = gson.toJson(analytic)
                    Log.d(getString(R.string.debug_key), jsonString)
                    analyticsFile.writeText(jsonString)
                    Log.d(getString(R.string.debug_key), "Analytics Updated")

                    // Resetting View
                    resetView(emotion, true)
                    emotion = ""

                    // Updating User
                    Toast.makeText(contxt, getString(R.string.notify_diary_entry_success), Toast.LENGTH_SHORT).show()
                    Log.d(getString(R.string.debug_key), getString(R.string.notify_diary_entry_success))
                }
                catch (e : Exception) {
                    Toast.makeText(contxt, getString(R.string.notify_diary_entry_failure), Toast.LENGTH_SHORT).show()
                    Log.d(getString(R.string.debug_key), getString(R.string.notify_diary_entry_failure))
                }
            }
        }

        // Date Picker Functionality
        dateInput.setOnClickListener {
            it.setOnClickListener {
                val cldr = Calendar.getInstance()
                val day = cldr[Calendar.DAY_OF_MONTH]
                val month = cldr[Calendar.MONTH]
                val year = cldr[Calendar.YEAR]

                picker = DatePickerDialog(
                    this@NewEntryFragment.context as Context,
                    DatePickerDialog.OnDateSetListener { view, year, month, dayOfMonth ->
                        var cal : Calendar = Calendar.getInstance()
                        var calSet = cal.clone() as Calendar

                        calSet.set(Calendar.DATE, dayOfMonth)
                        calSet.set(Calendar.MONTH, month)
                        calSet.set(Calendar.YEAR, year)
                        var time_val = calSet.timeInMillis

                        dayIndex = sdfDay.format(time_val).toInt() - 1
                        dateInput.setText(dayOfMonth.toString() + "/" + (month + 1) + "/" + year)
                        currDateOnly = dayOfMonth.toString() + (month + 1) + year
                    },
                    year, month, day
                )
                picker.datePicker.maxDate = System.currentTimeMillis()
                picker.show()
            }
            Log.d(getString(R.string.debug_key), dayIndex.toString() + "||" + currDateOnly)
        }

        // Exit Confirmation on Back Press
        val callback = object : OnBackPressedCallback(true /** true means that the callback is enabled */) {
            override fun handleOnBackPressed() {
                Log.d(getString(R.string.debug_key), "Back Pressed")

                // Creating & Showing Alert Dialog
                val builder = AlertDialog.Builder(root.context)
                builder.setMessage("Do you want to close?")
                    .setCancelable(false)
                    .setPositiveButton("OK"){ dialog, id ->
                        activity?.finish()
                    }
                    .setNegativeButton("Cancel",
                        DialogInterface.OnClickListener { dialog, id ->
                            return@OnClickListener
                        }
                    )
                val alert = builder.create()
                alert.show()
            }
        }
        requireActivity().onBackPressedDispatcher.addCallback(this, callback)

        return root
    }

    // Function to Reset the View After Submission or Emotion Change
    fun resetView(emo : String, resetDiary : Boolean) {
        if (resetDiary)
            inpDiary.setText("")

        var emotion = emo.toLowerCase()
        when (emotion) {
            "happy" -> btnHappy.setBackgroundColor(resources.getColor(android.R.color.transparent))
            "delight" -> btnDelight.setBackgroundColor(resources.getColor(android.R.color.transparent))
            "excite" -> btnExcite.setBackgroundColor(resources.getColor(android.R.color.transparent))
            "sad" -> btnSad.setBackgroundColor(resources.getColor(android.R.color.transparent))
            "awful" -> btnAwful.setBackgroundColor(resources.getColor(android.R.color.transparent))
            "exhaust" -> btnExhast.setBackgroundColor(resources.getColor(android.R.color.transparent))
            "anger" -> btnAngry.setBackgroundColor(resources.getColor(android.R.color.transparent))
            "outrage" -> btnOutrage.setBackgroundColor(resources.getColor(android.R.color.transparent))
            "scared" -> btnScared.setBackgroundColor(resources.getColor(android.R.color.transparent))
        }
    }

    // Saving State - Emotion, Date & Diary Text
    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        if (inpDiary != null && etDate != null) {
            var diaryText = inpDiary.text.toString()
            var date = etDate.text.toString()

            outState?.run {
                putString("Emotion", emotion)
                putString("Date", date)
                putString("Text", diaryText)
            }
            super.onSaveInstanceState(outState)

            Log.d(getString(R.string.debug_key), "State Saved")
        }
    }

    // Restoring State - Emotion, Date & Diary Text
    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        if (savedInstanceState != null) {
            etDate.setText(savedInstanceState?.getString("Date"))
            inpDiary.setText(savedInstanceState?.getString("Text"))
            emotion = savedInstanceState?.getString("Emotion") as String

            when (emotion) {
                "happy" -> btnHappy.setBackgroundColor(resources.getColor(R.color.btnColorHighlighr))
                "delight" -> btnDelight.setBackgroundColor(resources.getColor(R.color.btnColorHighlighr))
                "excite" -> btnExcite.setBackgroundColor(resources.getColor(R.color.btnColorHighlighr))
                "sad" -> btnSad.setBackgroundColor(resources.getColor(R.color.btnColorHighlighr))
                "awful" -> btnAwful.setBackgroundColor(resources.getColor(R.color.btnColorHighlighr))
                "exhaust" -> btnExhast.setBackgroundColor(resources.getColor(R.color.btnColorHighlighr))
                "anger" -> btnAngry.setBackgroundColor(resources.getColor(R.color.btnColorHighlighr))
                "outrage" -> btnOutrage.setBackgroundColor(resources.getColor(R.color.btnColorHighlighr))
                "scared" -> btnScared.setBackgroundColor(resources.getColor(R.color.btnColorHighlighr))
            }
            inpDiary.requestFocus()

            Log.d(getString(R.string.debug_key), "State Restored")
        }
    }
}