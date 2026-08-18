package com.example.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Fill
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import coil.compose.rememberAsyncImagePainter
import coil.request.ImageRequest
import java.io.File

@Composable
fun CatAvatarImage(
  nickname: String,
  photoUri: String,
  modifier: Modifier = Modifier,
  avatarPresetId: String = "",
  shapeRadiusDp: Int = 20
) {
  Box(
    modifier = modifier
      .clip(RoundedCornerShape(shapeRadiusDp.dp))
  ) {
    val isRealImageFile = androidx.compose.runtime.remember(photoUri) {
      if (photoUri.startsWith("file://") || photoUri.startsWith("content://") || (photoUri.startsWith("/") && !photoUri.startsWith("asset://"))) {
        val cleanPath = photoUri.removePrefix("file://")
        val file = File(cleanPath)
        file.exists() && file.length() > 0L
      } else {
        false
      }
    }

    if (isRealImageFile) {
      val cleanPath = photoUri.removePrefix("file://")
      val file = File(cleanPath)
      val context = LocalContext.current
      Image(
        painter = rememberAsyncImagePainter(
          ImageRequest.Builder(context)
            .data(file)
            .crossfade(true)
            .allowHardware(true)
            .build()
        ),
        contentDescription = "Foto $nickname",
        modifier = Modifier.fillMaxSize(),
        contentScale = ContentScale.Crop
      )
    } else if (avatarPresetId.isNotBlank()) {
      MascotAvatarView(
        mascotId = avatarPresetId,
        modifier = Modifier.fillMaxSize()
      )
    } else {
      // Procedural Vector Cat Character based on nickname & preset
      ProceduralCatCharacter(
        nickname = nickname,
        modifier = Modifier.fillMaxSize()
      )
    }
  }
}

@Composable
fun ProceduralCatCharacter(
  nickname: String,
  modifier: Modifier = Modifier
) {
  val catTheme = getCatArtTheme(nickname)

  Canvas(
    modifier = modifier
      .fillMaxSize()
      .background(catTheme.bg)
  ) {
    val w = size.width
    val h = size.height

    // Draw background texture/soft sun circle
    drawCircle(
      color = catTheme.sunCircle,
      radius = w * 0.42f,
      center = Offset(w * 0.5f, h * 0.52f)
    )

    // Body curve
    val bodyPath = Path().apply {
      moveTo(w * 0.2f, h)
      cubicTo(w * 0.2f, h * 0.7f, w * 0.35f, h * 0.6f, w * 0.5f, h * 0.6f)
      cubicTo(w * 0.65f, h * 0.6f, w * 0.8f, h * 0.7f, w * 0.8f, h)
      close()
    }
    drawPath(bodyPath, color = catTheme.fur)

    // Left Ear
    val leftEar = Path().apply {
      moveTo(w * 0.28f, h * 0.48f)
      lineTo(w * 0.22f, h * 0.22f)
      lineTo(w * 0.44f, h * 0.36f)
      close()
    }
    drawPath(leftEar, color = catTheme.fur)
    val leftInnerEar = Path().apply {
      moveTo(w * 0.30f, h * 0.44f)
      lineTo(w * 0.26f, h * 0.27f)
      lineTo(w * 0.40f, h * 0.37f)
      close()
    }
    drawPath(leftInnerEar, color = catTheme.innerEar)

    // Right Ear
    val rightEar = Path().apply {
      moveTo(w * 0.72f, h * 0.48f)
      lineTo(w * 0.78f, h * 0.22f)
      lineTo(w * 0.56f, h * 0.36f)
      close()
    }
    drawPath(rightEar, color = catTheme.fur)
    val rightInnerEar = Path().apply {
      moveTo(w * 0.70f, h * 0.44f)
      lineTo(w * 0.74f, h * 0.27f)
      lineTo(w * 0.60f, h * 0.37f)
      close()
    }
    drawPath(rightInnerEar, color = catTheme.innerEar)

    // Head
    drawCircle(
      color = catTheme.fur,
      radius = w * 0.28f,
      center = Offset(w * 0.5f, h * 0.50f)
    )

    // Patterns (patches / stripes)
    if (catTheme.patchColor != null) {
      val patch = Path().apply {
        moveTo(w * 0.58f, h * 0.32f)
        cubicTo(w * 0.72f, h * 0.34f, w * 0.75f, h * 0.52f, w * 0.68f, h * 0.60f)
        cubicTo(w * 0.60f, h * 0.55f, w * 0.55f, h * 0.42f, w * 0.58f, h * 0.32f)
        close()
      }
      drawPath(patch, color = catTheme.patchColor)
    }

    // Eyes
    val eyeY = h * 0.49f
    drawCircle(
      color = catTheme.eyeColor,
      radius = w * 0.042f,
      center = Offset(w * 0.40f, eyeY)
    )
    drawCircle(
      color = Color.White,
      radius = w * 0.015f,
      center = Offset(w * 0.39f, eyeY - h * 0.012f)
    )

    drawCircle(
      color = catTheme.eyeColor,
      radius = w * 0.042f,
      center = Offset(w * 0.60f, eyeY)
    )
    drawCircle(
      color = Color.White,
      radius = w * 0.015f,
      center = Offset(w * 0.59f, eyeY - h * 0.012f)
    )

    // Nose
    val nosePath = Path().apply {
      moveTo(w * 0.50f, h * 0.54f)
      lineTo(w * 0.47f, h * 0.52f)
      lineTo(w * 0.53f, h * 0.52f)
      close()
    }
    drawPath(nosePath, color = catTheme.nose)

    // Mouth & Whiskers
    val mouthY = h * 0.56f
    drawLine(
      color = catTheme.whiskerColor,
      start = Offset(w * 0.50f, h * 0.54f),
      end = Offset(w * 0.50f, mouthY),
      strokeWidth = w * 0.015f
    )
    // Whiskers Left
    drawLine(
      color = catTheme.whiskerColor,
      start = Offset(w * 0.34f, h * 0.53f),
      end = Offset(w * 0.16f, h * 0.51f),
      strokeWidth = w * 0.012f
    )
    drawLine(
      color = catTheme.whiskerColor,
      start = Offset(w * 0.34f, h * 0.56f),
      end = Offset(w * 0.18f, h * 0.58f),
      strokeWidth = w * 0.012f
    )
    // Whiskers Right
    drawLine(
      color = catTheme.whiskerColor,
      start = Offset(w * 0.66f, h * 0.53f),
      end = Offset(w * 0.84f, h * 0.51f),
      strokeWidth = w * 0.012f
    )
    drawLine(
      color = catTheme.whiskerColor,
      start = Offset(w * 0.66f, h * 0.56f),
      end = Offset(w * 0.82f, h * 0.58f),
      strokeWidth = w * 0.012f
    )

    // Cute Cheeks
    drawCircle(
      color = Color(0x33F4A28C),
      radius = w * 0.05f,
      center = Offset(w * 0.33f, h * 0.54f)
    )
    drawCircle(
      color = Color(0x33F4A28C),
      radius = w * 0.05f,
      center = Offset(w * 0.67f, h * 0.54f)
    )
  }
}

data class CatArtTheme(
  val bg: Color,
  val sunCircle: Color,
  val fur: Color,
  val innerEar: Color,
  val patchColor: Color?,
  val eyeColor: Color,
  val nose: Color,
  val whiskerColor: Color
)

private fun getCatArtTheme(nickname: String): CatArtTheme {
  return when (nickname.lowercase()) {
    "milo" -> CatArtTheme(
      bg = Color(0xFFFAF1E4),
      sunCircle = Color(0xFFFFE0B2),
      fur = Color(0xFFF57C00),
      innerEar = Color(0xFFFFCCBC),
      patchColor = Color(0xFFFFF3E0),
      eyeColor = Color(0xFF2E3D30),
      nose = Color(0xFFE64A19),
      whiskerColor = Color(0xFF5D4037)
    )
    "oyen" -> CatArtTheme(
      bg = Color(0xFFFDF0ED),
      sunCircle = Color(0xFFFFD8CC),
      fur = Color(0xFFE65100),
      innerEar = Color(0xFFFFCCBC),
      patchColor = Color(0xFFFFE0B2),
      eyeColor = Color(0xFF1B3D2F),
      nose = Color(0xFFBF360C),
      whiskerColor = Color(0xFF4E342E)
    )
    "belang" -> CatArtTheme(
      bg = Color(0xFFF2F6F3),
      sunCircle = Color(0xFFD7E7DD),
      fur = Color(0xFFECEFF1),
      innerEar = Color(0xFFFFCDD2),
      patchColor = Color(0xFFE65100),
      eyeColor = Color(0xFF2E7D32),
      nose = Color(0xFFD81B60),
      whiskerColor = Color(0xFF37474F)
    )
    "bolu" -> CatArtTheme(
      bg = Color(0xFFFFF8E7),
      sunCircle = Color(0xFFFFEDB3),
      fur = Color(0xFFFFE082),
      innerEar = Color(0xFFFFCCBC),
      patchColor = Color(0xFFFFF9C4),
      eyeColor = Color(0xFF37474F),
      nose = Color(0xFFF4511E),
      whiskerColor = Color(0xFF6D4C41)
    )
    "mochi" -> CatArtTheme(
      bg = Color(0xFFEBF3F5),
      sunCircle = Color(0xFFCFE4EA),
      fur = Color(0xFFFAFAFA),
      innerEar = Color(0xFFFFCDD2),
      patchColor = Color(0xFF263238),
      eyeColor = Color(0xFF0277BD),
      nose = Color(0xFFE91E63),
      whiskerColor = Color(0xFF455A64)
    )
    "kuro" -> CatArtTheme(
      bg = Color(0xFFE8EAF6),
      sunCircle = Color(0xFFC5CAE9),
      fur = Color(0xFF263238),
      innerEar = Color(0xFF78909C),
      patchColor = null,
      eyeColor = Color(0xFFFFD600),
      nose = Color(0xFF37474F),
      whiskerColor = Color(0xFF90A4AE)
    )
    "cimot" -> CatArtTheme(
      bg = Color(0xFFF0F4F8),
      sunCircle = Color(0xFFD1DEE8),
      fur = Color(0xFF90A4AE),
      innerEar = Color(0xFFFFCDD2),
      patchColor = Color(0xFFCFD8DC),
      eyeColor = Color(0xFF00897B),
      nose = Color(0xFFE57373),
      whiskerColor = Color(0xFF37474F)
    )
    "luna" -> CatArtTheme(
      bg = Color(0xFFF5EEF8),
      sunCircle = Color(0xFFE1BEE7),
      fur = Color(0xFF4E342E),
      innerEar = Color(0xFFFFAB91),
      patchColor = Color(0xFFFFB74D),
      eyeColor = Color(0xFF7CB342),
      nose = Color(0xFFD84315),
      whiskerColor = Color(0xFF3E2723)
    )
    else -> CatArtTheme(
      bg = Color(0xFFE8F5E9),
      sunCircle = Color(0xFFC8E6C9),
      fur = Color(0xFF66BB6A),
      innerEar = Color(0xFFFFCDD2),
      patchColor = Color(0xFFE8F5E9),
      eyeColor = Color(0xFF1B5E20),
      nose = Color(0xFFE91E63),
      whiskerColor = Color(0xFF2E7D32)
    )
  }
}
