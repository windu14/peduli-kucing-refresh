package com.example.ui.explore

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.gestures.detectTransformGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.MyLocation
import androidx.compose.material.icons.filled.Pets
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.SmallFloatingActionButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Fill
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.model.CatCondition
import com.example.domain.model.Cat
import com.example.ui.components.CatAvatarImage
import com.example.ui.components.ConditionChip
import com.example.ui.components.StatusBadge
import com.example.ui.theme.MapBuilding
import com.example.ui.theme.MapPark
import com.example.ui.theme.MapRoad
import com.example.ui.theme.MapWater
import com.example.ui.theme.PeachSecondary
import com.example.ui.theme.SagePrimary
import com.example.ui.theme.StatusInjured
import com.example.ui.theme.StatusNeedHelp
import kotlin.math.cos

@Composable
fun ExploreMapScreen(
  viewModel: ExploreViewModel,
  onNavigateToCatDetail: (Long) -> Unit,
  onNavigateToCamera: () -> Unit
) {
  val uiState by viewModel.uiState.collectAsStateWithLifecycle()

  val infiniteTransition = rememberInfiniteTransition(label = "pulse")
  val pulseRadius by infiniteTransition.animateFloat(
    initialValue = 12f,
    targetValue = 28f,
    animationSpec = infiniteRepeatable(
      animation = tween(1500, easing = FastOutSlowInEasing),
      repeatMode = RepeatMode.Restart
    ),
    label = "pulseRadius"
  )
  val pulseAlpha by infiniteTransition.animateFloat(
    initialValue = 0.6f,
    targetValue = 0f,
    animationSpec = infiniteRepeatable(
      animation = tween(1500, easing = FastOutSlowInEasing),
      repeatMode = RepeatMode.Restart
    ),
    label = "pulseAlpha"
  )

  Box(
    modifier = Modifier
      .fillMaxSize()
      .background(Color(0xFFF3EFEA))
  ) {
    // 1. Interactive Vector & Tile Map Canvas
    InteractiveOpenStreetMapCanvas(
      centerLat = uiState.mapCenterLat,
      centerLng = uiState.mapCenterLng,
      zoom = uiState.zoomLevel,
      userLat = uiState.userLatitude,
      userLng = uiState.userLongitude,
      cats = uiState.filteredCats,
      selectedCat = uiState.selectedCat,
      pulseRadius = pulseRadius,
      pulseAlpha = pulseAlpha,
      onCatSelected = { viewModel.selectCat(it) },
      onPan = { dx, dy -> viewModel.panMap(dx, dy) }
    )

    // 2. Top Search & Filter Overlay
    Column(
      modifier = Modifier
        .fillMaxWidth()
        .statusBarsPadding()
        .padding(top = 12.dp)
    ) {
      // Search Box
      Surface(
        shape = RoundedCornerShape(24.dp),
        color = MaterialTheme.colorScheme.surface,
        shadowElevation = 4.dp,
        modifier = Modifier
          .fillMaxWidth()
          .padding(horizontal = 16.dp)
      ) {
        Row(
          modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 14.dp, vertical = 6.dp),
          verticalAlignment = Alignment.CenterVertically
        ) {
          Icon(
            imageVector = Icons.Default.Search,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.onSurfaceVariant
          )
          Spacer(modifier = Modifier.width(8.dp))
          OutlinedTextField(
            value = uiState.searchQuery,
            onValueChange = { viewModel.setSearchQuery(it) },
            placeholder = { Text("Cari cuking atau area sekitar...") },
            singleLine = true,
            colors = OutlinedTextFieldDefaults.colors(
              focusedBorderColor = Color.Transparent,
              unfocusedBorderColor = Color.Transparent
            ),
            modifier = Modifier.weight(1f)
          )
          if (uiState.searchQuery.isNotEmpty()) {
            IconButton(onClick = { viewModel.setSearchQuery("") }) {
              Icon(imageVector = Icons.Default.Close, contentDescription = "Clear")
            }
          }
        }
      }

      Spacer(modifier = Modifier.height(10.dp))

      // Filter Chips Row
      LazyRow(
        contentPadding = PaddingValues(horizontal = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
      ) {
        items(MapFilterType.values()) { filter ->
          val isSelected = uiState.activeFilter == filter
          FilterChip(
            selected = isSelected,
            onClick = { viewModel.setFilter(filter) },
            label = {
              Text(
                text = filter.label,
                fontSize = 12.sp,
                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
              )
            },
            shape = RoundedCornerShape(16.dp),
            colors = FilterChipDefaults.filterChipColors(
              selectedContainerColor = MaterialTheme.colorScheme.primary,
              selectedLabelColor = MaterialTheme.colorScheme.onPrimary,
              containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.95f),
              labelColor = MaterialTheme.colorScheme.onSurface
            ),
            elevation = FilterChipDefaults.filterChipElevation(elevation = 2.dp)
          )
        }
      }
    }

    // 3. Floating Map Controls (Zoom + Recenter)
    Column(
      modifier = Modifier
        .align(Alignment.CenterEnd)
        .padding(end = 16.dp),
      verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
      SmallFloatingActionButton(
        onClick = { viewModel.zoomIn() },
        containerColor = MaterialTheme.colorScheme.surface,
        contentColor = MaterialTheme.colorScheme.onSurface,
        shape = CircleShape
      ) {
        Icon(Icons.Default.Add, contentDescription = "Zoom In")
      }

      SmallFloatingActionButton(
        onClick = { viewModel.zoomOut() },
        containerColor = MaterialTheme.colorScheme.surface,
        contentColor = MaterialTheme.colorScheme.onSurface,
        shape = CircleShape
      ) {
        Icon(Icons.Default.Remove, contentDescription = "Zoom Out")
      }

      FloatingActionButton(
        onClick = { viewModel.recenterToUser() },
        containerColor = MaterialTheme.colorScheme.primaryContainer,
        contentColor = MaterialTheme.colorScheme.onPrimaryContainer,
        shape = RoundedCornerShape(18.dp)
      ) {
        Icon(Icons.Default.MyLocation, contentDescription = "Lokasi Saya")
      }
    }

    // 4. Selected Cat Bottom Peek Sheet
    AnimatedVisibility(
      visible = uiState.selectedCat != null,
      enter = slideInVertically { it },
      exit = slideOutVertically { it },
      modifier = Modifier
        .align(Alignment.BottomCenter)
        .padding(bottom = 90.dp, start = 16.dp, end = 16.dp)
    ) {
      uiState.selectedCat?.let { cat ->
        SelectedCatPeekCard(
          cat = cat,
          onClose = { viewModel.selectCat(null) },
          onOpenDetail = { onNavigateToCatDetail(cat.id) }
        )
      }
    }
  }
}

@Composable
fun InteractiveOpenStreetMapCanvas(
  centerLat: Double,
  centerLng: Double,
  zoom: Float,
  userLat: Double,
  userLng: Double,
  cats: List<Cat>,
  selectedCat: Cat?,
  pulseRadius: Float,
  pulseAlpha: Float,
  onCatSelected: (Cat) -> Unit,
  onPan: (Float, Float) -> Unit
) {
  var canvasSize by remember { mutableStateOf(Size.Zero) }

  Box(
    modifier = Modifier
      .fillMaxSize()
      .pointerInput(cats, centerLat, centerLng, zoom, canvasSize) {
        detectTapGestures { offset ->
          val scale = zoom * 12000f
          val clickedCat = cats.firstOrNull { cat ->
            val px = (canvasSize.width / 2 + (cat.longitude - centerLng) * scale * cos(Math.toRadians(centerLat))).toFloat()
            val py = (canvasSize.height / 2 - (cat.latitude - centerLat) * scale).toFloat()
            val distSq = (offset.x - px) * (offset.x - px) + (offset.y - py) * (offset.y - py)
            distSq < 2500f // 50px click radius
          }
          if (clickedCat != null) {
            onCatSelected(clickedCat)
          }
        }
      }
      .pointerInput(Unit) {
        detectDragGestures { change, dragAmount ->
          change.consume()
          onPan(dragAmount.x, dragAmount.y)
        }
      }
  ) {
    Canvas(modifier = Modifier.fillMaxSize()) {
      canvasSize = size
      val w = size.width
      val h = size.height

      // Background Soft Warm Gray
      drawRect(color = Color(0xFFF6F3EE))

      // Coordinates mapping helper
      val scale = zoom * 12000f

      fun latLngToScreen(lat: Double, lng: Double): Offset {
        val x = (w / 2 + (lng - centerLng) * scale * cos(Math.toRadians(centerLat))).toFloat()
        val y = (h / 2 - (lat - centerLat) * scale).toFloat()
        return Offset(x, y)
      }

      // Draw decorative map features (Road grid, parks, water body)
      val parkOffset = latLngToScreen(-6.9165, 107.6200)
      drawRoundRect(
        color = MapPark,
        topLeft = Offset(parkOffset.x - 200f, parkOffset.y - 140f),
        size = Size(400f, 280f),
        cornerRadius = CornerRadius(24f, 24f)
      )

      val riverPath = Path().apply {
        val p1 = latLngToScreen(-6.9210, 107.6150)
        val p2 = latLngToScreen(-6.9140, 107.6240)
        moveTo(p1.x - 100f, p1.y + 200f)
        cubicTo(p1.x + 100f, p1.y, p2.x - 100f, p2.y + 100f, p2.x + 200f, p2.y - 200f)
      }
      drawPath(riverPath, color = MapWater, style = Stroke(width = 44f))

      // Street lines
      for (i in -4..4) {
        val pt = latLngToScreen(centerLat + i * 0.002, centerLng)
        drawLine(
          color = MapRoad,
          start = Offset(0f, pt.y),
          end = Offset(w, pt.y),
          strokeWidth = 24f
        )
      }
      for (i in -4..4) {
        val pt = latLngToScreen(centerLat, centerLng + i * 0.002)
        drawLine(
          color = MapRoad,
          start = Offset(pt.x, 0f),
          end = Offset(pt.x, h),
          strokeWidth = 20f
        )
      }

      // Draw User Current Location Pulse
      val userPos = latLngToScreen(userLat, userLng)
      drawCircle(
        color = SagePrimary.copy(alpha = pulseAlpha),
        radius = pulseRadius * 2.5f,
        center = userPos
      )
      drawCircle(
        color = SagePrimary,
        radius = 12f,
        center = userPos
      )
      drawCircle(
        color = Color.White,
        radius = 5f,
        center = userPos
      )

      // Draw Cat Markers
      cats.forEach { cat ->
        val pos = latLngToScreen(cat.latitude, cat.longitude)
        val isSelected = selectedCat?.id == cat.id

        val markerColor = when (cat.condition) {
          CatCondition.PRIHATIN, CatCondition.SAKIT -> StatusInjured
          CatCondition.NAMPAK_TIDAK_SEHAT -> StatusNeedHelp
          CatCondition.SEDANG -> Color(0xFFF57F17)
          CatCondition.SEHAT -> SagePrimary
        }

        // Marker Shadow
        drawCircle(
          color = Color(0x33000000),
          radius = if (isSelected) 26f else 20f,
          center = Offset(pos.x, pos.y + 4f)
        )

        // Marker Pin Body
        drawCircle(
          color = if (isSelected) PeachSecondary else markerColor,
          radius = if (isSelected) 24f else 18f,
          center = pos
        )
        drawCircle(
          color = Color.White,
          radius = if (isSelected) 21f else 15f,
          center = pos
        )
        drawCircle(
          color = if (isSelected) PeachSecondary else markerColor,
          radius = if (isSelected) 18f else 12f,
          center = pos
        )

        // Paw print in marker center
        drawCircle(
          color = Color.White,
          radius = if (isSelected) 6f else 4f,
          center = Offset(pos.x, pos.y + 2f)
        )
        drawCircle(
          color = Color.White,
          radius = if (isSelected) 2.5f else 1.8f,
          center = Offset(pos.x - 5f, pos.y - 3f)
        )
        drawCircle(
          color = Color.White,
          radius = if (isSelected) 3f else 2f,
          center = Offset(pos.x, pos.y - 6f)
        )
        drawCircle(
          color = Color.White,
          radius = if (isSelected) 2.5f else 1.8f,
          center = Offset(pos.x + 5f, pos.y - 3f)
        )
      }
    }
  }
}

@Composable
private fun SelectedCatPeekCard(
  cat: Cat,
  onClose: () -> Unit,
  onOpenDetail: () -> Unit
) {
  Card(
    shape = RoundedCornerShape(26.dp),
    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
    elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
    modifier = Modifier
      .fillMaxWidth()
      .clickable { onOpenDetail() }
  ) {
    Column(modifier = Modifier.padding(16.dp)) {
      Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
      ) {
        Box(
          modifier = Modifier
            .size(72.dp)
            .clip(RoundedCornerShape(18.dp))
        ) {
          CatAvatarImage(
            nickname = cat.nickname,
            photoUri = cat.photoUri,
            modifier = Modifier.fillMaxSize(),
            shapeRadiusDp = 18
          )
        }

        Spacer(modifier = Modifier.width(14.dp))

        Column(modifier = Modifier.weight(1f)) {
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
              Text(
                text = cat.nickname,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
              )
              if (cat.colorPattern.isNotBlank()) {
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                  text = "• ${cat.colorPattern}",
                  style = MaterialTheme.typography.bodySmall,
                  color = MaterialTheme.colorScheme.onSurfaceVariant
                )
              }
            }

            IconButton(onClick = onClose, modifier = Modifier.size(28.dp)) {
              Icon(
                imageVector = Icons.Default.Close,
                contentDescription = "Tutup",
                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.size(18.dp)
              )
            }
          }

          Spacer(modifier = Modifier.height(4.dp))

          Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(6.dp)
          ) {
            ConditionChip(condition = cat.condition)
            Surface(
              shape = RoundedCornerShape(8.dp),
              color = MaterialTheme.colorScheme.surfaceVariant
            ) {
              Text(
                text = cat.physique.displayName,
                style = MaterialTheme.typography.labelSmall,
                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
              )
            }
            Surface(
              shape = RoundedCornerShape(8.dp),
              color = MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.5f)
            ) {
              Text(
                text = cat.ownership.displayName,
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSecondaryContainer,
                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
              )
            }
          }

          Spacer(modifier = Modifier.height(6.dp))

          Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
              imageVector = Icons.Default.LocationOn,
              contentDescription = null,
              tint = MaterialTheme.colorScheme.primary,
              modifier = Modifier.size(14.dp)
            )
            Spacer(modifier = Modifier.width(4.dp))
            Text(
              text = "${cat.locationName} (${cat.distanceMeters}m)",
              style = MaterialTheme.typography.bodySmall,
              color = MaterialTheme.colorScheme.onSurfaceVariant,
              maxLines = 1
            )
          }
        }
      }

      Spacer(modifier = Modifier.height(14.dp))

      Button(
        onClick = onOpenDetail,
        shape = RoundedCornerShape(16.dp),
        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
        modifier = Modifier
          .fillMaxWidth()
          .height(46.dp)
      ) {
        Text(
          text = "Lihat Detail Lengkap Cuking",
          fontWeight = FontWeight.SemiBold,
          fontSize = 14.sp
        )
      }
    }
  }
}
