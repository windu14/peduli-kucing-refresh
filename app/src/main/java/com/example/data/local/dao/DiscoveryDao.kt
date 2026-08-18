package com.example.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.data.local.entity.DiscoveryEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface DiscoveryDao {
  @Query("SELECT * FROM discoveries ORDER BY createdAt DESC")
  fun getAllDiscoveries(): Flow<List<DiscoveryEntity>>

  @Query("SELECT * FROM discoveries WHERE catId = :catId ORDER BY createdAt DESC")
  fun getDiscoveriesForCat(catId: Long): Flow<List<DiscoveryEntity>>

  @Query("SELECT * FROM discoveries WHERE catId = :catId ORDER BY createdAt DESC")
  suspend fun getDiscoveriesForCatList(catId: Long): List<DiscoveryEntity>

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insertDiscovery(discovery: DiscoveryEntity): Long

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insertAll(discoveries: List<DiscoveryEntity>)

  @Query("SELECT COUNT(*) FROM discoveries")
  suspend fun getCount(): Int
}
