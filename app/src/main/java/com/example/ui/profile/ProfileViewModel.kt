package com.example.ui.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.repository.UserRepository
import com.example.domain.model.Achievement
import com.example.domain.model.UserProfile
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn

data class ProfileUiState(
  val userProfile: UserProfile? = null,
  val achievements: List<Achievement> = emptyList(),
  val isLoading: Boolean = false
)

class ProfileViewModel(
  private val userRepository: UserRepository
) : ViewModel() {

  val uiState: StateFlow<ProfileUiState> = combine(
    userRepository.userProfileFlow,
    userRepository.achievementsFlow
  ) { profile, achievements ->
    ProfileUiState(
      userProfile = profile,
      achievements = achievements,
      isLoading = false
    )
  }.stateIn(
    scope = viewModelScope,
    started = SharingStarted.WhileSubscribed(5000),
    initialValue = ProfileUiState(isLoading = true)
  )
}
