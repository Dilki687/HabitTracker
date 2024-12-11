package com.example.habittracker.ui.fragments.createhabit

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.widget.Toast

class YourAlarmReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        // Handle the alarm event (e.g., show a notification)
        Toast.makeText(context, "It's time for your habit!", Toast.LENGTH_SHORT).show()
        // Optionally, create and show a notification here
    }
}