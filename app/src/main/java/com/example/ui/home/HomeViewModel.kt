package com.example.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.repository.CatRepository
import com.example.data.repository.UserRepository
import com.example.domain.model.Cat
import com.example.domain.model.DailyMission
import com.example.domain.model.Discovery
import com.example.domain.model.UserProfile
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

data class HomeUiState(
  val userProfile: UserProfile? = null,
  val nearbyCats: List<Cat> = emptyList(),
  val recentDiscoveries: List<Discovery> = emptyList(),
  val dailyMission: DailyMission? = null,
  val isLoading: Boolean = false
)

class HomeViewModel(
  private val catRepository: CatRepository,
  private val userRepository: UserRepository
) : ViewModel() {

  val uiState: StateFlow<HomeUiState> = combine(
    userRepository.userProfileFlow,
    catRepository.getAllCats(),
    catRepository.getRecentDiscoveries()
  ) { profile, cats, discoveries ->
    HomeUiState(
      userProfile = profile,
      nearbyCats = cats.sortedBy { it.distanceMeters },
      recentDiscoveries = discoveries,
      dailyMission = userRepository.dailyMission,
      isLoading = false
    )
  }.stateIn(
    scope = viewModelScope,
    started = SharingStarted.WhileSubscribed(5000),
    initialValue = HomeUiState(isLoading = true)
  )

  fun toggleFavorite(catId: Long, isFavorite: Boolean) {
    viewModelScope.launch {
      catRepository.toggleFavorite(catId, isFavorite)
    }
  }
}
