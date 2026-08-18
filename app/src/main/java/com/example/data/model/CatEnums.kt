package com.example.data.model

import androidx.compose.ui.graphics.Color
import com.example.ui.theme.StatusHandled
import com.example.ui.theme.StatusHealthy
import com.example.ui.theme.StatusInjured
import com.example.ui.theme.StatusNeedHelp
import com.example.ui.theme.StatusNew
import com.example.ui.theme.StatusUnsure

enum class CatCondition(val displayName: String, val badgeColor: Color) {
  SEHAT("Sehat", Color(0xFF2E7D32)),
  SAKIT("Sakit", Color(0xFFC62828)),
  PRIHATIN("Prihatin", Color(0xFFD32F2F)),
  NAMPAK_TIDAK_SEHAT("Nampak Tidak Sehat", Color(0xFFE65100)),
  SEDANG("Sedang", Color(0xFFF57C00));

  companion object {
    fun fromString(value: String): CatCondition {
      return try {
        valueOf(value)
      } catch (e: Exception) {
        when (value.uppercase()) {
          "HEALTHY" -> SEHAT
          "INJURED" -> SAKIT
          "NEED_ATTENTION" -> NAMPAK_TIDAK_SEHAT
          "UNSURE" -> SEDANG
          else -> SEHAT
        }
      }
    }
  }
}

enum class CatPhysique(val displayName: String, val description: String) {
  GEMUK("Gemuk", "Badan gembul, montok"),
  KURUS("Kurus", "Badan ramping/kurus"),
  IDEAL("Ideal", "Postur tubuh proporsional");

  companion object {
    fun fromString(value: String): CatPhysique {
      return try {
        valueOf(value)
      } catch (e: Exception) {
        IDEAL
      }
    }
  }
}

enum class CatOwnership(val displayName: String, val description: String) {
  ADA_PEMILIK("Ada Pemilik", "Kucing berpemilik/peliharaan"),
  KUCING_LIAR("Kucing Liar", "Kucing jalanan tanpa pemilik tetap"),
  TIDAK_JELAS("Tidak Jelas", "Belum diketahui pasti");

  companion object {
    fun fromString(value: String): CatOwnership {
      return try {
        valueOf(value)
      } catch (e: Exception) {
        KUCING_LIAR
      }
    }
  }
}

enum class CatStatus(val displayName: String, val badgeColor: Color) {
  NEWLY_FOUND("Baru Ditambahkan", StatusNew),
  SEEN_AGAIN("Terlihat Lagi", StatusNeedHelp),
  HEALTHY("Sehat", StatusHealthy),
  NEED_ATTENTION("Butuh Perhatian", StatusNeedHelp),
  INJURED("Sakit / Terluka", StatusInjured),
  HANDLED("Sudah Ditangani", StatusHandled),
  NOT_SEEN("Tidak Terlihat Lagi", StatusUnsure);

  companion object {
    fun fromString(value: String): CatStatus {
      return try {
        valueOf(value)
      } catch (e: Exception) {
        NEWLY_FOUND
      }
    }
  }
}

