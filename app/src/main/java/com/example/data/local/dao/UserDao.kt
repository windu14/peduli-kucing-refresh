package com.example.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.data.local.entity.UserEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface UserDao {
  @Query("SELECT * FROM users WHERE id = :userId LIMIT 1")
  fun getUserFlow(userId: String = "user_windu"): Flow<UserEntity?>

  @Query("SELECT * FROM users WHERE id = :userId LIMIT 1")
  suspend fun getUser(userId: String = "user_windu"): UserEntity?

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insertUser(user: UserEntity)

  @Update
  suspend fun updateUser(user: UserEntity)

  @Query("UPDATE users SET xp = xp + :xpGained, photosTaken = photosTaken + 1, catsFound = catsFound + :newCatIncrement WHERE id = :userId")
  suspend fun addXpAndStats(userId: String, xpGained: Int, newCatIncrement: Int)

  @Query("UPDATE users SET level = :newLevel, title = :newTitle WHERE id = :userId")
  suspend fun updateLevel(userId: String, newLevel: Int, newTitle: String)

  @Query("UPDATE users SET catsHelped = catsHelped + 1 WHERE id = :userId")
  suspend fun incrementCatsHelped(userId: String = "user_windu")
}
