package com.forgefit.android.ui.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.forgefit.android.data.healthconnect.HealthConnectManager
import com.forgefit.android.data.model.UserProfile
import com.forgefit.android.data.repository.UserRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class ProfileViewModel(
    private val userRepository: UserRepository,
    private val healthConnectManager: HealthConnectManager
) : ViewModel() {

    val userProfile: StateFlow<UserProfile?> = userRepository.getUserProfile()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    private val _healthConnectAvailable = MutableStateFlow(false)
    val healthConnectAvailable: StateFlow<Boolean> = _healthConnectAvailable.asStateFlow()

    private val _healthConnectPermissionsGranted = MutableStateFlow(false)
    val healthConnectPermissionsGranted: StateFlow<Boolean> = _healthConnectPermissionsGranted.asStateFlow()

    init {
        checkHealthConnect()
    }

    private fun checkHealthConnect() {
        viewModelScope.launch {
            _healthConnectAvailable.value = healthConnectManager.isHealthConnectAvailable()
            if (_healthConnectAvailable.value) {
                _healthConnectPermissionsGranted.value = healthConnectManager.hasAllPermissions()
            }
        }
    }

    fun updateProfile(profile: UserProfile) {
        viewModelScope.launch {
            userRepository.updateUserProfile(profile)
        }
    }

    fun refreshHealthConnectStatus() {
        checkHealthConnect()
    }
}
