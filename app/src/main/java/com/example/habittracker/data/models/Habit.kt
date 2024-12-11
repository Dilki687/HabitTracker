@file:Suppress("DEPRECATED_ANNOTATION")

package com.example.habittracker.data.models

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Habit(
    val id: String,
    val habit_title: String,
    val habit_description: String,
    val habit_startTime: String,
    val imageId: Int,
    val dueDate: Long, // Timestamp for the due date
    var timeSpent: Long = 0, // Initialize to 0
    var duration: Long = 0
) : Parcelable
