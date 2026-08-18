package com.example.ui.collection

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.repository.CatRepository
import com.example.domain.model.Cat
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

enum class CollectionTab(val title: String) {
  ALL("Semua Cuking"),
  FAVORITES("Favorit"),
  MY_FINDS("Temuan Saya")
}

data class CollectionUiState(
  val cats: List<Cat> = emptyList(),
  val filteredCats: List<Cat> = emptyList(),
  val activeTab: CollectionTab = CollectionTab.ALL,
  val searchQuery: String = "",
  val isLoading: Boolean = false
)

class CollectionViewModel(
  private val catRepository: CatRepository
) : ViewModel() {

  private val _activeTab = MutableStateFlow(CollectionTab.ALL)
  private val _searchQuery = MutableStateFlow("")

  val uiState: StateFlow<CollectionUiState> = combine(
    catRepository.getAllCats(),
    _activeTab,
    _searchQuery
  ) { cats, tab, query ->
    val filtered = cats.filter { cat ->
      val matchesTab = when (tab) {
        CollectionTab.ALL -> true
        CollectionTab.FAVORITES -> cat.isFavorite
        CollectionTab.MY_FINDS -> cat.reportedBy.equals("Windu", ignoreCase = true)
      }
      val matchesQuery = query.isBlank() ||
        cat.nickname.contains(query, ignoreCase = true) ||
        cat.notes.contains(query, ignoreCase = true)

      matchesTab && matchesQuery
    }

    CollectionUiState(
      cats = cats,
      filteredCats = filtered,
      activeTab = tab,
      searchQuery = query,
      isLoading = false
    )
  }.stateIn(
    scope = viewModelScope,
    started = SharingStarted.WhileSubscribed(5000),
    initialValue = CollectionUiState(isLoading = true)
  )

  fun setTab(tab: CollectionTab) {
    _activeTab.value = tab
  }

  fun setSearchQuery(query: String) {
    _searchQuery.value = query
  }

  fun toggleFavorite(catId: Long, isFavorite: Boolean) {
    viewModelScope.launch {
      catRepository.toggleFavorite(catId, isFavorite)
    }
  }
}
