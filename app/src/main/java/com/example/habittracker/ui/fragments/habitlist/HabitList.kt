package com.example.habittracker.ui.fragments.habitlist

import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.habittracker.R
import com.example.habittracker.data.models.Habit
import com.example.habittracker.databinding.FragmentHabitListBinding
import com.example.habittracker.ui.fragments.habitlist.adapters.HabitListAdapter
import com.example.habittracker.ui.viewmodels.HabitViewModel

class HabitList : Fragment(R.layout.fragment_habit_list) {
    private lateinit var habitList: List<Habit>
    private lateinit var habitViewModel: HabitViewModel
    private lateinit var adapter: HabitListAdapter

    private var _binding: FragmentHabitListBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        _binding = FragmentHabitListBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        //Adapter
        adapter = HabitListAdapter()
        binding.rvHabits.adapter = adapter
        binding.rvHabits.layoutManager = LinearLayoutManager(context)

        //viewModel
        habitViewModel = ViewModelProvider(this).get(HabitViewModel::class.java)

        habitViewModel.getAllHabits.observe(viewLifecycleOwner, Observer{
            adapter.setData(it)
            habitList = it

            if (it.isEmpty()) {
                binding.rvHabits.visibility = View.GONE
                binding.tvEmptyView.visibility = View.VISIBLE
            } else {
                binding.rvHabits.visibility = View.VISIBLE
                binding.tvEmptyView.visibility = View.GONE
            }
        })

        setHasOptionsMenu(true)
        binding.swipeToRefresh.setOnRefreshListener{
            adapter.setData(habitList)
            binding.swipeToRefresh.isRefreshing = false
        }

        binding.fabAdd.setOnClickListener {
            findNavController().navigate(R.id.action_habitList_to_createHabitsItem)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.nav_main, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.nav_delete -> habitViewModel.deleteAllHabits()
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
