package com.example.habittracker.ui.viewmodels

import android.app.Application
import android.content.Context
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.habittracker.data.models.Habit
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class HabitViewModel(application: Application) : AndroidViewModel(application) {

    private val sharedPreferences = application.getSharedPreferences("habits_pref", Context.MODE_PRIVATE)
    private val gson = Gson()
    val getAllHabits: MutableLiveData<List<Habit>> = MutableLiveData()

    init {
        loadHabits()  // Load habits when the ViewModel is created
    }

    private fun loadHabits() {
        viewModelScope.launch(Dispatchers.IO) {
            val json = sharedPreferences.getString("habit_list", null)
            val type = object : TypeToken<List<Habit>>() {}.type
            val habits: List<Habit> = json?.let { gson.fromJson(it, type) } ?: emptyList()
            getAllHabits.postValue(habits)  // Update LiveData
        }
    }

    fun addHabit(habit: Habit) {
        viewModelScope.launch(Dispatchers.IO) {
            val habits = getAllHabits.value?.toMutableList() ?: mutableListOf()
            habits.add(habit)
            saveHabits(habits)
            loadHabits()  // Refresh after adding
        }
    }

    fun updateHabit(updatedHabit: Habit) {
        viewModelScope.launch(Dispatchers.IO) {
            val habits = getAllHabits.value?.toMutableList() ?: mutableListOf()
            val index = habits.indexOfFirst { it.id == updatedHabit.id }
            if (index != -1) {
                habits[index] = updatedHabit
                saveHabits(habits)
                loadHabits()  // Refresh after updating
            }
        }
    }

    fun deleteHabit(habit: Habit) {
        viewModelScope.launch(Dispatchers.IO) {
            val habits = getAllHabits.value?.toMutableList() ?: mutableListOf()
            habits.removeIf { it.id == habit.id }
            saveHabits(habits)
            loadHabits()  // Refresh after deleting
        }
    }

    fun deleteAllHabits() {
        viewModelScope.launch(Dispatchers.IO) {
            sharedPreferences.edit().remove("habit_list").apply()
            loadHabits()  // Refresh after deleting all
        }
    }

    private fun saveHabits(habitList: List<Habit>) {
        val json = gson.toJson(habitList)
        sharedPreferences.edit().putString("habit_list", json).apply()
    }
}
