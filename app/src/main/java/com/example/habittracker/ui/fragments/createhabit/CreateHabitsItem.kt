package com.example.habittracker.ui.fragments.createhabit

import android.app.AlarmManager
import android.app.DatePickerDialog
import android.app.PendingIntent
import android.app.TimePickerDialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.DatePicker
import android.widget.TimePicker
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.example.habittracker.R
import com.example.habittracker.data.models.Habit
import com.example.habittracker.databinding.FragmentCreateHabitsItemBinding
import com.example.habittracker.ui.viewmodels.HabitViewModel
import com.example.habittracker.utils.Calculations
import java.util.*

class CreateHabitsItem : Fragment(R.layout.fragment_create_habits_item),
    TimePickerDialog.OnTimeSetListener, DatePickerDialog.OnDateSetListener {

    private var title = ""
    private var description = ""
    private var drawableSelected = 0
    private var timeStamp = ""

    private lateinit var habitViewModel: HabitViewModel
    private lateinit var binding: FragmentCreateHabitsItemBinding

    private var day = 0
    private var month = 0
    private var year = 0
    private var hour = 0
    private var minute = 0

    private var cleanDate = ""
    private var cleanTime = ""

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentCreateHabitsItemBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        habitViewModel = ViewModelProvider(this).get(HabitViewModel::class.java)

        //Add habit to database
        binding.btnConfirm.setOnClickListener {
            addHabitToDB()
        }
        //Pick a date and time
        pickDateAndTime()

        //Selected and image to put into our list
        drawableSelected()
    }

    private fun pickDateAndTime(){
        binding.btnPickDate.setOnClickListener {
            getDateCalendar()
            DatePickerDialog(requireContext(), this, year, month, day).show()
        }

        binding.btnPickTime.setOnClickListener {
            getTimeCalendar()
            TimePickerDialog(requireContext(), this, hour, minute, true).show()
        }
    }

    private fun addHabitToDB() {
        // Get text from editTexts
        title = binding.etHabitTitle.text.toString()
        description = binding.etHabitDescription.text.toString()

        // Create a timestamp string for our RecyclerView
        timeStamp = "$cleanDate $cleanTime"

        // Check that the form is complete before submitting data to the database
        if (!(title.isEmpty() || description.isEmpty() || timeStamp.isEmpty() || drawableSelected == 0)) {
            // Calculate due date as a timestamp (in milliseconds)
            val dueDate = Calendar.getInstance().apply {
                set(year, month, day, hour, minute, 0) // Set to the selected date and time
                set(Calendar.SECOND, 0)
            }.timeInMillis

            // Define the duration as a Long (for example, 1 hour = 3600000 milliseconds)
            val durationInHours = 1L // Duration in hours
            val durationInMillis = durationInHours * 60 * 60 * 1000 // Convert hours to milliseconds

            // Create a new Habit object using UUID
            val newHabit = Habit(
                id = UUID.randomUUID().toString(),
                habit_title = title,
                habit_description = description,
                habit_startTime = timeStamp,
                imageId = drawableSelected,
                dueDate = dueDate, // Pass the calculated dueDate
                duration = durationInMillis // Pass the calculated duration as Long
            )

            // Add the habit if all the fields are filled
            habitViewModel.addHabit(newHabit)
            Toast.makeText(context, "Habit created successfully!", Toast.LENGTH_SHORT).show()

            // Navigate back to our home fragment
            findNavController().navigate(R.id.action_createHabitsItem_to_habitList)
        } else {
            Toast.makeText(context, "Please fill all the fields", Toast.LENGTH_SHORT).show()
        }
    }

    private fun drawableSelected() {

        binding.ivFastFoodSelected.setOnClickListener {
            binding.ivFastFoodSelected.isSelected = !binding.ivFastFoodSelected.isSelected
            drawableSelected = R.drawable.fastfood

            //de-select the other options when we pick an image
            binding.ivSmokingSelected.isSelected = false
            binding.ivTeaSelected.isSelected = false
        }

        binding.ivSmokingSelected.setOnClickListener {
            binding.ivSmokingSelected.isSelected = !binding.ivSmokingSelected.isSelected
            drawableSelected = R.drawable.smoking

            //de-select the other options when we pick an image
            binding.ivFastFoodSelected.isSelected = false
            binding.ivTeaSelected.isSelected = false
        }

        binding.ivTeaSelected.setOnClickListener {
            binding.ivTeaSelected.isSelected = !binding.ivTeaSelected.isSelected
            drawableSelected = R.drawable.tea

            //de-select the other options when we pick an image
            binding.ivFastFoodSelected.isSelected = false
            binding.ivSmokingSelected.isSelected = false
        }


    }

    override fun onDateSet(p0: DatePicker?, yearX: Int, monthX: Int, dayX: Int) {

        cleanDate = Calculations.cleanDate(dayX, monthX, yearX)
        binding.tvDateSelected.text = "Date: $cleanDate"
    }

    private fun getTimeCalendar() {
        val cal = Calendar.getInstance()
        hour = cal.get(Calendar.HOUR_OF_DAY)
        minute = cal.get(Calendar.MINUTE)
    }

    private fun getDateCalendar() {
        val cal = Calendar.getInstance()
        day = cal.get(Calendar.DAY_OF_MONTH)
        month = cal.get(Calendar.MONTH)
        year = cal.get(Calendar.YEAR)
    }

    override fun onTimeSet(TimePicker: TimePicker?, p1: Int, p2: Int) {
        Log.d("Fragment", "Time: $p1:$p2")

        cleanTime = Calculations.cleanTime(p1, p2)
        binding.tvTimeSelected.text = "Time: $cleanTime"
    }

    private fun scheduleAlarm() {
        val calendar = Calendar.getInstance().apply {
            set(year, month, day, hour, minute, 0) // Use the selected date and time
            set(Calendar.SECOND, 0)
        }

        val alarmManager = requireActivity().getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val intent = Intent(requireContext(), YourAlarmReceiver::class.java) // Replace with your BroadcastReceiver
        val alarmIntent = PendingIntent.getBroadcast(requireContext(), 0, intent, PendingIntent.FLAG_UPDATE_CURRENT)

        // Schedule the alarm
        alarmManager.setExact(AlarmManager.RTC_WAKEUP, calendar.timeInMillis, alarmIntent)
    }

}