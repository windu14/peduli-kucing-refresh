package com.example.ui.explore

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.model.CatCondition
import com.example.data.model.CatStatus
import com.example.data.repository.CatRepository
import com.example.domain.model.Cat
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

enum class MapFilterType(val label: String) {
  ALL("Semua"),
  NEW("Baru"),
  NEED_HELP("Butuh Bantuan"),
  HANDLED("Sudah Ditangani")
}

data class ExploreUiState(
  val cats: List<Cat> = emptyList(),
  val filteredCats: List<Cat> = emptyList(),
  val selectedCat: Cat? = null,
  val activeFilter: MapFilterType = MapFilterType.ALL,
  val searchQuery: String = "",
  val userLatitude: Double = -6.9175,
  val userLongitude: Double = 107.6191,
  val mapCenterLat: Double = -6.9175,
  val mapCenterLng: Double = 107.6191,
  val zoomLevel: Float = 15f
)

class ExploreViewModel(
  private val catRepository: CatRepository
) : ViewModel() {

  private val _activeFilter = MutableStateFlow(MapFilterType.ALL)
  private val _searchQuery = MutableStateFlow("")
  private val _selectedCat = MutableStateFlow<Cat?>(null)
  private val _mapCenterLat = MutableStateFlow(-6.9175)
  private val _mapCenterLng = MutableStateFlow(107.6191)
  private val _zoomLevel = MutableStateFlow(15f)

  private data class FilterState(
    val filter: MapFilterType,
    val query: String,
    val selectedCat: Cat?
  )

  private val _filterState = combine(_activeFilter, _searchQuery, _selectedCat) { filter, query, selected ->
    FilterState(filter, query, selected)
  }

  private data class ViewportState(
    val lat: Double,
    val lng: Double,
    val zoom: Float
  )

  private val _viewportState = combine(_mapCenterLat, _mapCenterLng, _zoomLevel) { lat, lng, zoom ->
    ViewportState(lat, lng, zoom)
  }

  val uiState: StateFlow<ExploreUiState> = combine(
    catRepository.getAllCats(),
    _filterState,
    _viewportState
  ) { cats, filterState, viewport ->
    val filtered = cats.filter { cat ->
      val matchesFilter = when (filterState.filter) {
        MapFilterType.ALL -> true
        MapFilterType.NEW -> cat.status == CatStatus.NEWLY_FOUND
        MapFilterType.NEED_HELP -> cat.condition == CatCondition.NAMPAK_TIDAK_SEHAT || cat.condition == CatCondition.SAKIT || cat.condition == CatCondition.PRIHATIN || cat.status == CatStatus.NEED_ATTENTION || cat.status == CatStatus.INJURED
        MapFilterType.HANDLED -> cat.status == CatStatus.HANDLED || cat.condition == CatCondition.SEHAT || cat.condition == CatCondition.SEDANG
      }
      val matchesQuery = filterState.query.isBlank() ||
        cat.nickname.contains(filterState.query, ignoreCase = true) ||
        cat.notes.contains(filterState.query, ignoreCase = true)

      matchesFilter && matchesQuery
    }

    ExploreUiState(
      cats = cats,
      filteredCats = filtered,
      selectedCat = filterState.selectedCat,
      activeFilter = filterState.filter,
      searchQuery = filterState.query,
      mapCenterLat = viewport.lat,
      mapCenterLng = viewport.lng,
      zoomLevel = viewport.zoom
    )
  }.stateIn(
    scope = viewModelScope,
    started = SharingStarted.WhileSubscribed(5000),
    initialValue = ExploreUiState()
  )

  fun setFilter(filter: MapFilterType) {
    _activeFilter.value = filter
  }

  fun setSearchQuery(query: String) {
    _searchQuery.value = query
  }

  fun selectCat(cat: Cat?) {
    _selectedCat.value = cat
    if (cat != null) {
      _mapCenterLat.value = cat.latitude
      _mapCenterLng.value = cat.longitude
    }
  }

  fun recenterToUser() {
    _mapCenterLat.value = -6.9175
    _mapCenterLng.value = 107.6191
    _zoomLevel.value = 15f
  }

  fun zoomIn() {
    _zoomLevel.value = (_zoomLevel.value + 1f).coerceAtMost(18f)
  }

  fun zoomOut() {
    _zoomLevel.value = (_zoomLevel.value - 1f).coerceAtLeast(12f)
  }

  fun panMap(deltaX: Float, deltaY: Float) {
    val factor = 0.000025 * (16.0 / _zoomLevel.value)
    _mapCenterLng.value = _mapCenterLng.value - deltaX * factor
    _mapCenterLat.value = _mapCenterLat.value + deltaY * factor
  }
}
