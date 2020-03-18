package com.example.mooddiary.ui.settings

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import androidx.preference.SwitchPreference
import com.example.mooddiary.*
import kotlinx.android.synthetic.main.dialog_change_name.view.*
import kotlinx.android.synthetic.main.dialog_change_pin.view.*

class SettingsFragment : PreferenceFragmentCompat() {
    lateinit var sharedPref : SharedPreferences

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        addPreferencesFromResource(R.xml.preferences)
        sharedPref = context?.getSharedPreferences(getString(R.string.user_pref_file_name), Context.MODE_PRIVATE) as SharedPreferences

        val changeName = findPreference<Preference>("change_name")
        val changePin = findPreference<Preference>("change_pin")

        changeName?.setOnPreferenceClickListener {
            Log.d(getString(R.string.debug_key), "Changing User Name")
            openNameDialog()
            true
        }

        changePin?.setOnPreferenceClickListener {
            Log.d(getString(R.string.debug_key), "Changing Security PIN")
            openPINDialog()
            true
        }
    }

    // Function to open Change Username Dialog
    private fun openNameDialog() {
        // Fetching saved username from shared preferences
        var savedName = sharedPref.getString(getString(R.string.user_pref_name_key), "")

        // Creating Dialog
        val mDialogView = LayoutInflater.from(this.context).inflate(R.layout.dialog_change_name, null)
        val mBuilder = AlertDialog.Builder(this.context as Context)
            .setView(mDialogView)
            .setTitle("Change Name")
        val  mAlertDialog = mBuilder.show()
        // Setting saved name as a placeholder/hint
        mDialogView.dialogNameET.hint = savedName

        // Dialog Save Handler
        mDialogView.dialogNameSaveBtn.setOnClickListener {
            val name = mDialogView.dialogNameET.text.toString()

            // Checking whether name field is empty/same as saved name/new
            if (name.isNullOrEmpty()) {
                mDialogView.dialogNameErrorLabel.text = getString(R.string.notify_name_empty)
            }
            else if (name == savedName) {
                mDialogView.dialogNameET.text.clear()
                mDialogView.dialogNameET.clearFocus()
                mDialogView.dialogNameErrorLabel.text = getString(R.string.notify_name_exists)
            }
            else {
                with(sharedPref.edit()) {
                    putString(getString(R.string.user_pref_name_key), name)
                    commit()
                }
                mAlertDialog.dismiss()
                Toast.makeText(this.context, "Name Changed to ".plus(name), Toast.LENGTH_SHORT).show()
            }
        }

        // Dialog Cancel Handler
        mDialogView.dialogNameCancelBtn.setOnClickListener {
            mAlertDialog.dismiss()
        }
    }

    // Function to open Change PIN Dialog
    private fun openPINDialog() {
        // Fetching saved PIN from shared preferences
        var savedPin = sharedPref.getString(getString(R.string.user_pref_pin_key), "")

        // Creating Dialog
        val mDialogView = LayoutInflater.from(this.context).inflate(R.layout.dialog_change_pin, null)
        val mBuilder = AlertDialog.Builder(this.context as Context)
            .setView(mDialogView)
            .setTitle("Change PIN")
        val  mAlertDialog = mBuilder.show()

        // Setting Placeholder/Hints
        mDialogView.dialogPinET.hint = getString(R.string.pin_placeholder)
        mDialogView.dialogCPinET.hint = getString(R.string.confirm_pin_placeholder)

        // Dialog Save Handler
        mDialogView.dialogPinSaveBtn.setOnClickListener {
            val pin = mDialogView.dialogPinET.text.toString()
            val cpin = mDialogView.dialogCPinET.text.toString()

            // Checking whether PIN field is empty/ Confirm PIN is empty/ PINs same as saved PIN/ PIN & Confirm PIN are same/new
            if (pin.isNullOrEmpty()) {
                mDialogView.dialogCPinET.text.clear()
                mDialogView.dialogPinET.text.clear()
                mDialogView.dialogPinET.clearFocus()
                mDialogView.dialogCPinET.clearFocus()
                mDialogView.dialogPINErrorLabel.text = getString(R.string.notify_pin_empty)
            }
            else if (cpin.isNullOrEmpty()) {
                mDialogView.dialogCPinET.text.clear()
                mDialogView.dialogPinET.text.clear()
                mDialogView.dialogPinET.clearFocus()
                mDialogView.dialogCPinET.clearFocus()
                mDialogView.dialogPINErrorLabel.text = getString(R.string.notify_cpin_empty)
            }
            else if (pin == cpin) {
                if (pin == savedPin) {
                    mDialogView.dialogCPinET.text.clear()
                    mDialogView.dialogPinET.text.clear()
                    mDialogView.dialogPinET.clearFocus()
                    mDialogView.dialogCPinET.clearFocus()
                    mDialogView.dialogPINErrorLabel.text = getString(R.string.notify_pin_exists)
                }
                else {
                    with(sharedPref.edit()) {
                        putString(getString(R.string.user_pref_pin_key), pin)
                        commit()
                    }
                    mAlertDialog.dismiss()
                    Toast.makeText(this.context, getString(R.string.notify_pin_change), Toast.LENGTH_SHORT).show()
                }
            }
        }

        // Dialog Cancel Handler
        mDialogView.dialogPinCancelBtn.setOnClickListener {
            mAlertDialog.dismiss()
        }
    }
}