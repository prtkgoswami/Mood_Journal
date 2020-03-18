package com.example.mooddiary

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Switch
import android.widget.Toast
import com.google.gson.Gson
import kotlinx.android.synthetic.main.activity_register.*
import java.io.File
import java.util.*

class RegisterActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        val sharedPref : SharedPreferences = this.getSharedPreferences(getString(R.string.user_pref_file_name), Context.MODE_PRIVATE)

        // Ragistration handler
        nextBtn.setOnClickListener {
            Log.d(getString(R.string.debug_key), "Next Button Clicked on Welcome Page")

            val name = inputName.text.toString()
            val pin = inputPin.text.toString()
            val cpin = inputCPin.text.toString()
            Log.d(getString(R.string.debug_key), pin + " || " + cpin)

            // Checking if fields are empty
            if(name.isNullOrEmpty()) {
                Toast.makeText(this, getString(R.string.notify_name_empty), Toast.LENGTH_SHORT).show()
            }
            else if(pin.isNullOrEmpty()) {
                Toast.makeText(this, getString(R.string.notify_pin_empty), Toast.LENGTH_SHORT).show()
            }
            else if(cpin.isNullOrEmpty()) {
                Toast.makeText(this, getString(R.string.notify_cpin_empty), Toast.LENGTH_SHORT).show()
            }
            else {
                // Checking if PIN & Confirmation PIN are equal
                if(!pin.equals(cpin)) {
                    inputPin.setText("")
                    inputCPin.setText("")
                    Toast.makeText(this, getString(R.string.notify_unequal_pin), Toast.LENGTH_SHORT).show()
                }
                // Checking if PIN is less than 4
                else if(pin.length < 4) {
                    inputPin.setText("")
                    inputCPin.setText("")
                    Toast.makeText(this, getString(R.string.notify_unequal_pin), Toast.LENGTH_SHORT).show()
                }
                // Registering User
                else {
                    Log.d(getString(R.string.debug_key), "Name is ".plus(name))
                    Log.d(getString(R.string.debug_key), "PIN is ".plus(pin))

                    // Writing to Shared Prefrences
                    with(sharedPref.edit()) {
                        putString(getString(R.string.user_pref_name_key), name)
                        putString(getString(R.string.user_pref_pin_key), pin)
                        commit()
                    }

                    // Getting Index of the Week in the current Month
                    val cal = Calendar.getInstance()
                    val date = Date()
                    cal.set(1900+date.getYear(), date.getMonth(), date.getDate())
                    val weekIndex = cal.get(Calendar.WEEK_OF_MONTH) as Int

                    // Creating Analytics File
                    Log.d(getString(R.string.debug_key), "New Analytics File Created")
                    var analyticsFileName = getString(R.string.analytics_file_name)
                    val analyticsFile = File(this.getExternalFilesDir(null), analyticsFileName)
                    var analytics: Analytics = Analytics(weekIndex)
                    var gson = Gson()
                    var jsonString = gson.toJson(analytics)
                    analyticsFile.writeText(jsonString)

                    Toast.makeText(this, "Welcome ".plus(name), Toast.LENGTH_SHORT).show()

                    var mIntent : Intent = Intent(this, MoodDiaryBaseActivity::class.java)
                    mIntent.putExtra("userName", name)
                    startActivity(mIntent)

                    // Finishing Activity & Removing Activity from Stack
                    finish()
                }
            }
        }
    }
}
