package com.example.mooddiary

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.EditText
import com.google.gson.GsonBuilder
import com.google.gson.reflect.TypeToken
import kotlinx.android.synthetic.main.activity_diary_page.*
import java.io.BufferedReader
import java.io.File

class DiaryPageActivity : AppCompatActivity() {
    lateinit var etDiary : EditText
    lateinit var openFile : File
    lateinit var menuEditItem : MenuItem
    lateinit var menuSaveItem: MenuItem
    lateinit var date : String
    lateinit var entryTime : String
    lateinit var entryEmotion : String

    override fun onCreate(savedInstanceState: Bundle?) {
        setTheme(R.style.AppTheme)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_diary_page)

        val extras: Bundle = intent.extras as Bundle
        date = extras.getString("jsonDate").toString()
        entryTime = extras.getString("jsonTime").toString()
        entryEmotion = extras.getString("jsonEmotion").toString()
        val data : String = extras.getString("jsonData").toString()
        openFile = extras.get("openFile") as File
        etDiary = findViewById(R.id.et_diary_entry)

        // Setting Emoticon based on Emotion, Date, Time & the Dairy Text
        when (entryEmotion) {
            "happy" -> iv_emoticon.setImageResource(R.drawable.emoji_happy)
            "delight" -> iv_emoticon.setImageResource(R.drawable.emoji_delight)
            "excite" -> iv_emoticon.setImageResource(R.drawable.emoji_excite)
            "sad" -> iv_emoticon.setImageResource(R.drawable.emoji_sad)
            "awful" -> iv_emoticon.setImageResource(R.drawable.emoji_awful)
            "exhaust" -> iv_emoticon.setImageResource(R.drawable.emoji_exhaust)
            "anger" -> iv_emoticon.setImageResource(R.drawable.emoji_anger)
            "outrage" -> iv_emoticon.setImageResource(R.drawable.emoji_outrage)
            "scared" -> iv_emoticon.setImageResource(R.drawable.emoji_scared)
        }
        tv_date.setText("Date : " + date)
        tv_time.setText("Time : " + entryTime)
        iv_emotion.setText("Emotion : " + entryEmotion.capitalize())
        etDiary.setText(data)
        etDiary.isEnabled = false
    }

    // Creating Edit & Save options in App Bar
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.new_entry_menu, menu)
        menuEditItem = menu!!.findItem(R.id.edit_item)
        menuSaveItem = menu!!.findItem(R.id.save_item)
        menuSaveItem.setVisible(false)
        return super.onCreateOptionsMenu(menu)
    }

    // Handling the Option Click
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val selItem = item.itemId

        when (selItem) {
            R.id.edit_item -> {
                etDiary.isEnabled = true
                menuSaveItem.setVisible(true)
                menuEditItem.setVisible(false)
                Log.d(getString(R.string.debug_key), "Edit Clicked")
            }
            R.id.save_item -> {
                etDiary.isEnabled = false
                menuSaveItem.setVisible(false)
                menuEditItem.setVisible(true)
                saveEntry()
                Log.d(getString(R.string.debug_key), "Save Clicked")
            }
        }
        return true
    }

    // Function to Save the Entry after edit
    fun saveEntry() {
        val gb: GsonBuilder = GsonBuilder()
        gb.setLenient()
        var gson = gb.create()
        val br: BufferedReader = openFile.bufferedReader()
        val inputString = br.use { it.readText() }

        // Appending into Entry Object
        var pageType = object : TypeToken<ArrayList<Page>>() {}.type
        var pageList = gson.fromJson<List<Page>>(inputString, pageType)
        var iterator = pageList.iterator()
        iterator.forEach {
            var time = it.entry?.postTime.toString()
            if (time == entryTime) {
                it.entry?.postData = etDiary.text.toString()
            }
        }

        // Writing to File
        val jsonString = gson.toJson(pageList)
        openFile.writeText(jsonString)
        Log.d(getString(R.string.debug_key), "Entry Altered")
    }
}
