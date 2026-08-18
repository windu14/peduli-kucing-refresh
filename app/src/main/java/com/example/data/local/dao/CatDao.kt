package com.example.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.data.local.entity.CatEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface CatDao {
  @Query("SELECT * FROM cats ORDER BY updatedAt DESC")
  fun getAllCats(): Flow<List<CatEntity>>

  @Query("SELECT * FROM cats WHERE id = :id")
  suspend fun getCatById(id: Long): CatEntity?

  @Query("SELECT * FROM cats WHERE id = :id")
  fun getCatByIdFlow(id: Long): Flow<CatEntity?>

  @Query("SELECT * FROM cats WHERE isFavorite = 1 ORDER BY updatedAt DESC")
  fun getFavoriteCats(): Flow<List<CatEntity>>

  @Query("SELECT * FROM cats WHERE reportedBy = :userId ORDER BY updatedAt DESC")
  fun getMyCats(userId: String): Flow<List<CatEntity>>

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insertCat(cat: CatEntity): Long

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insertAllCats(cats: List<CatEntity>)

  @Update
  suspend fun updateCat(cat: CatEntity)

  @Query("UPDATE cats SET isFavorite = :isFavorite WHERE id = :catId")
  suspend fun toggleFavorite(catId: Long, isFavorite: Boolean)

  @Query("UPDATE cats SET status = :status, updatedAt = :updatedAt, discoveryCount = discoveryCount + 1 WHERE id = :catId")
  suspend fun updateCatStatus(catId: Long, status: String, updatedAt: Long = System.currentTimeMillis())

  @Query("DELETE FROM cats WHERE id = :id")
  suspend fun deleteCatById(id: Long)

  @Query("SELECT COUNT(*) FROM cats")
  suspend fun getCount(): Int
}
