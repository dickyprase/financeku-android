package com.financeku.app.ui.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.financeku.app.data.local.datastore.TokenDataStore
import com.financeku.app.data.repository.ProfileRepository
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
    val error: String? = null
)

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val profileRepository: ProfileRepository,
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
            when (val result = profileRepository.getProfile()) {
                is Resource.Success -> {
                    val profile = result.data
                    _uiState.value = _uiState.value.copy(
                        name = profile.name,
                        email = profile.email,
                        role = profile.role ?: "user"
                    )
                }
                is Resource.Error -> {
                    _uiState.value = _uiState.value.copy(error = result.message)
                }
                is Resource.Loading -> {}
            }
        }
    }

    fun logout() {
        viewModelScope.launch {
            tokenDataStore.clearTokens()
        }
    }
}
