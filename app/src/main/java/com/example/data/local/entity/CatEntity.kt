package com.example.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "cats")
data class CatEntity(
  @PrimaryKey(autoGenerate = true)
  val id: Long = 0,
  val nickname: String,
  val photoUri: String = "",
  val avatarPresetId: String = "mascot_oyen",
  val colorPattern: String = "Oranye / Tabby",
  val description: String = "",
  val latitude: Double = -6.9175,
  val longitude: Double = 107.6191,
  val locationName: String = "Sekitar Lokasi",
  val condition: String = "SEHAT",
  val physique: String = "IDEAL",
  val ownership: String = "KUCING_LIAR",
  val status: String = "NEWLY_FOUND",
  val notes: String = "",
  val createdAt: Long = System.currentTimeMillis(),
  val updatedAt: Long = System.currentTimeMillis(),
  val discoveryCount: Int = 1,
  val isFavorite: Boolean = false,
  val reportedBy: String = "Windu"
)

