package com.example.domain.model

import androidx.compose.ui.graphics.Color
import com.example.data.model.CatCondition
import com.example.data.model.CatOwnership
import com.example.data.model.CatPhysique
import com.example.data.model.CatStatus

data class Cat(
  val id: Long,
  val nickname: String,
  val photoUri: String = "",
  val avatarPresetId: String = "mascot_oyen",
  val colorPattern: String = "Oranye / Tabby",
  val description: String = "",
  val latitude: Double,
  val longitude: Double,
  val locationName: String = "Sekitar Lokasi",
  val condition: CatCondition,
  val physique: CatPhysique = CatPhysique.IDEAL,
  val ownership: CatOwnership = CatOwnership.KUCING_LIAR,
  val status: CatStatus,
  val notes: String,
  val createdAt: Long,
  val updatedAt: Long,
  val discoveryCount: Int,
  val isFavorite: Boolean,
  val reportedBy: String,
  val distanceMeters: Int = 0
)

data class Discovery(
  val id: Long,
  val catId: Long,
  val catNickname: String,
  val photoUri: String,
  val latitude: Double,
  val longitude: Double,
  val condition: CatCondition,
  val notes: String,
  val createdAt: Long,
  val userId: String,
  val xpGained: Int
)

data class UserProfile(
  val id: String,
  val displayName: String,
  val title: String,
  val avatarUri: String,
  val xp: Int,
  val level: Int,
  val catsFound: Int,
  val catsHelped: Int,
  val photosTaken: Int,
  val areasExplored: Int,
  val currentLevelMinXp: Int,
  val nextLevelXp: Int,
  val xpRemaining: Int,
  val progressFraction: Float
)

data class Achievement(
  val id: String,
  val title: String,
  val description: String,
  val icon: String,
  val xpReward: Int,
  val unlocked: Boolean,
  val unlockedAt: Long?,
  val progress: Int,
  val maxProgress: Int
)

data class DailyMission(
  val id: String,
  val title: String,
  val target: Int,
  val current: Int,
  val xpReward: Int,
  val isCompleted: Boolean
)
