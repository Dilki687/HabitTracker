package com.example.habittracker.notifications

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.IBinder
import android.util.Log
import androidx.core.app.NotificationCompat
import com.example.habittracker.MainActivity
import com.example.habittracker.R
import com.example.habittracker.data.models.Habit
import com.google.gson.Gson

class NotificationService : Service() {
    override fun onStartCommand(intent: Intent, flags: Int, startId: Int): Int {
        val habitId = intent.getStringExtra("habitId") // Assuming habitId is a String
        val habitJson = intent.getStringExtra("habit")
        Log.d("NotificationService", "Starting NotificationService for Habit ID: $habitId")

        if (habitId != null && habitJson != null) {
            val habit = Gson().fromJson(habitJson, Habit::class.java)
            showNotification(habit)
        } else {
            Log.e("NotificationService", "Invalid Habit ID or Habit data")
        }

        return START_NOT_STICKY
    }

    private fun showNotification(habit: Habit) {
        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        val notificationIntent = Intent(this, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
        }

        // Ensure each PendingIntent is unique by using the habit ID as the requestCode
        val pendingIntent = PendingIntent.getActivity(
            this, habit.id.hashCode(), notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val notification = NotificationCompat.Builder(this, "habit_channel")
            .setContentTitle("Habit Reminder: ${habit.habit_title}") // Show habit title in the notification
            .setContentText("Don't forget to complete your habit: ${habit.habit_description}")
            .setSmallIcon(R.drawable.ic_notification) // Ensure you have a notification icon
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .setPriority(NotificationCompat.PRIORITY_HIGH) // Set high priority for important reminders
            .build()

        // Create notification channel if necessary
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                "habit_channel",
                "Habit Notifications",
                NotificationManager.IMPORTANCE_HIGH // Use high importance for habit reminders
            )
            notificationManager.createNotificationChannel(channel)
        }

        notificationManager.notify(habit.id.hashCode(), notification) // Use the habit's ID to uniquely identify notifications
        Log.d("NotificationService", "Notification displayed for Habit ID: ${habit.id}") // Confirm notification displayed
    }

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }
}
