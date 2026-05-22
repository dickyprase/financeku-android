package com.financeku.app.ui.dashboard

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.financeku.app.data.repository.ReportRepository
import com.financeku.app.data.repository.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class DashboardUiState(
    val totalBalance: Double = 0.0,
    val monthlyIncome: Double = 0.0,
    val monthlyExpense: Double = 0.0,
    val overtimePending: Double = 0.0,
    val budgetPercentage: Double = 0.0,
    val isLoading: Boolean = false,
    val error: String? = null
)

@HiltViewModel
class DashboardViewModel @Inject constructor(
    private val reportRepository: ReportRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(DashboardUiState())
    val uiState: StateFlow<DashboardUiState> = _uiState

    init {
        loadDashboard()
    }

    fun loadDashboard() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)
            when (val result = reportRepository.getDashboardReport()) {
                is Resource.Success -> {
                    val data = result.data
                    val income = data.monthlyIncome
                    val expense = data.monthlyExpense
                    val percentage = if (income > 0) (expense / income) * 100 else 0.0

                    _uiState.value = DashboardUiState(
                        totalBalance = data.totalBalance,
                        monthlyIncome = income,
                        monthlyExpense = expense,
                        overtimePending = data.overtimePending,
                        budgetPercentage = percentage,
                        isLoading = false
                    )
                }
                is Resource.Error -> {
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        error = result.message
                    )
                }
                is Resource.Loading -> {}
            }
        }
    }
}
