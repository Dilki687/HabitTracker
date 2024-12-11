package com.example.habittracker.ui.fragments.habitlist.adapters

import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.navigation.NavDirections
import androidx.navigation.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.example.habittracker.ActivityTimer
import com.example.habittracker.R
import com.example.habittracker.data.models.Habit
import com.example.habittracker.ui.fragments.habitlist.HabitListDirections
import com.example.habittracker.utils.Calculations
import com.google.gson.Gson

class HabitListAdapter : RecyclerView.Adapter<HabitListAdapter.MyViewHolder>() {
    var habitList = emptyList<Habit>()
    var TAG = "HabitListAdapter"
    private val gson = Gson()

    inner class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val cardView: CardView = itemView.findViewById(R.id.cv_cardView)
        val habitIcon: ImageView = itemView.findViewById(R.id.iv_habit_icon)
        val habitTitle: TextView = itemView.findViewById(R.id.tv_item_title)
        val habitDescription: TextView = itemView.findViewById(R.id.tv_item_description)
        val timeElapsed: TextView = itemView.findViewById(R.id.tv_timeElapsed)
        val createdTimeStamp: TextView = itemView.findViewById(R.id.tv_item_createdTimeStamp)
        private val startTimerButton: Button = itemView.findViewById(R.id.btn_start_timer)

        init {
            cardView.setOnClickListener {
                val position = adapterPosition
                Log.d(TAG, "Item clicked at: $position")
                Log.d(TAG, "ID: ${habitList[position].id}")

                val action = HabitListDirections.actionHabitListToUpdateHabitItem2(habitList[position])
                itemView.findNavController().navigate(action)
            }

            // Set up the start timer button click listener
            startTimerButton.setOnClickListener {
                val habitJson = gson.toJson(habitList[adapterPosition])
                val intent = Intent(itemView.context, ActivityTimer::class.java)
                intent.putExtra("habit", habitJson)
                itemView.context.startActivity(intent)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HabitListAdapter.MyViewHolder {
        return MyViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.recycler_habit_item, parent, false)
        )
    }

    override fun onBindViewHolder(holder: HabitListAdapter.MyViewHolder, position: Int) {
        val currentHabit = habitList[position]
        holder.habitIcon.setImageResource(currentHabit.imageId)
        holder.habitTitle.text = currentHabit.habit_title
        holder.habitDescription.text = currentHabit.habit_description
        holder.timeElapsed.text = Calculations.calculateTimeBetweenDates(currentHabit.habit_startTime)
        holder.createdTimeStamp.text = "Since: ${currentHabit.habit_startTime}"
    }

    override fun getItemCount(): Int {
        return habitList.size
    }

    fun setData(habit: List<Habit>) {
        this.habitList = habit
        notifyDataSetChanged()
    }
}
