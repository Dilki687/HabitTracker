package com.example.habittracker.data.models

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class IntroView(val description: String, val image: Int) : Parcelable {
}