package com.financeku.app.ui.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.financeku.app.data.local.datastore.TokenDataStore
import com.financeku.app.data.repository.AuthRepository
import com.financeku.app.data.repository.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch
import javax.inject.Inject

data class ProfileUiState(
    val name: String = "",
    val email: String = "",
    val role: String = "user",
    val walletCount: Int = 0,
    val transactionCount: Int = 0,
    val overtimeCount: Int = 0,
    val isLoading: Boolean = false,
    val error: String? = null,
    val changePasswordState: ChangePasswordState = ChangePasswordState.Idle
)

sealed class ChangePasswordState {
    data object Idle : ChangePasswordState()
    data object Loading : ChangePasswordState()
    data object Success : ChangePasswordState()
    data class Error(val message: String) : ChangePasswordState()
}

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val authRepository: AuthRepository,
    private val tokenDataStore: TokenDataStore
) : ViewModel() {

    private val _uiState = MutableStateFlow(ProfileUiState())
    val uiState: StateFlow<ProfileUiState> = _uiState

    init {
        loadProfile()
    }

    private fun loadProfile() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)

            val name = tokenDataStore.userName.firstOrNull() ?: ""
            val email = tokenDataStore.userEmail.firstOrNull() ?: ""

            _uiState.value = _uiState.value.copy(
                name = name,
                email = email,
                isLoading = false
            )

            // Load full profile from API
            when (val result = authRepository.getMe()) {
                is Resource.Success -> {
                    val user = result.data
                    _uiState.value = _uiState.value.copy(
                        name = user.name,
                        email = user.email
                    )
                }
                is Resource.Error -> {
                    _uiState.value = _uiState.value.copy(error = result.message)
                }
                is Resource.Loading -> {}
            }
        }
    }

    fun changePassword(currentPassword: String, newPassword: String, confirmation: String) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(changePasswordState = ChangePasswordState.Loading)
            // Use a simple approach - call API directly
            try {
                val response = authRepository.changePassword(currentPassword, newPassword, confirmation)
                when (response) {
                    is Resource.Success -> {
                        _uiState.value = _uiState.value.copy(changePasswordState = ChangePasswordState.Success)
                    }
                    is Resource.Error -> {
                        _uiState.value = _uiState.value.copy(
                            changePasswordState = ChangePasswordState.Error(response.message)
                        )
                    }
                    is Resource.Loading -> {}
                }
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    changePasswordState = ChangePasswordState.Error(e.message ?: "Failed")
                )
            }
        }
    }

    fun resetChangePasswordState() {
        _uiState.value = _uiState.value.copy(changePasswordState = ChangePasswordState.Idle)
    }

    fun logout() {
        viewModelScope.launch {
            tokenDataStore.clearTokens()
        }
    }
}
