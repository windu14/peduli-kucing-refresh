package com.example.data.repository

import com.example.data.local.dao.AchievementDao
import com.example.data.local.dao.UserDao
import com.example.data.local.entity.AchievementEntity
import com.example.data.local.entity.UserEntity
import com.example.domain.model.Achievement
import com.example.domain.model.DailyMission
import com.example.domain.model.UserProfile
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class UserRepository(
  private val userDao: UserDao,
  private val achievementDao: AchievementDao
) {

  val userProfileFlow: Flow<UserProfile> = userDao.getUserFlow().map { entity ->
    val user = entity ?: UserEntity()
    mapToUserProfile(user)
  }

  val achievementsFlow: Flow<List<Achievement>> = achievementDao.getAllAchievements().map { list ->
    list.map { it.toDomain() }
  }

  val dailyMission: DailyMission = DailyMission(
    id = "daily_1",
    title = "Temukan 2 cuking hari ini",
    target = 2,
    current = 1,
    xpReward = 50,
    isCompleted = false
  )

  suspend fun awardXp(xpGained: Int, isNewCat: Boolean) {
    val currentUser = userDao.getUser("user_windu") ?: UserEntity()
    val newTotalXp = currentUser.xp + xpGained
    val calculatedLevel = calculateLevel(newTotalXp)
    val calculatedTitle = getTitleForLevel(calculatedLevel)

    userDao.addXpAndStats(
      userId = "user_windu",
      xpGained = xpGained,
      newCatIncrement = if (isNewCat) 1 else 0
    )

    if (calculatedLevel != currentUser.level || calculatedTitle != currentUser.title) {
      userDao.updateLevel("user_windu", calculatedLevel, calculatedTitle)
    }

    // Check achievements progress
    if (isNewCat) {
      val totalFound = currentUser.catsFound + 1
      if (totalFound >= 1) achievementDao.unlock("ach_first_find")
      if (totalFound >= 10) achievementDao.unlock("ach_spotter")
    }
  }

  suspend fun incrementCatsHelped() {
    userDao.incrementCatsHelped("user_windu")
    val user = userDao.getUser("user_windu")
    if ((user?.catsHelped ?: 0) >= 10) {
      achievementDao.unlock("ach_good_human")
    }
  }

  private fun calculateLevel(xp: Int): Int {
    return when {
      xp < 300 -> 1
      xp < 700 -> 2
      xp < 1200 -> 3
      xp < 1800 -> 4
      xp < 2500 -> 5
      else -> 6
    }
  }

  private fun getTitleForLevel(level: Int): String {
    return when (level) {
      1 -> "Pemula"
      2 -> "Teman Cuking"
      3 -> "Penjelajah"
      4 -> "Penjaga Cuking"
      5 -> "Sahabat Cuking"
      else -> "Pelindung Sejati"
    }
  }

  private fun getLevelThresholds(level: Int): Pair<Int, Int> {
    return when (level) {
      1 -> Pair(0, 300)
      2 -> Pair(300, 700)
      3 -> Pair(700, 1200)
      4 -> Pair(1200, 1800)
      5 -> Pair(1800, 2500)
      else -> Pair(2500, 3500)
    }
  }

  private fun mapToUserProfile(user: UserEntity): UserProfile {
    val (minXp, maxXp) = getLevelThresholds(user.level)
    val span = (maxXp - minXp).coerceAtLeast(1)
    val currentProgress = (user.xp - minXp).coerceAtLeast(0)
    val fraction = (currentProgress.toFloat() / span.toFloat()).coerceIn(0f, 1f)
    val remaining = (maxXp - user.xp).coerceAtLeast(0)

    return UserProfile(
      id = user.id,
      displayName = user.displayName,
      title = user.title,
      avatarUri = user.avatarUri,
      xp = user.xp,
      level = user.level,
      catsFound = user.catsFound,
      catsHelped = user.catsHelped,
      photosTaken = user.photosTaken,
      areasExplored = user.areasExplored,
      currentLevelMinXp = minXp,
      nextLevelXp = maxXp,
      xpRemaining = remaining,
      progressFraction = fraction
    )
  }

  private fun AchievementEntity.toDomain(): Achievement {
    return Achievement(
      id = id,
      title = title,
      description = description,
      icon = icon,
      xpReward = xpReward,
      unlocked = unlocked,
      unlockedAt = unlockedAt,
      progress = progress,
      maxProgress = maxProgress
    )
  }
}
