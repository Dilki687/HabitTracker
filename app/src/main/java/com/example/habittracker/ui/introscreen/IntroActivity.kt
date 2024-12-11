package com.example.habittracker.ui.introscreen

import ViewPagerAdapter
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.viewpager2.widget.ViewPager2
import com.example.habittracker.MainActivity
import com.example.habittracker.R
import com.example.habittracker.data.models.IntroView
import com.example.habittracker.databinding.ActivityIntroBinding

class IntroActivity : AppCompatActivity() {
    private lateinit var binding: ActivityIntroBinding
    lateinit var introView: List<IntroView>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityIntroBinding.inflate(layoutInflater)
        setContentView(binding.root)

        addToIntroView()

        binding.viewPager2.adapter = ViewPagerAdapter(introView)
        binding.viewPager2.orientation = ViewPager2.ORIENTATION_HORIZONTAL

        binding.circleIndicator.setViewPager(binding.viewPager2)

        binding.viewPager2.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageScrolled(
                position: Int,
                positionOffset: Float,
                positionOffsetPixels: Int
            ) {
                if (position == 2) {
                    animationButton()
                }
                super.onPageScrolled(position, positionOffset, positionOffsetPixels)
            }
        })
    }

    private fun animationButton() {
        binding.btnStartApp.visibility = View.VISIBLE

        binding.btnStartApp.animate().apply {
            duration = 1400
            alpha(1f)

            binding.btnStartApp.setOnClickListener {
                val i = Intent(applicationContext, MainActivity::class.java)
                startActivity(i)
                finish()
            }
        }.start()
    }

    private fun addToIntroView() {
        //Create some items that you want to add to your viewpager

        introView = listOf(
            IntroView("Welcome to Habit Tracker!", R.drawable.intro_pic),
            IntroView("This app is designed to keep track of your habits, " +
                    "whether it's a good one, or a bad one.", R.drawable.intro_pic),
            IntroView("Good luck! Tap on the button below to get started with using the app!", R.drawable.intro_pic),
        )
        Log.d("IntroActivity", "IntroView size: ${introView.size}")
    }
}