package com.example.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.PeachSecondary
import com.example.ui.theme.SagePrimary

data class MascotPreset(
  val id: String,
  val name: String,
  val coatName: String,
  val primaryColor: Color,
  val secondaryColor: Color,
  val eyeColor: Color,
  val earColor: Color,
  val tag: String
)

object MascotCatalog {
  val presets = listOf(
    MascotPreset(
      id = "mascot_oyen",
      name = "Si Oyen",
      coatName = "Oranye / Tabby",
      primaryColor = Color(0xFFFF9E43),
      secondaryColor = Color(0xFFFFF3E0),
      eyeColor = Color(0xFF2E7D32),
      earColor = Color(0xFFFFB74D),
      tag = "Populer"
    ),
    MascotPreset(
      id = "mascot_calico",
      name = "Si Belang Tiga",
      coatName = "Calico / Kembang",
      primaryColor = Color(0xFFFFF8E1),
      secondaryColor = Color(0xFFE65100),
      eyeColor = Color(0xFF1565C0),
      earColor = Color(0xFF37474F),
      tag = "Unik"
    ),
    MascotPreset(
      id = "mascot_tuxedo",
      name = "Si Tuxedo",
      coatName = "Hitam Putih",
      primaryColor = Color(0xFF263238),
      secondaryColor = Color(0xFFFFFFFF),
      eyeColor = Color(0xFFFBC02D),
      earColor = Color(0xFF37474F),
      tag = "Elegan"
    ),
    MascotPreset(
      id = "mascot_kuro",
      name = "Si Kuro",
      coatName = "Hitam Pekat",
      primaryColor = Color(0xFF1A1A1A),
      secondaryColor = Color(0xFF2E2E2E),
      eyeColor = Color(0xFFFFD54F),
      earColor = Color(0xFF424242),
      tag = "Misterius"
    ),
    MascotPreset(
      id = "mascot_putih",
      name = "Si Snowy",
      coatName = "Putih Bersih",
      primaryColor = Color(0xFFFFFFFF),
      secondaryColor = Color(0xFFFFEBEE),
      eyeColor = Color(0xFF42A5F5),
      earColor = Color(0xFFFFCDD2),
      tag = "Manis"
    ),
    MascotPreset(
      id = "mascot_abu",
      name = "Si Miko",
      coatName = "Abu-abu Polos",
      primaryColor = Color(0xFF90A4AE),
      secondaryColor = Color(0xFFECEFF1),
      eyeColor = Color(0xFF66BB6A),
      earColor = Color(0xFFB0BEC5),
      tag = "Kalem"
    ),
    MascotPreset(
      id = "mascot_buncit",
      name = "Si Gembul",
      coatName = "Krem Tembem",
      primaryColor = Color(0xFFFFCC80),
      secondaryColor = Color(0xFFFFF8E1),
      eyeColor = Color(0xFF4E342E),
      earColor = Color(0xFFFFE0B2),
      tag = "Lucu"
    ),
    MascotPreset(
      id = "mascot_pejuang",
      name = "Si Pejuang",
      coatName = "Belang Selamat",
      primaryColor = Color(0xFF8D6E63),
      secondaryColor = Color(0xFFD7CCC8),
      eyeColor = Color(0xFFFFA000),
      earColor = Color(0xFFA1887F),
      tag = "Tangguh"
    )
  )

  fun getById(id: String): MascotPreset {
    return presets.find { it.id.equals(id, ignoreCase = true) } ?: presets[0]
  }
}

@Composable
fun MascotAvatarView(
  mascotId: String,
  modifier: Modifier = Modifier,
  size: Dp = 64.dp
) {
  val preset = MascotCatalog.getById(mascotId)

  Box(
    modifier = modifier
      .size(size)
      .clip(CircleShape)
      .background(preset.primaryColor.copy(alpha = 0.2f)),
    contentAlignment = Alignment.Center
  ) {
    Canvas(modifier = Modifier.fillMaxSize()) {
      val w = this.size.width
      val h = this.size.height

      // Left Ear
      val leftEar = Path().apply {
        moveTo(w * 0.18f, h * 0.40f)
        lineTo(w * 0.12f, h * 0.10f)
        lineTo(w * 0.42f, h * 0.24f)
        close()
      }
      drawPath(leftEar, color = preset.primaryColor)
      val leftInnerEar = Path().apply {
        moveTo(w * 0.22f, h * 0.36f)
        lineTo(w * 0.18f, h * 0.18f)
        lineTo(w * 0.38f, h * 0.27f)
        close()
      }
      drawPath(leftInnerEar, color = preset.earColor)

      // Right Ear
      val rightEar = Path().apply {
        moveTo(w * 0.82f, h * 0.40f)
        lineTo(w * 0.88f, h * 0.10f)
        lineTo(w * 0.58f, h * 0.24f)
        close()
      }
      drawPath(rightEar, color = if (preset.id == "mascot_calico") preset.earColor else preset.primaryColor)
      val rightInnerEar = Path().apply {
        moveTo(w * 0.78f, h * 0.36f)
        lineTo(w * 0.82f, h * 0.18f)
        lineTo(w * 0.62f, h * 0.27f)
        close()
      }
      drawPath(rightInnerEar, color = Color(0xFFFFCDD2))

      // Head Main Circle
      drawCircle(
        color = preset.primaryColor,
        radius = w * 0.38f,
        center = Offset(w * 0.50f, h * 0.56f)
      )

      // Secondary patch (e.g., muzzle/belly/calico spot)
      if (preset.id == "mascot_calico") {
        drawCircle(
          color = preset.secondaryColor,
          radius = w * 0.20f,
          center = Offset(w * 0.35f, h * 0.48f)
        )
      } else if (preset.id == "mascot_tuxedo" || preset.id == "mascot_oyen") {
        drawCircle(
          color = preset.secondaryColor,
          radius = w * 0.22f,
          center = Offset(w * 0.50f, h * 0.64f)
        )
      }

      // Eyes
      val eyeRadius = w * 0.065f
      val eyeY = h * 0.52f
      drawCircle(
        color = preset.eyeColor,
        radius = eyeRadius,
        center = Offset(w * 0.35f, eyeY)
      )
      drawCircle(
        color = preset.eyeColor,
        radius = eyeRadius,
        center = Offset(w * 0.65f, eyeY)
      )

      // Eye pupil shine
      drawCircle(
        color = Color.White,
        radius = eyeRadius * 0.45f,
        center = Offset(w * 0.37f, eyeY - eyeRadius * 0.3f)
      )
      drawCircle(
        color = Color.White,
        radius = eyeRadius * 0.45f,
        center = Offset(w * 0.67f, eyeY - eyeRadius * 0.3f)
      )

      // Pink Nose
      val nose = Path().apply {
        moveTo(w * 0.47f, h * 0.60f)
        lineTo(w * 0.53f, h * 0.60f)
        lineTo(w * 0.50f, h * 0.64f)
        close()
      }
      drawPath(nose, color = Color(0xFFFF80AB))

      // Smile mouth
      drawLine(
        color = Color(0xFF37474F),
        start = Offset(w * 0.50f, h * 0.64f),
        end = Offset(w * 0.50f, h * 0.67f),
        strokeWidth = w * 0.03f,
        cap = StrokeCap.Round
      )
      drawLine(
        color = Color(0xFF37474F),
        start = Offset(w * 0.50f, h * 0.67f),
        end = Offset(w * 0.44f, h * 0.70f),
        strokeWidth = w * 0.03f,
        cap = StrokeCap.Round
      )
      drawLine(
        color = Color(0xFF37474F),
        start = Offset(w * 0.50f, h * 0.67f),
        end = Offset(w * 0.56f, h * 0.70f),
        strokeWidth = w * 0.03f,
        cap = StrokeCap.Round
      )

      // Whiskers
      val whiskerColor = if (preset.primaryColor == Color(0xFF1A1A1A) || preset.primaryColor == Color(0xFF263238)) Color.White else Color(0xFF455A64)
      val whiskerWidth = w * 0.025f
      // Left whiskers
      drawLine(whiskerColor, Offset(w * 0.28f, h * 0.62f), Offset(w * 0.08f, h * 0.60f), whiskerWidth, StrokeCap.Round)
      drawLine(whiskerColor, Offset(w * 0.28f, h * 0.66f), Offset(w * 0.08f, h * 0.68f), whiskerWidth, StrokeCap.Round)
      // Right whiskers
      drawLine(whiskerColor, Offset(w * 0.72f, h * 0.62f), Offset(w * 0.92f, h * 0.60f), whiskerWidth, StrokeCap.Round)
      drawLine(whiskerColor, Offset(w * 0.72f, h * 0.66f), Offset(w * 0.92f, h * 0.68f), whiskerWidth, StrokeCap.Round)

      // Blush cheeks
      drawCircle(
        color = Color(0x66FF8A80),
        radius = w * 0.06f,
        center = Offset(w * 0.24f, h * 0.60f)
      )
      drawCircle(
        color = Color(0x66FF8A80),
        radius = w * 0.06f,
        center = Offset(w * 0.76f, h * 0.60f)
      )
    }
  }
}

@Composable
fun MascotAvatarSelector(
  selectedId: String,
  onSelect: (MascotPreset) -> Unit,
  modifier: Modifier = Modifier
) {
  Column(modifier = modifier) {
    Text(
      text = "Pilih Avatar Maskot Ilustrasi",
      style = MaterialTheme.typography.titleMedium,
      fontWeight = FontWeight.Bold,
      color = MaterialTheme.colorScheme.onSurface
    )
    Text(
      text = "Pilih karakter maskot yang paling mirip atau disukai untuk tampil di peta & kartu cuking",
      style = MaterialTheme.typography.bodySmall,
      color = MaterialTheme.colorScheme.onSurfaceVariant
    )

    LazyRow(
      contentPadding = PaddingValues(top = 12.dp, bottom = 4.dp),
      horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
      items(MascotCatalog.presets, key = { it.id }) { preset ->
        val isSelected = preset.id == selectedId
        Surface(
          onClick = { onSelect(preset) },
          shape = RoundedCornerShape(18.dp),
          color = if (isSelected) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surface,
          border = if (isSelected) BorderStroke(2.dp, MaterialTheme.colorScheme.primary) else BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f)),
          shadowElevation = if (isSelected) 3.dp else 0.dp
        ) {
          Column(
            modifier = Modifier
              .padding(horizontal = 12.dp, vertical = 10.dp),
            horizontalAlignment = Alignment.CenterHorizontally
          ) {
            MascotAvatarView(
              mascotId = preset.id,
              size = 54.dp
            )
            Spacer(modifier = Modifier.size(6.dp))
            Text(
              text = preset.name,
              style = MaterialTheme.typography.labelMedium,
              fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
              color = if (isSelected) MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.onSurface
            )
            Text(
              text = preset.coatName,
              style = MaterialTheme.typography.labelSmall,
              fontSize = 10.sp,
              color = MaterialTheme.colorScheme.onSurfaceVariant
            )
          }
        }
      }
    }
  }
}
