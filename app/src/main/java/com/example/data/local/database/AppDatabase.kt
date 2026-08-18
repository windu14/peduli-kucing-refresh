package com.example.data.local.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.data.local.dao.AchievementDao
import com.example.data.local.dao.CatDao
import com.example.data.local.dao.DiscoveryDao
import com.example.data.local.dao.UserDao
import com.example.data.local.entity.AchievementEntity
import com.example.data.local.entity.CatEntity
import com.example.data.local.entity.DiscoveryEntity
import com.example.data.local.entity.UserEntity

@Database(
  entities = [
    CatEntity::class,
    DiscoveryEntity::class,
    UserEntity::class,
    AchievementEntity::class
  ],
  version = 2,
  exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
  abstract fun catDao(): CatDao
  abstract fun discoveryDao(): DiscoveryDao
  abstract fun userDao(): UserDao
  abstract fun achievementDao(): AchievementDao
}
