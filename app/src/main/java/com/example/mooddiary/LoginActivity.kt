package com.example.mooddiary

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.hardware.biometrics.BiometricPrompt
import android.hardware.fingerprint.FingerprintManager
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.Toast
import androidx.core.content.ContextCompat
import com.andrognito.pinlockview.IndicatorDots
import com.andrognito.pinlockview.PinLockListener
import com.andrognito.pinlockview.PinLockView
import com.google.gson.Gson
import kotlinx.android.synthetic.main.activity_login.*
import java.io.BufferedReader
import java.io.File
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.Executor

class LoginActivity : AppCompatActivity() {
    val TAG = "PinLockView"
    lateinit var sharedPref : SharedPreferences
    private lateinit var executor : Executor
    private lateinit var biometricPrompt: androidx.biometric.BiometricPrompt

    // PIN Lock Handler
    private val mPinLockListener: PinLockListener = object : PinLockListener {
        override fun onComplete(pin: String) {
            Log.d(TAG , "Pin complete: $pin")

            var savedPin = sharedPref.getString(getString(R.string.user_pref_pin_key), "")
            val userName = sharedPref.getString("user_name", "") as String

            // If Username is Empty/NULL, Register User
            if(!userName.isNullOrEmpty()) {
                startActivity(Intent(this@LoginActivity, IntroActivity::class.java))
                finish()
            }

            Log.d(getString(R.string.debug_key), "$pin | $savedPin")

            if(pin == savedPin) {
                login(userName)
            }
            else {
                pin_lock_view.resetPinLockView()
                Toast.makeText(applicationContext, getString(R.string.notify_auth_fail), Toast.LENGTH_SHORT).show()
            }
        }

        // Handle Reset PIN - Implementation Required By the Library
        override fun onEmpty() {
            Log.d(TAG, "Pin empty")
        }

        // Handle Intermediate PIN Change - Implementation Required By the Library
        override fun onPinChange(pinLength: Int, intermediatePin: String) {
            Log.d(TAG, "Pin changed, new length $pinLength with intermediate pin $intermediatePin")
        }
    }

    // Function to Login User
    private fun login (userName : String) {
        var analyticsFileName = getString(R.string.analytics_file_name)
        val analyticsFile = File(this@LoginActivity.getExternalFilesDir(null), analyticsFileName)
        var gson = Gson()

        // Fetching Week Number in the Current Month
        val cal = Calendar.getInstance()
        val date = Date()
        cal.set(1900+date.getYear(), date.getMonth(), date.getDate())
        val weekIndex = cal.get(Calendar.WEEK_OF_MONTH) as Int

        // Creating Analytics if doesn't exist
        if (!analyticsFile.exists()) {
            Log.d(getString(R.string.debug_key), "New Analytics File Created")
            var analytics: Analytics = Analytics(weekIndex)
            var jsonString = gson.toJson(analytics)
            analyticsFile.writeText(jsonString)
        }
        else {
            // Fetching Analytics File
            val br: BufferedReader = analyticsFile.bufferedReader()
            val inputString = br.use { it.readText() }
            var analytics = gson.fromJson<Analytics>(inputString, Analytics::class.java)

            // If Week Index is unequal to the index in the Analytics File, Reset the Weekly Analytics
            if (weekIndex != analytics.weekIndex) {
                Log.d(getString(R.string.debug_key), "Resetting Weekly Logs")
                analytics.resetWeek(weekIndex)
            }
        }

        var name = sharedPref.getString(getString(R.string.user_pref_name_key), "")
        Toast.makeText(applicationContext, "Welcome Back ".plus(name), Toast.LENGTH_LONG).show()

        var mIntent : Intent = Intent(this@LoginActivity, MoodDiaryBaseActivity::class.java)
        mIntent.putExtra("userName", userName)
        startActivity(mIntent)

        // Finishing Activity & Removing Activity from Stack
        finish()
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        setTheme(R.style.AppTheme)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        sharedPref = this.getSharedPreferences(getString(R.string.user_pref_file_name), Context.MODE_PRIVATE)
        val userName = sharedPref.getString("user_name", "") as String

        if(userName.isNullOrEmpty()) {
            startActivity(Intent(this@LoginActivity, IntroActivity::class.java))
            finish()
        }

        var mPinLockView = findViewById<PinLockView>(R.id.pin_lock_view)
        mPinLockView.setPinLockListener(mPinLockListener)
        mPinLockView.pinLength = 4
        mPinLockView.textColor = ContextCompat.getColor(this, R.color.white)

        // Setting Indicator Dots
        var mIndicatorDots = findViewById<IndicatorDots>(R.id.indicator_dots)
        mIndicatorDots.indicatorType = IndicatorDots.IndicatorType.FILL
        mPinLockView.attachIndicatorDots(mIndicatorDots)

        // Enabling Fingerprint Authentication
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            var fpMan = this.getSystemService(Context.FINGERPRINT_SERVICE) as FingerprintManager
            // Checking if Fingerprint Hardware exists & Fingerprint is enrolled in the security settings
            if (fpMan.isHardwareDetected() && fpMan.hasEnrolledFingerprints()) {
                executor = ContextCompat.getMainExecutor(this)
                biometricPrompt = androidx.biometric.BiometricPrompt(this, executor,
                    object : androidx.biometric.BiometricPrompt.AuthenticationCallback() {
                        // Authentication Error Handler Function
                        override fun onAuthenticationError(errorCode: Int, errString: CharSequence) {
                            super.onAuthenticationError(errorCode, errString)
                            Toast.makeText(applicationContext, "Authentication Error : $errString", Toast.LENGTH_SHORT).show()
                        }

                        // Authentication Success Handler Function
                        override fun onAuthenticationSucceeded(result: androidx.biometric.BiometricPrompt.AuthenticationResult) {
                            super.onAuthenticationSucceeded(result)
                            login(userName)
                        }

                        // Authentication Failure Handler Function
                        override fun onAuthenticationFailed() {
                            super.onAuthenticationFailed()
                            Toast.makeText(applicationContext, getString(R.string.notify_auth_fail), Toast.LENGTH_SHORT).show()
                        }
                    })

                // Setting the Fingerprint Prompt
                val promptInfo : androidx.biometric.BiometricPrompt.PromptInfo = androidx.biometric.BiometricPrompt.PromptInfo.Builder()
                    .setTitle(getString(R.string.biometric_title))
                    .setSubtitle(getString(R.string.biometric_subtitle))
                    .setNegativeButtonText(getString(R.string.biometric_negative_btn_text))
                    .build()

                // Fingerprint Button handler on Login Page
                var btnFp : Button = this.findViewById(R.id.btnFingerprint) as Button
                biometricPrompt.authenticate(promptInfo)
                btnFp.setOnClickListener {
                    biometricPrompt.authenticate(promptInfo)
                }
            }
            else {
                // Hiding Fingerprint Button if Hardware is non-existing/ fingerprint is not enrolled/ version is insufficient
                var btnFp : Button = this.findViewById(R.id.btnFingerprint) as Button
                btnFp.visibility = View.INVISIBLE
            }
        }
    }
}
