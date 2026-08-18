package com.example.data.local.database

import android.content.Context
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.data.local.entity.AchievementEntity
import com.example.data.local.entity.CatEntity
import com.example.data.local.entity.DiscoveryEntity
import com.example.data.local.entity.UserEntity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

object DatabaseProvider {
  @Volatile
  private var INSTANCE: AppDatabase? = null

  fun getDatabase(context: Context): AppDatabase {
    return INSTANCE ?: synchronized(this) {
      val instance = Room.databaseBuilder(
        context.applicationContext,
        AppDatabase::class.java,
        "peduli_cuking_database"
      )
        .addCallback(DatabaseCallback())
        .fallbackToDestructiveMigration()
        .build()
      INSTANCE = instance
      instance
    }
  }

  private class DatabaseCallback : RoomDatabase.Callback() {
    override fun onCreate(db: SupportSQLiteDatabase) {
      super.onCreate(db)
      CoroutineScope(Dispatchers.IO).launch {
        try {
          INSTANCE?.let { database ->
            populateInitialSeedData(database)
          }
        } catch (e: Exception) {
          e.printStackTrace()
        }
      }
    }

    override fun onDestructiveMigration(db: SupportSQLiteDatabase) {
      super.onDestructiveMigration(db)
      CoroutineScope(Dispatchers.IO).launch {
        try {
          INSTANCE?.let { database ->
            populateInitialSeedData(database)
          }
        } catch (e: Exception) {
          e.printStackTrace()
        }
      }
    }

    override fun onOpen(db: SupportSQLiteDatabase) {
      super.onOpen(db)
      CoroutineScope(Dispatchers.IO).launch {
        try {
          INSTANCE?.let { database ->
            populateInitialSeedData(database)
          }
        } catch (e: Exception) {
          e.printStackTrace()
        }
      }
    }
  }

  suspend fun populateInitialSeedData(database: AppDatabase) {
    if (database.catDao().getCount() > 0) return

    val currentTime = System.currentTimeMillis()
    val oneHour = 3600_000L
    val oneDay = 86400_000L

    // Base coordinate around friendly urban area (e.g., Bandung / Jakarta central parks)
    val baseLat = -6.9175
    val baseLng = 107.6191

    val seedCats = listOf(
      CatEntity(
        id = 1,
        nickname = "Milo",
        photoUri = "asset://cat_milo.png",
        avatarPresetId = "mascot_oyen",
        colorPattern = "Oranye / Tabby",
        description = "Cuking oren jinak sering santai di bawah pohon taman, suka dielus kepalanya.",
        latitude = baseLat + 0.0012,
        longitude = baseLng + 0.0015,
        locationName = "Taman Kota Sejahtera",
        condition = "SEHAT",
        physique = "IDEAL",
        ownership = "KUCING_LIAR",
        status = "HEALTHY",
        notes = "Cuking oren jinak sering santai di bawah pohon taman, suka dielus kepalanya.",
        createdAt = currentTime - 2 * oneDay,
        updatedAt = currentTime - 2 * oneHour,
        discoveryCount = 4,
        isFavorite = true,
        reportedBy = "Windu"
      ),
      CatEntity(
        id = 2,
        nickname = "Oyen",
        photoUri = "asset://cat_oyen.png",
        avatarPresetId = "mascot_oyen",
        colorPattern = "Oranye Cerah",
        description = "Nongkrong depan minimarket, kelihatan haus dan butuh dry food.",
        latitude = baseLat + 0.0028,
        longitude = baseLng - 0.0018,
        locationName = "Depan Minimarket Melati",
        condition = "NAMPAK_TIDAK_SEHAT",
        physique = "KURUS",
        ownership = "KUCING_LIAR",
        status = "NEED_ATTENTION",
        notes = "Nongkrong depan minimarket, kelihatan haus dan butuh dry food.",
        createdAt = currentTime - 3 * oneDay,
        updatedAt = currentTime - 4 * oneHour,
        discoveryCount = 2,
        isFavorite = true,
        reportedBy = "Windu"
      ),
      CatEntity(
        id = 3,
        nickname = "Belang",
        photoUri = "asset://cat_belang.png",
        avatarPresetId = "mascot_calico",
        colorPattern = "Belang Tiga / Calico",
        description = "Kucing belang 3 lincah bermain di dekat perumahan warga.",
        latitude = baseLat - 0.0021,
        longitude = baseLng + 0.0025,
        locationName = "Komplek Asri Blok C",
        condition = "SEHAT",
        physique = "IDEAL",
        ownership = "ADA_PEMILIK",
        status = "NEWLY_FOUND",
        notes = "Kucing belang 3 (calico) lincah bermain di dekat area perumahan warga.",
        createdAt = currentTime - oneDay,
        updatedAt = currentTime - 6 * oneHour,
        discoveryCount = 1,
        isFavorite = false,
        reportedBy = "Rian"
      ),
      CatEntity(
        id = 4,
        nickname = "Bolu",
        photoUri = "asset://cat_bolu.png",
        avatarPresetId = "mascot_buncit",
        colorPattern = "Krem Gembul",
        description = "Badannya gembul gemas warna krem, suka tidur siang di kursi teras kafe.",
        latitude = baseLat - 0.0035,
        longitude = baseLng - 0.0022,
        locationName = "Teras Kafe Kopi Kucing",
        condition = "SEHAT",
        physique = "GEMUK",
        ownership = "ADA_PEMILIK",
        status = "HEALTHY",
        notes = "Badannya gembul gemas warna krem, suka tidur siang di kursi teras kafe.",
        createdAt = currentTime - 4 * oneDay,
        updatedAt = currentTime - 12 * oneHour,
        discoveryCount = 5,
        isFavorite = true,
        reportedBy = "Windu"
      ),
      CatEntity(
        id = 5,
        nickname = "Mochi",
        photoUri = "asset://cat_mochi.png",
        avatarPresetId = "mascot_putih",
        colorPattern = "Putih Bersih",
        description = "Sudah divaksin & disteril oleh komunitas, ada kalung penanda pink.",
        latitude = baseLat + 0.0042,
        longitude = baseLng + 0.0038,
        locationName = "Pusat Komunitas Hewan",
        condition = "SEHAT",
        physique = "IDEAL",
        ownership = "ADA_PEMILIK",
        status = "HANDLED",
        notes = "Sudah divaksin & disteril oleh komunitas pecinta kucing lokal, ada kalung penanda.",
        createdAt = currentTime - 7 * oneDay,
        updatedAt = currentTime - 1 * oneDay,
        discoveryCount = 3,
        isFavorite = false,
        reportedBy = "Sarah"
      ),
      CatEntity(
        id = 6,
        nickname = "Kuro",
        photoUri = "asset://cat_kuro.png",
        avatarPresetId = "mascot_kuro",
        colorPattern = "Hitam Pekat",
        description = "Kucing hitam pekat dengan mata kuning terang, sering berjaga dekat toko buku.",
        latitude = baseLat + 0.0019,
        longitude = baseLng - 0.0041,
        locationName = "Depan Toko Buku Pustaka",
        condition = "SEHAT",
        physique = "IDEAL",
        ownership = "KUCING_LIAR",
        status = "SEEN_AGAIN",
        notes = "Kucing hitam pekat dengan mata kuning terang, sering berjaga dekat toko buku.",
        createdAt = currentTime - 5 * oneDay,
        updatedAt = currentTime - 3 * oneHour,
        discoveryCount = 2,
        isFavorite = false,
        reportedBy = "Dimas"
      ),
      CatEntity(
        id = 7,
        nickname = "Cimot",
        photoUri = "asset://cat_cimot.png",
        avatarPresetId = "mascot_pejuang",
        colorPattern = "Abu Belang",
        description = "Kaki depan kiri pincang sedikit saat jalan, butuh bantuan relawan atau klinik.",
        latitude = baseLat - 0.0015,
        longitude = baseLng - 0.0012,
        locationName = "Gang Mawar No. 14",
        condition = "SAKIT",
        physique = "KURUS",
        ownership = "KUCING_LIAR",
        status = "INJURED",
        notes = "Kaki depan kiri pincang sedikit saat jalan, butuh bantuan relawan atau klinik.",
        createdAt = currentTime - 8 * oneHour,
        updatedAt = currentTime - 1 * oneHour,
        discoveryCount = 1,
        isFavorite = true,
        reportedBy = "Windu"
      ),
      CatEntity(
        id = 8,
        nickname = "Luna",
        photoUri = "asset://cat_luna.png",
        avatarPresetId = "mascot_tuxedo",
        colorPattern = "Tuxedo Hitam Putih",
        description = "Kucing corak rapi, sering minum di dekat air mancur taman kota.",
        latitude = baseLat + 0.0051,
        longitude = baseLng - 0.0009,
        locationName = "Air Mancur Alun-Alun",
        condition = "SEHAT",
        physique = "IDEAL",
        ownership = "TIDAK_JELAS",
        status = "HEALTHY",
        notes = "Kucing tortoiseshell cantik, sering minum di dekat air mancur taman kota.",
        createdAt = currentTime - 6 * oneDay,
        updatedAt = currentTime - 18 * oneHour,
        discoveryCount = 4,
        isFavorite = false,
        reportedBy = "Windu"
      )
    )

    database.catDao().insertAllCats(seedCats)

    val seedDiscoveries = listOf(
      DiscoveryEntity(
        id = 1,
        catId = 1,
        catNickname = "Milo",
        photoUri = "asset://cat_milo.png",
        latitude = baseLat + 0.0012,
        longitude = baseLng + 0.0015,
        condition = "HEALTHY",
        notes = "Milo terlihat tidur santai di bawah pohon beringin taman kota.",
        createdAt = currentTime - 2 * oneHour,
        userId = "user_windu",
        xpGained = 40
      ),
      DiscoveryEntity(
        id = 2,
        catId = 7,
        catNickname = "Cimot",
        photoUri = "asset://cat_cimot.png",
        latitude = baseLat - 0.0015,
        longitude = baseLng - 0.0012,
        condition = "INJURED",
        notes = "Ditemukan di dekat gang perumahan, kaki pincang butuh pertolongan segera.",
        createdAt = currentTime - 8 * oneHour,
        userId = "user_windu",
        xpGained = 45
      ),
      DiscoveryEntity(
        id = 3,
        catId = 2,
        catNickname = "Oyen",
        photoUri = "asset://cat_oyen.png",
        latitude = baseLat + 0.0028,
        longitude = baseLng - 0.0018,
        condition = "NEED_ATTENTION",
        notes = "Diberi makanan basah di depan minimarket, lahap sekali.",
        createdAt = currentTime - 1 * oneDay,
        userId = "user_windu",
        xpGained = 40
      ),
      DiscoveryEntity(
        id = 4,
        catId = 4,
        catNickname = "Bolu",
        photoUri = "asset://cat_bolu.png",
        latitude = baseLat - 0.0035,
        longitude = baseLng - 0.0022,
        condition = "HEALTHY",
        notes = "Bolu sedang dielus pengunjung kafe.",
        createdAt = currentTime - 2 * oneDay,
        userId = "user_windu",
        xpGained = 40
      ),
      DiscoveryEntity(
        id = 5,
        catId = 6,
        catNickname = "Kuro",
        photoUri = "asset://cat_kuro.png",
        latitude = baseLat + 0.0019,
        longitude = baseLng - 0.0041,
        condition = "HEALTHY",
        notes = "Kuro patroli malam hari di depan ruko buku.",
        createdAt = currentTime - 3 * oneDay,
        userId = "user_windu",
        xpGained = 40
      )
    )

    database.discoveryDao().insertAll(seedDiscoveries)

    val seedUser = UserEntity(
      id = "user_windu",
      displayName = "Windu",
      title = "Penjaga Cuking",
      avatarUri = "",
      xp = 1240,
      level = 4,
      catsFound = 12,
      catsHelped = 8,
      photosTaken = 18,
      areasExplored = 5
    )

    database.userDao().insertUser(seedUser)

    val seedAchievements = listOf(
      AchievementEntity(
        id = "ach_first_find",
        title = "First Find",
        description = "Menemukan dan mendokumentasikan cuking pertama kamu",
        icon = "paw",
        xpReward = 50,
        unlocked = true,
        unlockedAt = currentTime - 10 * oneDay,
        progress = 1,
        maxProgress = 1
      ),
      AchievementEntity(
        id = "ach_spotter",
        title = "Cuking Spotter",
        description = "Menemukan total 10 cuking di berbagai tempat",
        icon = "camera",
        xpReward = 100,
        unlocked = true,
        unlockedAt = currentTime - 2 * oneDay,
        progress = 10,
        maxProgress = 10
      ),
      AchievementEntity(
        id = "ach_explorer",
        title = "Explorer",
        description = "Menemukan cuking di 5 area lingkungan berbeda",
        icon = "map",
        xpReward = 100,
        unlocked = true,
        unlockedAt = currentTime - 1 * oneDay,
        progress = 5,
        maxProgress = 5
      ),
      AchievementEntity(
        id = "ach_good_human",
        title = "Good Human",
        description = "Membantu dan memberi update status 10 cuking",
        icon = "heart",
        xpReward = 150,
        unlocked = false,
        unlockedAt = null,
        progress = 8,
        maxProgress = 10
      ),
      AchievementEntity(
        id = "ach_night_watch",
        title = "Night Watch",
        description = "Mendokumentasikan cuking yang aktif pada malam hari",
        icon = "moon",
        xpReward = 75,
        unlocked = true,
        unlockedAt = currentTime - 3 * oneDay,
        progress = 1,
        maxProgress = 1
      ),
      AchievementEntity(
        id = "ach_guardian",
        title = "Guardian Angel",
        description = "Membantu penanganan cuking yang terluka atau butuh bantuan",
        icon = "star",
        xpReward = 200,
        unlocked = false,
        unlockedAt = null,
        progress = 1,
        maxProgress = 3
      )
    )

    database.achievementDao().insertAll(seedAchievements)
  }
}
