package com.example.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "discoveries")
data class DiscoveryEntity(
  @PrimaryKey(autoGenerate = true)
  val id: Long = 0,
  val catId: Long,
  val catNickname: String,
  val photoUri: String,
  val latitude: Double,
  val longitude: Double,
  val condition: String,
  val notes: String,
  val createdAt: Long = System.currentTimeMillis(),
  val userId: String = "user_windu",
  val xpGained: Int = 40
)
