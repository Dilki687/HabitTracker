package com.example.habittracker.ui.fragments.updatehabit

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.DatePicker
import android.widget.TimePicker
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.example.habittracker.R
import com.example.habittracker.data.models.Habit
import com.example.habittracker.databinding.FragmentUpdateHabitItemBinding
import com.example.habittracker.ui.viewmodels.HabitViewModel
import com.example.habittracker.utils.Calculations
import com.google.gson.Gson
import java.util.Calendar
import java.text.SimpleDateFormat
import java.util.Locale


class UpdateHabitItem : Fragment(R.layout.fragment_update_habit_item), TimePickerDialog.OnTimeSetListener, DatePickerDialog.OnDateSetListener {

    private var title = ""
    private var description = ""
    private var drawableSelected = 0
    private var timeStamp = ""

    private var day = 0
    private var month = 0
    private var year = 0
    private var hour = 0
    private var minute = 0

    private var cleanDate = ""
    private var cleanTime = ""

    private lateinit var habitViewModel: HabitViewModel
    private val args by navArgs<UpdateHabitItemArgs>()

    private lateinit var binding: FragmentUpdateHabitItemBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentUpdateHabitItemBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        habitViewModel = ViewModelProvider(this).get(HabitViewModel::class.java)

        // Retrieve data from our habit list and populate the form
        binding.etHabitTitleUpdate.setText(args.selectedHabit.habit_title)
        binding.etHabitDescriptionUpdate.setText(args.selectedHabit.habit_description)

        // Set the previous date and time
        val dateTimeFormat = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())

        // Parse the existing habit's start time
        val existingStartTime = args.selectedHabit.habit_startTime
        try {
            val date = dateTimeFormat.parse(existingStartTime)

            // Set the formatted date and time in your TextViews
            val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
            val timeFormat = SimpleDateFormat("HH:mm", Locale.getDefault())

            // Store the previous date and time to use if not updated
            cleanDate = dateFormat.format(date)
            cleanTime = timeFormat.format(date)

            // Display the previous date and time
            binding.tvDateSelectedUpdate.text = "Date: $cleanDate"
            binding.tvTimeSelectedUpdate.text = "Time: $cleanTime"
        } catch (e: Exception) {
            // Handle potential parsing errors
            Log.e("UpdateHabit", "Error parsing date: ${e.message}")
        }

        drawableSelected()
        pickDateAndTime()

        // Set onClickListener for the update button
        binding.btnConfirmUpdate.setOnClickListener {
            updateHabit()  // Handle update logic
        }

        setHasOptionsMenu(true)
    }

    private fun updateHabit() {
        Log.d("UpdateHabit", "Confirm button clicked")
        title = binding.etHabitTitleUpdate.text.toString()
        description = binding.etHabitDescriptionUpdate.text.toString()
        timeStamp = "$cleanDate $cleanTime"

        // Calculate due date as a timestamp (in milliseconds)
        val dueDate = Calendar.getInstance().apply {
            set(year, month, day, hour, minute, 0) // Set to the selected date and time
            set(Calendar.SECOND, 0)
        }.timeInMillis

        if (title.isNotEmpty() && description.isNotEmpty() && timeStamp.isNotEmpty() && drawableSelected != 0) {
            val updatedHabit = Habit(
                id = args.selectedHabit.id,
                habit_title = title,
                habit_description = description,
                habit_startTime = timeStamp,
                imageId = drawableSelected,
                dueDate = dueDate // Pass the calculated dueDate
            )
            habitViewModel.updateHabit(updatedHabit)
            Toast.makeText(context, "Habit updated successfully!", Toast.LENGTH_SHORT).show()
            findNavController().navigate(R.id.action_updateHabitItem2_to_habitList)
        } else {
            Toast.makeText(context, "Please fill all the fields", Toast.LENGTH_SHORT).show()
        }
    }



    private fun saveHabit(habit: Habit) {
        val sharedPreferences = requireActivity().getSharedPreferences("habits", Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        val gson = Gson()
        val json = gson.toJson(habit) // Convert Habit to JSON

        editor.putString(habit.id, json) // Use habit ID as the key
        editor.apply()
    }

    private fun getHabit(id: String): Habit? {
        val sharedPreferences = requireActivity().getSharedPreferences("habits", Context.MODE_PRIVATE)
        val gson = Gson()
        val json = sharedPreferences.getString(id, null) // Get the JSON string by ID

        return gson.fromJson(json, Habit::class.java) // Convert JSON back to Habit object
    }


    private fun drawableSelected() {
        // Select a drawable for the updated habit
        binding.ivFastFoodSelectedUpdate.setOnClickListener {
            binding.ivFastFoodSelectedUpdate.isSelected = true
            drawableSelected = R.drawable.fastfood
            resetOtherSelections(R.drawable.fastfood)
        }

        binding.ivSmokingSelectedUpdate.setOnClickListener {
            binding.ivSmokingSelectedUpdate.isSelected = true
            drawableSelected = R.drawable.smoking
            resetOtherSelections(R.drawable.smoking)
        }

        binding.ivTeaSelectedUpdate.setOnClickListener {
            binding.ivTeaSelectedUpdate.isSelected = true
            drawableSelected = R.drawable.tea
            resetOtherSelections(R.drawable.tea)
        }
    }

    // Helper function to reset other drawable selections
    private fun resetOtherSelections(selectedDrawable: Int) {
        when (selectedDrawable) {
            R.drawable.fastfood -> {
                binding.ivSmokingSelectedUpdate.isSelected = false
                binding.ivTeaSelectedUpdate.isSelected = false
            }
            R.drawable.smoking -> {
                binding.ivFastFoodSelectedUpdate.isSelected = false
                binding.ivTeaSelectedUpdate.isSelected = false
            }
            R.drawable.tea -> {
                binding.ivFastFoodSelectedUpdate.isSelected = false
                binding.ivSmokingSelectedUpdate.isSelected = false
            }
        }
    }

    private fun pickDateAndTime() {
        // Select the date and time for the updated habit
        binding.btnPickDateUpdate.setOnClickListener {
            getDateCalendar()
            DatePickerDialog(requireContext(), this, year, month, day).show()
        }

        binding.btnPickTimeUpdate.setOnClickListener {
            getTimeCalendar()
            TimePickerDialog(context, this, hour, minute, true).show()
        }
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

    override fun onDateSet(p0: DatePicker?, yearX: Int, monthX: Int, dayX: Int) {
        cleanDate = Calculations.cleanDate(dayX, monthX, yearX)
        binding.tvDateSelectedUpdate.text = "Date: $cleanDate"
    }

    override fun onTimeSet(p0: TimePicker?, p1: Int, p2: Int) {
        cleanTime = Calculations.cleanTime(p1, p2)
        binding.tvTimeSelectedUpdate.text = "Time: $cleanTime"
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.single_item_menu, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.nav_delete -> deleteHabit(args.selectedHabit)
        }
        return super.onOptionsItemSelected(item)
    }

    // Delete the selected habit
    private fun deleteHabit(habit: Habit) {
        habitViewModel.deleteHabit(habit)
        Toast.makeText(context, "Habit successfully deleted!", Toast.LENGTH_SHORT).show()
        findNavController().navigate(R.id.action_updateHabitItem2_to_habitList)
    }
}
