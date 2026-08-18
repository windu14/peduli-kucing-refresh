package com.example.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "users")
data class UserEntity(
  @PrimaryKey
  val id: String = "user_windu",
  val displayName: String = "Windu",
  val title: String = "Penjaga Cuking",
  val avatarUri: String = "",
  val xp: Int = 1240,
  val level: Int = 4,
  val catsFound: Int = 12,
  val catsHelped: Int = 8,
  val photosTaken: Int = 18,
  val areasExplored: Int = 5
)
