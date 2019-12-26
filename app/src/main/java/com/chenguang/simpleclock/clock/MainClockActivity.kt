package com.chenguang.simpleclock.clock

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.chenguang.simpleclock.R
import com.chenguang.simpleclock.util.Constants

class MainClockActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        if (intent?.hasExtra(Constants.EXTRA_ALARM_TITLE) == true) {
            val alarmTitle = intent.getStringExtra(Constants.EXTRA_ALARM_TITLE)!!
            showAlarmTriggerDialog(alarmTitle)
        }
    }

    private fun showAlarmTriggerDialog(alarmTitle: String) {
        val dialogBuilder = AlertDialog.Builder(this)
            .setTitle(R.string.alarm_trigger_dialog_title)
            .setMessage(getString(R.string.alarm_trigger_dialog_message, alarmTitle))
            .setNeutralButton(android.R.string.ok) { dialog, _ ->
                dialog.dismiss()
            }.setCancelable(true)
        dialogBuilder.show()
    }
}
