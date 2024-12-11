package com.example.habittracker

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.os.CountDownTimer
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.NotificationCompat
import com.example.habittracker.data.models.Habit
import com.example.habittracker.MainActivity
import com.example.habittracker.databinding.ActivityTimerBinding
import com.google.gson.Gson

class ActivityTimer : AppCompatActivity() {

    private lateinit var binding: ActivityTimerBinding
    private lateinit var habit: Habit
    private val gson = Gson()
    private var countdownTimer: CountDownTimer? = null
    private var timeLeftInMillis: Long = 0
    private var isTimerRunning = false

    companion object {
        private const val REQUEST_NOTIFICATION_PERMISSION = 100
        private const val CHANNEL_ID = "habit_tracker_channel"
        private const val CHANNEL_NAME = "Habit Tracker Notifications"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityTimerBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Request notification permission for Android 13+
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (checkSelfPermission(android.Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(arrayOf(android.Manifest.permission.POST_NOTIFICATIONS), REQUEST_NOTIFICATION_PERMISSION)
            }
        }

        // Get the Habit object from Intent
        val habitJson = intent.getStringExtra("habit")
        habit = gson.fromJson(habitJson, Habit::class.java)

        // Calculate time left until the due date (in milliseconds)
        val currentTime = System.currentTimeMillis()
        timeLeftInMillis = habit.dueDate - currentTime

        // Check if time left is negative
        if (timeLeftInMillis < 0) {
            Toast.makeText(this, "The due date has already passed.", Toast.LENGTH_SHORT).show()
            finish() // Close activity if due date has passed
            return
        }

        // Update the UI with the initial time
        updateTimerUI(timeLeftInMillis)

        // Set button click listeners
        binding.startButton.setOnClickListener {
            if (isTimerRunning) {
                stopTimer()
            } else {
                startTimer()
            }
        }
        binding.resetButton.setOnClickListener { resetTimer() }

        // Set initial button text
        binding.startButton.text = "Start"
    }

    private fun startTimer() {
        if (isTimerRunning) return

        isTimerRunning = true
        binding.startButton.text = "Stop"

        countdownTimer = object : CountDownTimer(timeLeftInMillis, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                timeLeftInMillis = millisUntilFinished
                updateTimerUI(timeLeftInMillis)
            }

            override fun onFinish() {
                isTimerRunning = false
                binding.startButton.text = "Start"
                Toast.makeText(this@ActivityTimer, "Timer Finished", Toast.LENGTH_SHORT).show()
                showNotification("Timer Finished", "Your habit timer has completed!")
                resetTimer()
            }
        }.start()
    }

    private fun stopTimer() {
        countdownTimer?.cancel()
        isTimerRunning = false
        binding.startButton.text = "Start"
    }

    private fun resetTimer() {
        stopTimer()
        timeLeftInMillis = habit.dueDate - System.currentTimeMillis() // Reset to remaining time
        updateTimerUI(timeLeftInMillis)
    }

    private fun updateTimerUI(timeInMillis: Long) {
        val seconds = (timeInMillis / 1000) % 60
        val minutes = (timeInMillis / (1000 * 60)) % 60
        val hours = (timeInMillis / (1000 * 60 * 60)) % 24
        val days = timeInMillis / (1000 * 60 * 60 * 24)

        binding.valueDays.text = String.format("%02d", days)
        binding.valueHours.text = String.format("%02d", hours)
        binding.valueMinutes.text = String.format("%02d", minutes)
        binding.valueSeconds.text = String.format("%02d", seconds)
    }

    private fun showNotification(title: String, message: String) {
        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID, CHANNEL_NAME,
                NotificationManager.IMPORTANCE_DEFAULT
            )
            notificationManager.createNotificationChannel(channel)
        }

        val intent = Intent(this, MainActivity::class.java)
        val pendingIntent = PendingIntent.getActivity(
            this, 0, intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val notification = NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle(title)
            .setContentText(message)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .build()

        notificationManager.notify(1, notification)
    }

    override fun onDestroy() {
        super.onDestroy()
        countdownTimer?.cancel()
    }

    override fun onPause() {
        super.onPause()
        if (isTimerRunning) {
            stopTimer()
        }
    }

    override fun onResume() {
        super.onResume()
        if (isTimerRunning) {
            startTimer() // Resume timer if it was running
        } else {
            updateTimerUI(timeLeftInMillis) // Update UI to show remaining time
        }
    }
}
