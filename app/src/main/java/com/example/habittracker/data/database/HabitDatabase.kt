//package com.example.habittracker.data.database
//
//import android.content.Context
//import android.content.SharedPreferences
//import com.example.habittracker.data.models.Habit
//import com.google.gson.Gson
//import com.google.gson.reflect.TypeToken
//
//class HabitDatabase private constructor(context: Context) {
//
//    private val sharedPreferences: SharedPreferences =
//        context.getSharedPreferences("habit_tracker_prefs", Context.MODE_PRIVATE)
//    private val gson = Gson()
//
//    companion object {
//        @Volatile
//        private var INSTANCE: HabitDatabase? = null
//
//        fun getInstance(context: Context): HabitDatabase {
//            return INSTANCE ?: synchronized(this) {
//                INSTANCE ?: HabitDatabase(context).also { INSTANCE = it }
//            }
//        }
//    }
//
//    fun addHabit(habit: Habit) {
//        val habits = getAllHabits().toMutableList()
//        habits.add(habit)
//        saveHabits(habits)
//    }
//
//    fun updateHabit(updatedHabit: Habit) {
//        val habits = getAllHabits().map {
//            if (it.id == updatedHabit.id) updatedHabit else it
//        }
//        saveHabits(habits)
//    }
//
//    fun deleteHabit(habit: Habit) {
//        val habits = getAllHabits().filter { it.id != habit.id }
//        saveHabits(habits)
//    }
//
//    fun getAllHabits(): List<Habit> {
//        val json = sharedPreferences.getString("habits", null)
//        return if (json != null) {
//            gson.fromJson(json, object : com.google.gson.reflect.TypeToken<List<Habit>>() {}.type)
//        } else {
//            emptyList()
//        }
//    }
//
//    fun deleteAll() {
//        sharedPreferences.edit().remove("habits").apply()
//    }
//
//    private fun saveHabits(habits: List<Habit>) {
//        val json = gson.toJson(habits)
//        sharedPreferences.edit().putString("habits", json).apply()
//    }
//}