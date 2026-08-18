package com.example.data.repository

import com.example.data.local.dao.CatDao
import com.example.data.local.dao.DiscoveryDao
import com.example.data.local.entity.CatEntity
import com.example.data.local.entity.DiscoveryEntity
import com.example.data.model.CatCondition
import com.example.data.model.CatOwnership
import com.example.data.model.CatPhysique
import com.example.data.model.CatStatus
import com.example.domain.model.Cat
import com.example.domain.model.Discovery
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.sqrt

interface ICatRepository {
  fun getAllCats(): Flow<List<Cat>>
  fun getCatById(id: Long): Flow<Cat?>
  fun getFavoriteCats(): Flow<List<Cat>>
  fun getMyCats(userId: String): Flow<List<Cat>>
  fun getDiscoveriesForCat(catId: Long): Flow<List<Discovery>>
  fun getRecentDiscoveries(): Flow<List<Discovery>>
  suspend fun submitNewCatDiscovery(
    nickname: String,
    photoUri: String,
    avatarPresetId: String,
    colorPattern: String,
    description: String,
    condition: CatCondition,
    physique: CatPhysique,
    ownership: CatOwnership,
    latitude: Double,
    longitude: Double,
    locationName: String,
    notes: String,
    reportedBy: String
  ): Pair<Long, Int>
  suspend fun toggleFavorite(catId: Long, isFavorite: Boolean)
  suspend fun updateCatStatus(catId: Long, newStatus: CatStatus, notes: String): Int
  suspend fun deleteCat(catId: Long)
}

class CatRepository(
  private val catDao: CatDao,
  private val discoveryDao: DiscoveryDao,
  private val userRepository: UserRepository
) : ICatRepository {

  // Current mock center point
  private val userLat = -6.9175
  private val userLng = 107.6191

  override fun getAllCats(): Flow<List<Cat>> {
    return catDao.getAllCats().map { list ->
      list.map { it.toDomain(userLat, userLng) }
    }
  }

  override fun getCatById(id: Long): Flow<Cat?> {
    return catDao.getCatByIdFlow(id).map { it?.toDomain(userLat, userLng) }
  }

  override fun getFavoriteCats(): Flow<List<Cat>> {
    return catDao.getFavoriteCats().map { list ->
      list.map { it.toDomain(userLat, userLng) }
    }
  }

  override fun getMyCats(userId: String): Flow<List<Cat>> {
    return catDao.getMyCats(userId).map { list ->
      list.map { it.toDomain(userLat, userLng) }
    }
  }

  override fun getDiscoveriesForCat(catId: Long): Flow<List<Discovery>> {
    return discoveryDao.getDiscoveriesForCat(catId).map { list ->
      list.map { it.toDomain() }
    }
  }

  override fun getRecentDiscoveries(): Flow<List<Discovery>> {
    return discoveryDao.getAllDiscoveries().map { list ->
      list.take(10).map { it.toDomain() }
    }
  }

  override suspend fun submitNewCatDiscovery(
    nickname: String,
    photoUri: String,
    avatarPresetId: String,
    colorPattern: String,
    description: String,
    condition: CatCondition,
    physique: CatPhysique,
    ownership: CatOwnership,
    latitude: Double,
    longitude: Double,
    locationName: String,
    notes: String,
    reportedBy: String
  ): Pair<Long, Int> {
    val cleanNickname = if (nickname.isBlank()) "Cuking Baru" else nickname.trim()
    val currentTime = System.currentTimeMillis()

    val catEntity = CatEntity(
      nickname = cleanNickname,
      photoUri = photoUri,
      avatarPresetId = avatarPresetId,
      colorPattern = colorPattern.ifBlank { "Oranye / Tabby" },
      description = description,
      latitude = latitude,
      longitude = longitude,
      locationName = locationName.ifBlank { "Sekitar Lokasi" },
      condition = condition.name,
      physique = physique.name,
      ownership = ownership.name,
      status = when (condition) {
        CatCondition.SEHAT -> CatStatus.HEALTHY.name
        CatCondition.NAMPAK_TIDAK_SEHAT, CatCondition.SEDANG -> CatStatus.NEED_ATTENTION.name
        CatCondition.SAKIT, CatCondition.PRIHATIN -> CatStatus.INJURED.name
      },
      notes = notes.ifBlank { description },
      createdAt = currentTime,
      updatedAt = currentTime,
      discoveryCount = 1,
      isFavorite = false,
      reportedBy = reportedBy
    )

    val catId = catDao.insertCat(catEntity)

    // Calculate XP: 20 base photo + 10 location + 10 condition detail + 10 avatar = +50 XP
    val xpGained = 50

    val discoveryEntity = DiscoveryEntity(
      catId = catId,
      catNickname = cleanNickname,
      photoUri = photoUri,
      latitude = latitude,
      longitude = longitude,
      condition = condition.name,
      notes = notes.ifBlank { "Ditambahkan ke peta Peduli Cuking ($colorPattern)" },
      createdAt = currentTime,
      userId = "user_windu",
      xpGained = xpGained
    )
    discoveryDao.insertDiscovery(discoveryEntity)

    // Reward XP & update user stats
    userRepository.awardXp(xpGained, isNewCat = true)

    return Pair(catId, xpGained)
  }

  override suspend fun toggleFavorite(catId: Long, isFavorite: Boolean) {
    catDao.toggleFavorite(catId, isFavorite)
  }

  override suspend fun updateCatStatus(catId: Long, newStatus: CatStatus, notes: String): Int {
    val cat = catDao.getCatById(catId) ?: return 0
    catDao.updateCatStatus(catId, newStatus.name)
    val xpReward = 25

    val discoveryEntity = DiscoveryEntity(
      catId = catId,
      catNickname = cat.nickname,
      photoUri = cat.photoUri,
      latitude = cat.latitude,
      longitude = cat.longitude,
      condition = when (newStatus) {
        CatStatus.HEALTHY, CatStatus.HANDLED -> CatCondition.SEHAT.name
        CatStatus.NEED_ATTENTION, CatStatus.SEEN_AGAIN -> CatCondition.NAMPAK_TIDAK_SEHAT.name
        CatStatus.INJURED -> CatCondition.SAKIT.name
        else -> CatCondition.SEDANG.name
      },
      notes = notes.ifBlank { "Update status: ${newStatus.displayName}" },
      createdAt = System.currentTimeMillis(),
      userId = "user_windu",
      xpGained = xpReward
    )
    discoveryDao.insertDiscovery(discoveryEntity)

    userRepository.awardXp(xpReward, isNewCat = false)
    userRepository.incrementCatsHelped()

    return xpReward
  }

  override suspend fun deleteCat(catId: Long) {
    catDao.deleteCatById(catId)
  }

  private fun CatEntity.toDomain(centerLat: Double, centerLng: Double): Cat {
    val distance = calculateDistanceInMeters(centerLat, centerLng, latitude, longitude)
    return Cat(
      id = id,
      nickname = nickname,
      photoUri = photoUri,
      avatarPresetId = avatarPresetId,
      colorPattern = colorPattern,
      description = description,
      latitude = latitude,
      longitude = longitude,
      locationName = locationName,
      condition = CatCondition.fromString(condition),
      physique = CatPhysique.fromString(physique),
      ownership = CatOwnership.fromString(ownership),
      status = CatStatus.fromString(status),
      notes = notes,
      createdAt = createdAt,
      updatedAt = updatedAt,
      discoveryCount = discoveryCount,
      isFavorite = isFavorite,
      reportedBy = reportedBy,
      distanceMeters = distance
    )
  }

  private fun DiscoveryEntity.toDomain(): Discovery {
    return Discovery(
      id = id,
      catId = catId,
      catNickname = catNickname,
      photoUri = photoUri,
      latitude = latitude,
      longitude = longitude,
      condition = CatCondition.fromString(condition),
      notes = notes,
      createdAt = createdAt,
      userId = userId,
      xpGained = xpGained
    )
  }

  private fun calculateDistanceInMeters(
    lat1: Double,
    lon1: Double,
    lat2: Double,
    lon2: Double
  ): Int {
    val r = 6371000.0 // Earth radius in meters
    val dLat = Math.toRadians(lat2 - lat1)
    val dLon = Math.toRadians(lon2 - lon1)
    val a = sin(dLat / 2) * sin(dLat / 2) +
      cos(Math.toRadians(lat1)) * cos(Math.toRadians(lat2)) *
      sin(dLon / 2) * sin(dLon / 2)
    val c = 2 * atan2(sqrt(a), sqrt(1 - a))
    return (r * c).toInt()
  }
}

