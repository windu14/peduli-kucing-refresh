package com.example.ui.camera

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.model.CatCondition
import com.example.data.model.CatOwnership
import com.example.data.model.CatPhysique
import com.example.data.repository.CatRepository
import com.example.data.repository.UserRepository
import com.example.ui.components.MascotCatalog
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class DiscoveryFormUiState(
  val photoUri: String = "",
  val nickname: String = "",
  val avatarPresetId: String = "mascot_oyen",
  val colorPattern: String = "Oranye / Oyen",
  val description: String = "",
  val condition: CatCondition = CatCondition.SEHAT,
  val physique: CatPhysique = CatPhysique.IDEAL,
  val ownership: CatOwnership = CatOwnership.KUCING_LIAR,
  val notes: String = "",
  val latitude: Double = -6.9175,
  val longitude: Double = 107.6191,
  val locationName: String = "Taman Kota Bandung, Jl. Wastukancana",
  val isSubmitting: Boolean = false,
  val isSubmittedSuccess: Boolean = false,
  val xpGained: Int = 50,
  val newCatId: Long = 0
)

class DiscoveryViewModel(
  private val catRepository: CatRepository,
  private val userRepository: UserRepository
) : ViewModel() {

  private val _uiState = MutableStateFlow(DiscoveryFormUiState())
  val uiState: StateFlow<DiscoveryFormUiState> = _uiState.asStateFlow()

  fun initPhoto(uri: String) {
    _uiState.value = _uiState.value.copy(
      photoUri = uri,
      isSubmittedSuccess = false
    )
  }

  fun updateNickname(name: String) {
    _uiState.value = _uiState.value.copy(nickname = name)
  }

  fun updateAvatarPreset(presetId: String) {
    val preset = MascotCatalog.getById(presetId)
    _uiState.value = _uiState.value.copy(
      avatarPresetId = presetId,
      colorPattern = if (_uiState.value.colorPattern.isBlank() || _uiState.value.colorPattern == "Oranye / Oyen") preset.coatName else _uiState.value.colorPattern
    )
  }

  fun updateColorPattern(color: String) {
    _uiState.value = _uiState.value.copy(colorPattern = color)
  }

  fun updateDescription(desc: String) {
    _uiState.value = _uiState.value.copy(description = desc)
  }

  fun updateCondition(condition: CatCondition) {
    _uiState.value = _uiState.value.copy(condition = condition)
  }

  fun updatePhysique(physique: CatPhysique) {
    _uiState.value = _uiState.value.copy(physique = physique)
  }

  fun updateOwnership(ownership: CatOwnership) {
    _uiState.value = _uiState.value.copy(ownership = ownership)
  }

  fun updateNotes(notes: String) {
    _uiState.value = _uiState.value.copy(notes = notes)
  }

  fun updateLocation(lat: Double, lng: Double, name: String) {
    _uiState.value = _uiState.value.copy(
      latitude = lat,
      longitude = lng,
      locationName = name
    )
  }

  fun submitDiscovery(onSuccess: (Long, Int) -> Unit) {
    if (_uiState.value.isSubmitting) return

    _uiState.value = _uiState.value.copy(isSubmitting = true)

    viewModelScope.launch {
      try {
        val state = _uiState.value
        val (catId, xp) = catRepository.submitNewCatDiscovery(
          nickname = state.nickname.ifBlank { MascotCatalog.getById(state.avatarPresetId).name },
          photoUri = state.photoUri.ifBlank { "asset://cat_milo.png" },
          avatarPresetId = state.avatarPresetId,
          colorPattern = state.colorPattern,
          description = state.description,
          condition = state.condition,
          physique = state.physique,
          ownership = state.ownership,
          latitude = state.latitude,
          longitude = state.longitude,
          locationName = state.locationName,
          notes = state.notes.ifBlank { state.description },
          reportedBy = "Windu"
        )

        _uiState.value = _uiState.value.copy(
          isSubmitting = false,
          isSubmittedSuccess = true,
          xpGained = xp,
          newCatId = catId
        )

        onSuccess(catId, xp)
      } catch (e: Exception) {
        _uiState.value = _uiState.value.copy(isSubmitting = false)
      }
    }
  }

  fun reset() {
    _uiState.value = DiscoveryFormUiState()
  }
}

