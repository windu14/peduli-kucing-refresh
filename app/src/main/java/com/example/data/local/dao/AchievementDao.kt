package com.example.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.data.local.entity.AchievementEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface AchievementDao {
  @Query("SELECT * FROM achievements ORDER BY unlocked DESC, id ASC")
  fun getAllAchievements(): Flow<List<AchievementEntity>>

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insertAll(achievements: List<AchievementEntity>)

  @Update
  suspend fun update(achievement: AchievementEntity)

  @Query("UPDATE achievements SET unlocked = 1, unlockedAt = :unlockedAt, progress = maxProgress WHERE id = :id")
  suspend fun unlock(id: String, unlockedAt: Long = System.currentTimeMillis())

  @Query("SELECT COUNT(*) FROM achievements WHERE unlocked = 1")
  suspend fun getUnlockedCount(): Int
}
