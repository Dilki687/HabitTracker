//package com.example.habittracker.logic.repository
//
//import android.content.Context
//import android.content.SharedPreferences
//import com.example.habittracker.data.models.Habit
//import com.google.gson.Gson
//import com.google.gson.reflect.TypeToken
//
//class HabitRepository(context: Context) {
//
//    private val sharedPreferences: SharedPreferences =
//        context.getSharedPreferences("habits_pref", Context.MODE_PRIVATE)
//    private val gson = Gson()
//
//    // Fetch all habits
//    fun getAllHabits(): List<Habit> {
//        val json = sharedPreferences.getString("habit_list", null)
//        return if (json != null) {
//            val type = object : TypeToken<List<Habit>>() {}.type
//            gson.fromJson(json, type)
//        } else {
//            emptyList()
//        }
//    }
//
//    // Add a new habit
//    fun addHabit(habit: Habit) {
//        val habitList = getAllHabits().toMutableList()
//        habitList.add(habit)
//        saveHabits(habitList)
//    }
//
//    // Update an existing habit
//    fun updateHabit(updatedHabit: Habit) {
//        val habitList = getAllHabits().toMutableList()
//        val index = habitList.indexOfFirst { it.id == updatedHabit.id }
//        if (index != -1) {
//            habitList[index] = updatedHabit
//            saveHabits(habitList)
//        }
//    }
//
//    // Delete a habit
//    fun deleteHabit(habit: Habit) {
//        val habitList = getAllHabits().toMutableList()
//        habitList.removeIf { it.id == habit.id }
//        saveHabits(habitList)
//    }
//
//    // Delete all habits
//    fun deleteAllHabits() {
//        sharedPreferences.edit().remove("habit_list").apply()
//    }
//
//    // Save habits to SharedPreferences
//    private fun saveHabits(habitList: List<Habit>) {
//        val json = gson.toJson(habitList)
//        sharedPreferences.edit().putString("habit_list", json).apply()
//    }
//}
