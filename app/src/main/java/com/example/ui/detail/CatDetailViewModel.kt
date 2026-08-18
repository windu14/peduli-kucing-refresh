package com.example.ui.detail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.model.CatStatus
import com.example.data.repository.CatRepository
import com.example.domain.model.Cat
import com.example.domain.model.Discovery
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

data class CatDetailUiState(
  val cat: Cat? = null,
  val discoveries: List<Discovery> = emptyList(),
  val isLoading: Boolean = true,
  val showUpdateSheet: Boolean = false,
  val xpFeedbackGained: Int? = null
)

class CatDetailViewModel(
  private val catId: Long,
  private val catRepository: CatRepository
) : ViewModel() {

  private val _showUpdateSheet = MutableStateFlow(false)
  private val _xpFeedback = MutableStateFlow<Int?>(null)

  val uiState: StateFlow<CatDetailUiState> = combine(
    catRepository.getCatById(catId),
    catRepository.getDiscoveriesForCat(catId),
    _showUpdateSheet,
    _xpFeedback
  ) { cat, discoveries, showSheet, xp ->
    CatDetailUiState(
      cat = cat,
      discoveries = discoveries,
      isLoading = false,
      showUpdateSheet = showSheet,
      xpFeedbackGained = xp
    )
  }.stateIn(
    scope = viewModelScope,
    started = SharingStarted.WhileSubscribed(5000),
    initialValue = CatDetailUiState()
  )

  fun toggleFavorite() {
    val currentCat = uiState.value.cat ?: return
    viewModelScope.launch {
      catRepository.toggleFavorite(currentCat.id, !currentCat.isFavorite)
    }
  }

  fun showUpdateBottomSheet(show: Boolean) {
    _showUpdateSheet.value = show
  }

  fun updateStatus(newStatus: CatStatus, notes: String, onComplete: (Int) -> Unit) {
    val currentCat = uiState.value.cat ?: return
    viewModelScope.launch {
      val xp = catRepository.updateCatStatus(currentCat.id, newStatus, notes)
      _showUpdateSheet.value = false
      _xpFeedback.value = xp
      onComplete(xp)
    }
  }

  fun recordSeenAgain(onComplete: (Int) -> Unit) {
    val currentCat = uiState.value.cat ?: return
    viewModelScope.launch {
      val xp = catRepository.updateCatStatus(currentCat.id, CatStatus.SEEN_AGAIN, "Terlihat kembali di lokasi.")
      _xpFeedback.value = xp
      onComplete(xp)
    }
  }

  fun clearXpFeedback() {
    _xpFeedback.value = null
  }
}
