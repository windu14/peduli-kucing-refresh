package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Help
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.CatCondition
import com.example.data.model.CatStatus

@Composable
fun ConditionChip(
  condition: CatCondition,
  modifier: Modifier = Modifier
) {
  val (bgColor, textColor, icon) = when (condition) {
    CatCondition.SEHAT -> Triple(Color(0xFFE8F5E9), Color(0xFF2E7D32), Icons.Default.CheckCircle)
    CatCondition.SEDANG -> Triple(Color(0xFFFFF8E1), Color(0xFFF57F17), Icons.Default.Info)
    CatCondition.NAMPAK_TIDAK_SEHAT -> Triple(Color(0xFFFFF3E0), Color(0xFFE65100), Icons.Default.Warning)
    CatCondition.SAKIT -> Triple(Color(0xFFFFEBEE), Color(0xFFC62828), Icons.Default.Warning)
    CatCondition.PRIHATIN -> Triple(Color(0xFFFFEBEE), Color(0xFFD32F2F), Icons.Default.Favorite)
  }

  Row(
    modifier = modifier
      .clip(RoundedCornerShape(12.dp))
      .background(bgColor)
      .padding(horizontal = 8.dp, vertical = 4.dp),
    verticalAlignment = Alignment.CenterVertically
  ) {
    Icon(
      imageVector = icon,
      contentDescription = null,
      tint = textColor,
      modifier = Modifier.size(12.dp)
    )
    Spacer(modifier = Modifier.width(4.dp))
    Text(
      text = condition.displayName,
      color = textColor,
      fontSize = 11.sp,
      style = MaterialTheme.typography.labelSmall
    )
  }
}

@Composable
fun StatusBadge(
  status: CatStatus,
  modifier: Modifier = Modifier
) {
  val (bgColor, textColor) = when (status) {
    CatStatus.HEALTHY, CatStatus.HANDLED -> Pair(Color(0xFFE0F2F1), Color(0xFF00695C))
    CatStatus.NEED_ATTENTION, CatStatus.SEEN_AGAIN -> Pair(Color(0xFFFFF3E0), Color(0xFFE65100))
    CatStatus.INJURED -> Pair(Color(0xFFFFEBEE), Color(0xFFC62828))
    CatStatus.NEWLY_FOUND -> Pair(Color(0xFFF3E5F5), Color(0xFF6A1B9A))
    CatStatus.NOT_SEEN -> Pair(Color(0xFFECEFF1), Color(0xFF546E7A))
  }

  Box(
    modifier = modifier
      .clip(RoundedCornerShape(8.dp))
      .background(bgColor)
      .padding(horizontal = 8.dp, vertical = 3.dp)
  ) {
    Text(
      text = status.displayName,
      color = textColor,
      fontSize = 11.sp,
      style = MaterialTheme.typography.labelSmall
    )
  }
}
