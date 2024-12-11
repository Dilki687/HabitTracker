package com.example.habittracker

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.NavigationUI.setupActionBarWithNavController
import com.example.habittracker.ui.introscreen.IntroActivity

class MainActivity : AppCompatActivity() {
    private lateinit var navController: NavController
    private var userFirstTime = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Initialize navController
        val navHostFragment = supportFragmentManager
            .findFragmentById(R.id.navHostFragment) as NavHostFragment
        navController = navHostFragment.navController

        // Check whether it's the user's first time
        loadData()

        // If it's the user's first time, show the intro screen
        if (userFirstTime) {
            userFirstTime = false
            saveData()
            val i = Intent(this, IntroActivity::class.java)
            startActivity(i)
            finish()  // Close this activity so that the user doesn't return to it
            return  // Stop further execution
        }

        // Set up navigation with the action bar if needed (optional)
        setupActionBarWithNavController(this, navController)
    }

    // Ensure the back button navigates properly in the navigation graph
    override fun onSupportNavigateUp(): Boolean {
        return navController.navigateUp() || super.onSupportNavigateUp()
    }

    // Save user data (whether it's the first time or not)
    private fun saveData() {
        val sp = getSharedPreferences("SHARED_PREFS", MODE_PRIVATE)
        sp.edit().apply {
            putBoolean("BOOLEAN_FIRST_TIME", userFirstTime)
            apply()
        }
    }

    // Load saved data (check if it's the user's first time)
    private fun loadData() {
        val sp = getSharedPreferences("SHARED_PREFS", MODE_PRIVATE)
        userFirstTime = sp.getBoolean("BOOLEAN_FIRST_TIME", true)
    }
}
