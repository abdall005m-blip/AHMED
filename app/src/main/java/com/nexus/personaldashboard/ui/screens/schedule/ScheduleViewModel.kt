package com.nexus.personaldashboard.ui.screens.schedule

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nexus.personaldashboard.data.repository.ScheduleRepository
import com.nexus.personaldashboard.domain.model.Schedule
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.util.Calendar
import javax.inject.Inject

@HiltViewModel
class ScheduleViewModel @Inject constructor(
    private val repo: ScheduleRepository
) : ViewModel() {

    val allSchedules = repo.getAllSchedules()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val todayDayOfWeek: Int
        get() {
            // Android Calendar.DAY_OF_WEEK: 1=Sun, 2=Mon... Map to 1=Mon..7=Sun
            val calDay = Calendar.getInstance().get(Calendar.DAY_OF_WEEK)
            return if (calDay == Calendar.SUNDAY) 7 else calDay - 1
        }

    fun upsert(schedule: Schedule) = viewModelScope.launch {
        if (schedule.id == 0L) {
            repo.insert(schedule)
        } else {
            repo.update(schedule)
        }
    }

    fun delete(schedule: Schedule) = viewModelScope.launch {
        repo.delete(schedule)
    }
}
