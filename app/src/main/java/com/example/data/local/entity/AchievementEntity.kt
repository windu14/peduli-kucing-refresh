package com.example.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "achievements")
data class AchievementEntity(
  @PrimaryKey
  val id: String,
  val title: String,
  val description: String,
  val icon: String,
  val xpReward: Int = 50,
  val unlocked: Boolean = false,
  val unlockedAt: Long? = null,
  val progress: Int = 0,
  val maxProgress: Int = 1
)
