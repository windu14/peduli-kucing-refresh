package com.example.ui.home

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Explore
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Pets
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.domain.model.Cat
import com.example.domain.model.Discovery
import com.example.domain.model.UserProfile
import com.example.ui.components.CatAvatarImage
import com.example.ui.components.CatListCard
import com.example.ui.components.ConditionChip
import com.example.ui.components.NearbyCatCompactCard
import com.example.ui.components.XpProgressCard
import com.example.ui.theme.PeachSecondary
import com.example.ui.theme.SagePrimary
import com.example.ui.theme.XpGold
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun HomeScreen(
  viewModel: HomeViewModel,
  onNavigateToCamera: () -> Unit,
  onNavigateToExplore: () -> Unit,
  onNavigateToCollection: () -> Unit,
  onNavigateToCatDetail: (Long) -> Unit,
  onNavigateToProfile: () -> Unit
) {
  val uiState by viewModel.uiState.collectAsStateWithLifecycle()

  LazyColumn(
    modifier = Modifier
      .fillMaxSize()
      .background(MaterialTheme.colorScheme.background),
    contentPadding = PaddingValues(bottom = 90.dp)
  ) {
    // 1. Header & Greeting
    item {
      HeaderSection(
        profile = uiState.userProfile,
        onProfileClick = onNavigateToProfile
      )
    }

    // 2. XP Progress Card
    item {
      uiState.userProfile?.let { profile ->
        Box(modifier = Modifier.padding(horizontal = 20.dp, vertical = 8.dp)) {
          XpProgressCard(
            profile = profile,
            onCardClick = onNavigateToProfile
          )
        }
      }
    }

    // 3. Quick Action Buttons
    item {
      QuickActionsSection(
        onCameraClick = onNavigateToCamera,
        onExploreClick = onNavigateToExplore,
        onCollectionClick = onNavigateToCollection
      )
    }

    // 4. Nearby Cats Horizontal Scroll
    item {
      SectionHeader(
        title = "Cuking di Sekitar Kamu",
        subtitle = "Berdasarkan jarak terdekat",
        actionText = "Lihat Peta",
        onActionClick = onNavigateToExplore
      )

      if (uiState.nearbyCats.isEmpty() && !uiState.isLoading) {
        EmptyNearbyCats(onAddCatClick = onNavigateToCamera)
      } else {
        LazyRow(
          contentPadding = PaddingValues(horizontal = 20.dp),
          horizontalArrangement = Arrangement.spacedBy(14.dp),
          modifier = Modifier.padding(vertical = 6.dp)
        ) {
          items(uiState.nearbyCats, key = { it.id }) { cat ->
            NearbyCatCompactCard(
              cat = cat,
              onClick = { onNavigateToCatDetail(cat.id) }
            )
          }
        }
      }
    }

    // 5. Daily Mission Card
    item {
      DailyMissionSection(
        mission = uiState.dailyMission,
        onMissionClick = onNavigateToCamera
      )
    }

    // 6. Recent Discoveries Header & List
    item {
      SectionHeader(
        title = "Penemuan Terakhir",
        subtitle = "Aktivitas terbaru komunitas",
        actionText = "Koleksi",
        onActionClick = onNavigateToCollection
      )
    }

    if (uiState.nearbyCats.isEmpty()) {
      item {
        Box(
          modifier = Modifier
            .fillMaxWidth()
            .padding(24.dp),
          contentAlignment = Alignment.Center
        ) {
          CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
        }
      }
    } else {
      items(uiState.nearbyCats.take(4), key = { "recent_${it.id}" }) { cat ->
        Box(modifier = Modifier.padding(horizontal = 20.dp, vertical = 6.dp)) {
          CatListCard(
            cat = cat,
            onClick = { onNavigateToCatDetail(cat.id) },
            onFavoriteClick = { viewModel.toggleFavorite(cat.id, !cat.isFavorite) }
          )
        }
      }
    }
  }
}

@Composable
private fun HeaderSection(
  profile: UserProfile?,
  onProfileClick: () -> Unit
) {
  Row(
    modifier = Modifier
      .fillMaxWidth()
      .statusBarsPadding()
      .padding(start = 20.dp, end = 20.dp, top = 16.dp, bottom = 8.dp),
    horizontalArrangement = Arrangement.SpaceBetween,
    verticalAlignment = Alignment.CenterVertically
  ) {
    Column {
      Row(verticalAlignment = Alignment.CenterVertically) {
        Text(
          text = "Halo, ${profile?.displayName ?: "Windu"}",
          style = MaterialTheme.typography.headlineLarge,
          fontWeight = FontWeight.Bold,
          color = MaterialTheme.colorScheme.onBackground
        )
        Text(
          text = " 👋",
          fontSize = 24.sp
        )
      }
      Spacer(modifier = Modifier.height(2.dp))
      Text(
        text = "Siap peduli sama cuking hari ini?",
        style = MaterialTheme.typography.bodyMedium,
        color = MaterialTheme.colorScheme.onSurfaceVariant
      )
    }

    // Avatar Button
    Surface(
      shape = CircleShape,
      color = MaterialTheme.colorScheme.primaryContainer,
      modifier = Modifier
        .size(48.dp)
        .clickable { onProfileClick() }
    ) {
      Box(contentAlignment = Alignment.Center) {
        Icon(
          imageVector = Icons.Default.Pets,
          contentDescription = "Profil",
          tint = MaterialTheme.colorScheme.primary,
          modifier = Modifier.size(26.dp)
        )
      }
    }
  }
}

@Composable
private fun QuickActionsSection(
  onCameraClick: () -> Unit,
  onExploreClick: () -> Unit,
  onCollectionClick: () -> Unit
) {
  Row(
    modifier = Modifier
      .fillMaxWidth()
      .padding(horizontal = 20.dp, vertical = 12.dp),
    horizontalArrangement = Arrangement.spacedBy(10.dp)
  ) {
    QuickActionButton(
      icon = Icons.Default.CameraAlt,
      label = "Tambah\nCuking",
      bgContainer = MaterialTheme.colorScheme.primary,
      contentColor = MaterialTheme.colorScheme.onPrimary,
      onClick = onCameraClick,
      modifier = Modifier.weight(1.2f)
    )

    QuickActionButton(
      icon = Icons.Default.Explore,
      label = "Jelajahi\nSekitar",
      bgContainer = MaterialTheme.colorScheme.surfaceVariant,
      contentColor = MaterialTheme.colorScheme.onSurfaceVariant,
      onClick = onExploreClick,
      modifier = Modifier.weight(1f)
    )

    QuickActionButton(
      icon = Icons.Default.Favorite,
      label = "Cuking\nSaya",
      bgContainer = MaterialTheme.colorScheme.surfaceVariant,
      contentColor = MaterialTheme.colorScheme.onSurfaceVariant,
      onClick = onCollectionClick,
      modifier = Modifier.weight(1f)
    )
  }
}

@Composable
private fun QuickActionButton(
  icon: ImageVector,
  label: String,
  bgContainer: Color,
  contentColor: Color,
  onClick: () -> Unit,
  modifier: Modifier = Modifier
) {
  Surface(
    onClick = onClick,
    shape = RoundedCornerShape(20.dp),
    color = bgContainer,
    modifier = modifier.height(84.dp)
  ) {
    Column(
      modifier = Modifier
        .fillMaxSize()
        .padding(10.dp),
      verticalArrangement = Arrangement.SpaceBetween,
      horizontalAlignment = Alignment.Start
    ) {
      Icon(
        imageVector = icon,
        contentDescription = null,
        tint = contentColor,
        modifier = Modifier.size(22.dp)
      )
      Text(
        text = label,
        color = contentColor,
        style = MaterialTheme.typography.labelMedium,
        fontWeight = FontWeight.SemiBold,
        lineHeight = 14.sp
      )
    }
  }
}

@Composable
private fun SectionHeader(
  title: String,
  subtitle: String,
  actionText: String? = null,
  onActionClick: (() -> Unit)? = null
) {
  Row(
    modifier = Modifier
      .fillMaxWidth()
      .padding(start = 20.dp, end = 20.dp, top = 20.dp, bottom = 8.dp),
    horizontalArrangement = Arrangement.SpaceBetween,
    verticalAlignment = Alignment.CenterVertically
  ) {
    Column {
      Text(
        text = title,
        style = MaterialTheme.typography.titleLarge,
        fontWeight = FontWeight.Bold,
        color = MaterialTheme.colorScheme.onBackground
      )
      Text(
        text = subtitle,
        style = MaterialTheme.typography.bodySmall,
        color = MaterialTheme.colorScheme.onSurfaceVariant
      )
    }

    if (actionText != null && onActionClick != null) {
      Text(
        text = actionText,
        style = MaterialTheme.typography.labelMedium,
        fontWeight = FontWeight.SemiBold,
        color = MaterialTheme.colorScheme.primary,
        modifier = Modifier
          .clip(RoundedCornerShape(8.dp))
          .clickable { onActionClick() }
          .padding(horizontal = 8.dp, vertical = 4.dp)
      )
    }
  }
}

@Composable
private fun DailyMissionSection(
  mission: com.example.domain.model.DailyMission?,
  onMissionClick: () -> Unit
) {
  Card(
    shape = RoundedCornerShape(24.dp),
    colors = CardDefaults.cardColors(
      containerColor = MaterialTheme.colorScheme.surface
    ),
    elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
    modifier = Modifier
      .fillMaxWidth()
      .padding(horizontal = 20.dp, vertical = 12.dp)
  ) {
    Row(
      modifier = Modifier
        .fillMaxWidth()
        .padding(16.dp),
      verticalAlignment = Alignment.CenterVertically
    ) {
      Surface(
        shape = RoundedCornerShape(14.dp),
        color = Color(0xFFFFDBCF),
        modifier = Modifier.size(44.dp)
      ) {
        Box(contentAlignment = Alignment.Center) {
          Icon(
            imageVector = Icons.Default.Pets,
            contentDescription = null,
            tint = PeachSecondary,
            modifier = Modifier.size(24.dp)
          )
        }
      }

      Spacer(modifier = Modifier.width(14.dp))

      Column(modifier = Modifier.weight(1f)) {
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween
        ) {
          Text(
            text = "Misi Hari Ini",
            style = MaterialTheme.typography.labelSmall,
            fontWeight = FontWeight.SemiBold,
            color = PeachSecondary
          )

          Surface(
            shape = RoundedCornerShape(8.dp),
            color = MaterialTheme.colorScheme.primaryContainer
          ) {
            Text(
              text = "+50 XP",
              style = MaterialTheme.typography.labelSmall,
              fontWeight = FontWeight.Bold,
              color = MaterialTheme.colorScheme.onPrimaryContainer,
              modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
            )
          }
        }

        Spacer(modifier = Modifier.height(4.dp))

        Text(
          text = mission?.title ?: "Temukan 2 cuking di jalan",
          style = MaterialTheme.typography.titleSmall,
          fontWeight = FontWeight.Bold,
          color = MaterialTheme.colorScheme.onSurface
        )

        Spacer(modifier = Modifier.height(8.dp))

        Row(
          modifier = Modifier.fillMaxWidth(),
          verticalAlignment = Alignment.CenterVertically
        ) {
          LinearProgressIndicator(
            progress = { (mission?.current ?: 1).toFloat() / (mission?.target ?: 2).toFloat() },
            modifier = Modifier
              .weight(1f)
              .height(6.dp)
              .clip(CircleShape),
            color = MaterialTheme.colorScheme.primary,
            trackColor = MaterialTheme.colorScheme.surfaceVariant
          )
          Spacer(modifier = Modifier.width(8.dp))
          Text(
            text = "${mission?.current ?: 1}/${mission?.target ?: 2}",
            style = MaterialTheme.typography.labelSmall,
            fontWeight = FontWeight.Medium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
          )
        }
      }
    }
  }
}

@Composable
private fun EmptyNearbyCats(
  onAddCatClick: () -> Unit
) {
  Card(
    shape = RoundedCornerShape(20.dp),
    colors = CardDefaults.cardColors(
      containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
    ),
    modifier = Modifier
      .fillMaxWidth()
      .padding(horizontal = 20.dp, vertical = 8.dp)
  ) {
    Column(
      modifier = Modifier
        .fillMaxWidth()
        .padding(24.dp),
      horizontalAlignment = Alignment.CenterHorizontally
    ) {
      Text(
        text = "🐾",
        fontSize = 32.sp
      )
      Spacer(modifier = Modifier.height(8.dp))
      Text(
        text = "Belum ada cuking di sekitar sini.",
        style = MaterialTheme.typography.titleSmall,
        fontWeight = FontWeight.Bold,
        color = MaterialTheme.colorScheme.onSurface
      )
      Spacer(modifier = Modifier.height(4.dp))
      Text(
        text = "Yuk jadi yang pertama menemukannya.",
        style = MaterialTheme.typography.bodySmall,
        color = MaterialTheme.colorScheme.onSurfaceVariant
      )
      Spacer(modifier = Modifier.height(14.dp))
      Button(
        onClick = onAddCatClick,
        shape = RoundedCornerShape(14.dp),
        colors = ButtonDefaults.buttonColors(
          containerColor = MaterialTheme.colorScheme.primary
        )
      ) {
        Text("Temukan Cuking")
      }
    }
  }
}
