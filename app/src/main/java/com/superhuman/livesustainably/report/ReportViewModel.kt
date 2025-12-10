package com.superhuman.livesustainably.report

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.gson.Gson
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class ReportJsonData(
    val weeklyStats: StatsData,
    val monthlyStats: StatsData,
    val dailyData: List<DailyData>,
    val transportBreakdown: List<TransportType>,
    val achievements: List<Achievement>
)

data class StatsData(
    val co2Saved: Double,
    val transportTrips: Int,
    val bikeKm: Double,
    val walkKm: Double,
    val publicTransportTrips: Int,
    val carTripsAvoided: Int,
    val sustainableMeals: Int,
    val recycledItems: Int,
    val waterSaved: Double
)

data class DailyData(
    val day: String,
    val co2: Double,
    val transport: Int
)

data class TransportType(
    val type: String,
    val percentage: Int,
    val color: String
)

data class Achievement(
    val title: String,
    val value: String,
    val icon: String
)

enum class ReportPeriod {
    WEEKLY, MONTHLY
}

data class ReportState(
    val selectedPeriod: ReportPeriod = ReportPeriod.WEEKLY,
    val weeklyStats: StatsData? = null,
    val monthlyStats: StatsData? = null,
    val dailyData: List<DailyData> = emptyList(),
    val transportBreakdown: List<TransportType> = emptyList(),
    val achievements: List<Achievement> = emptyList(),
    val isLoading: Boolean = true
)

@HiltViewModel
class ReportViewModel @Inject constructor(
    @ApplicationContext private val context: Context
) : ViewModel() {

    private val _state = MutableStateFlow(ReportState())
    val state: StateFlow<ReportState> = _state.asStateFlow()

    init {
        loadReportData()
    }

    private fun loadReportData() {
        viewModelScope.launch {
            try {
                _state.update { it.copy(isLoading = true) }

                val jsonString = context.assets.open("report_mock.json")
                    .bufferedReader()
                    .use { it.readText() }

                val gson = Gson()
                val data = gson.fromJson(jsonString, ReportJsonData::class.java)

                _state.update {
                    it.copy(
                        weeklyStats = data.weeklyStats,
                        monthlyStats = data.monthlyStats,
                        dailyData = data.dailyData,
                        transportBreakdown = data.transportBreakdown,
                        achievements = data.achievements,
                        isLoading = false
                    )
                }
            } catch (e: Exception) {
                _state.update { it.copy(isLoading = false) }
            }
        }
    }

    fun selectPeriod(period: ReportPeriod) {
        _state.update { it.copy(selectedPeriod = period) }
    }

    fun getCurrentStats(): StatsData? {
        return when (_state.value.selectedPeriod) {
            ReportPeriod.WEEKLY -> _state.value.weeklyStats
            ReportPeriod.MONTHLY -> _state.value.monthlyStats
        }
    }
}
