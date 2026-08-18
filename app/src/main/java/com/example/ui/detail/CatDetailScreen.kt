package com.example.ui.detail

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Pets
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.model.CatStatus
import com.example.domain.model.Discovery
import com.example.ui.components.CatAvatarImage
import com.example.ui.components.ConditionChip
import com.example.ui.components.GamificationFeedbackDialog
import com.example.ui.components.StatusBadge
import com.example.ui.theme.PeachSecondary
import com.example.ui.theme.SagePrimary
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CatDetailScreen(
  viewModel: CatDetailViewModel,
  onNavigateBack: () -> Unit,
  onOpenMap: (Double, Double) -> Unit
) {
  val uiState by viewModel.uiState.collectAsStateWithLifecycle()
  var showFeedbackDialog by remember { mutableStateOf(false) }
  var feedbackXp by remember { mutableStateOf(25) }
  var feedbackTitle by remember { mutableStateOf("Status Diperbarui!") }
  var feedbackMsg by remember { mutableStateOf("Terima kasih sudah peduli dan membantu mencatat kondisi cuking.") }

  if (uiState.isLoading || uiState.cat == null) {
    Box(
      modifier = Modifier
        .fillMaxSize()
        .background(MaterialTheme.colorScheme.background),
      contentAlignment = Alignment.Center
    ) {
      CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
    }
    return
  }

  val cat = uiState.cat!!

  Box(
    modifier = Modifier
      .fillMaxSize()
      .background(MaterialTheme.colorScheme.background)
  ) {
    LazyColumn(
      modifier = Modifier.fillMaxSize(),
      contentPadding = PaddingValues(bottom = 110.dp)
    ) {
      // 1. Hero Image Header
      item {
        Box(
          modifier = Modifier
            .fillMaxWidth()
            .height(300.dp)
        ) {
          CatAvatarImage(
            nickname = cat.nickname,
            photoUri = cat.photoUri,
            avatarPresetId = cat.avatarPresetId,
            modifier = Modifier.fillMaxSize(),
            shapeRadiusDp = 0
          )

          // Top action bar
          Row(
            modifier = Modifier
              .fillMaxWidth()
              .statusBarsPadding()
              .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            IconButton(
              onClick = onNavigateBack,
              modifier = Modifier
                .clip(CircleShape)
                .background(Color(0x66000000))
            ) {
              Icon(
                imageVector = Icons.Default.ArrowBack,
                contentDescription = "Kembali",
                tint = Color.White
              )
            }

            IconButton(
              onClick = { viewModel.toggleFavorite() },
              modifier = Modifier
                .clip(CircleShape)
                .background(Color(0x66000000))
            ) {
              Icon(
                imageVector = if (cat.isFavorite) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                contentDescription = "Favorit",
                tint = if (cat.isFavorite) PeachSecondary else Color.White
              )
            }
          }
        }
      }

      // 2. Main Details Card
      item {
        Surface(
          shape = RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp),
          color = MaterialTheme.colorScheme.background,
          modifier = Modifier
            .fillMaxWidth()
            .offset(y = (-24).dp)
        ) {
          Column(modifier = Modifier.padding(20.dp)) {
            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.SpaceBetween,
              verticalAlignment = Alignment.CenterVertically
            ) {
              Column {
                Text(
                  text = cat.nickname,
                  style = MaterialTheme.typography.headlineLarge,
                  fontWeight = FontWeight.Bold,
                  color = MaterialTheme.colorScheme.onBackground
                )
                if (cat.colorPattern.isNotBlank()) {
                  Text(
                    text = "Corak: ${cat.colorPattern}",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.SemiBold
                  )
                }
              }

              Surface(
                shape = RoundedCornerShape(12.dp),
                color = MaterialTheme.colorScheme.primaryContainer
              ) {
                Row(
                  modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                  verticalAlignment = Alignment.CenterVertically
                ) {
                  Icon(
                    imageVector = Icons.Default.Pets,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(16.dp)
                  )
                  Spacer(modifier = Modifier.width(4.dp))
                  Text(
                    text = "${cat.discoveryCount}x Dilihat",
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onPrimaryContainer
                  )
                }
              }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Badges Row: Condition, Physique, Ownership
            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.spacedBy(8.dp),
              verticalAlignment = Alignment.CenterVertically
            ) {
              ConditionChip(condition = cat.condition)

              Surface(
                shape = RoundedCornerShape(10.dp),
                color = MaterialTheme.colorScheme.surfaceVariant
              ) {
                Text(
                  text = "Fisik: ${cat.physique.displayName}",
                  style = MaterialTheme.typography.labelSmall,
                  fontWeight = FontWeight.SemiBold,
                  color = MaterialTheme.colorScheme.onSurfaceVariant,
                  modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                )
              }

              Surface(
                shape = RoundedCornerShape(10.dp),
                color = MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.6f)
              ) {
                Text(
                  text = cat.ownership.displayName,
                  style = MaterialTheme.typography.labelSmall,
                  fontWeight = FontWeight.SemiBold,
                  color = MaterialTheme.colorScheme.onSecondaryContainer,
                  modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                )
              }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Location Card
            Card(
              onClick = { onOpenMap(cat.latitude, cat.longitude) },
              shape = RoundedCornerShape(18.dp),
              colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
              elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
              modifier = Modifier.fillMaxWidth()
            ) {
              Row(
                modifier = Modifier
                  .fillMaxWidth()
                  .padding(14.dp),
                verticalAlignment = Alignment.CenterVertically
              ) {
                Surface(
                  shape = CircleShape,
                  color = MaterialTheme.colorScheme.primaryContainer,
                  modifier = Modifier.size(40.dp)
                ) {
                  Box(contentAlignment = Alignment.Center) {
                    Icon(
                      imageVector = Icons.Default.LocationOn,
                      contentDescription = null,
                      tint = MaterialTheme.colorScheme.primary,
                      modifier = Modifier.size(22.dp)
                    )
                  }
                }

                Spacer(modifier = Modifier.width(12.dp))

                Column(modifier = Modifier.weight(1f)) {
                  Text(
                    text = cat.locationName.ifBlank { "Lokasi Penemuan" },
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurface
                  )
                  Text(
                    text = "${cat.distanceMeters}m dari kamu • Lat: ${"%.4f".format(cat.latitude)}, Lng: ${"%.4f".format(cat.longitude)}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                  )
                }

                Text(
                  text = "Buka Peta",
                  style = MaterialTheme.typography.labelSmall,
                  fontWeight = FontWeight.Bold,
                  color = MaterialTheme.colorScheme.primary
                )
              }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Description / Notes Section
            if (cat.description.isNotBlank() || cat.notes.isNotBlank()) {
              Card(
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)),
                modifier = Modifier.fillMaxWidth()
              ) {
                Column(modifier = Modifier.padding(16.dp)) {
                  Text(
                    text = "Detail & Deskripsi",
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                  )
                  Spacer(modifier = Modifier.height(6.dp))
                  Text(
                    text = if (cat.description.isNotBlank()) cat.description else cat.notes,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    lineHeight = 20.sp
                  )
                }
              }
              Spacer(modifier = Modifier.height(20.dp))
            }

            // Timeline Header
            Text(
              text = "Riwayat Penemuan",
              style = MaterialTheme.typography.titleLarge,
              fontWeight = FontWeight.Bold,
              color = MaterialTheme.colorScheme.onBackground
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
              text = "Jejak kebaikan komunitas untuk ${cat.nickname}",
              style = MaterialTheme.typography.bodySmall,
              color = MaterialTheme.colorScheme.onSurfaceVariant
            )
          }
        }
      }

      // 3. Timeline Items
      items(uiState.discoveries, key = { it.id }) { discovery ->
        Box(modifier = Modifier.padding(horizontal = 20.dp, vertical = 6.dp)) {
          DiscoveryTimelineItem(discovery = discovery)
        }
      }
    }

    // Bottom Sticky Action Bar
    Surface(
      color = MaterialTheme.colorScheme.surface,
      tonalElevation = 8.dp,
      modifier = Modifier
        .fillMaxWidth()
        .align(Alignment.BottomCenter)
        .navigationBarsPadding()
    ) {
      Row(
        modifier = Modifier
          .fillMaxWidth()
          .padding(horizontal = 16.dp, vertical = 12.dp),
        horizontalArrangement = Arrangement.spacedBy(10.dp)
      ) {
        OutlinedButton(
          onClick = {
            viewModel.recordSeenAgain { xp ->
              feedbackXp = xp
              feedbackTitle = "Terlihat Lagi! (+25 XP)"
              feedbackMsg = "Kamu mencatat keberadaan ${cat.nickname} hari ini."
              showFeedbackDialog = true
            }
          },
          shape = RoundedCornerShape(16.dp),
          modifier = Modifier
            .weight(1f)
            .height(50.dp)
        ) {
          Icon(Icons.Default.Visibility, contentDescription = null, modifier = Modifier.size(18.dp))
          Spacer(modifier = Modifier.width(6.dp))
          Text("Terlihat Lagi", fontSize = 13.sp)
        }

        Button(
          onClick = { viewModel.showUpdateBottomSheet(true) },
          shape = RoundedCornerShape(16.dp),
          colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
          modifier = Modifier
            .weight(1.2f)
            .height(50.dp)
        ) {
          Icon(Icons.Default.CheckCircle, contentDescription = null, modifier = Modifier.size(18.dp))
          Spacer(modifier = Modifier.width(6.dp))
          Text("Bantu / Update", fontWeight = FontWeight.Bold, fontSize = 13.sp)
        }
      }
    }

    // Update Status Bottom Sheet
    if (uiState.showUpdateSheet) {
      UpdateStatusBottomSheet(
        onDismiss = { viewModel.showUpdateBottomSheet(false) },
        onSubmit = { status, notes ->
          viewModel.updateStatus(status, notes) { xp ->
            feedbackXp = xp
            feedbackTitle = "Kebaikan Tercatat! (+25 XP)"
            feedbackMsg = "Status ${cat.nickname} berhasil diperbarui menjadi ${status.displayName}."
            showFeedbackDialog = true
          }
        }
      )
    }

    // Feedback Dialog
    GamificationFeedbackDialog(
      show = showFeedbackDialog,
      xpGained = feedbackXp,
      title = feedbackTitle,
      message = feedbackMsg,
      onDismiss = { showFeedbackDialog = false }
    )
  }
}

@Composable
private fun DiscoveryTimelineItem(
  discovery: Discovery
) {
  val dateFormatted = SimpleDateFormat("dd MMM yyyy, HH:mm", Locale.getDefault()).format(Date(discovery.createdAt))

  Card(
    shape = RoundedCornerShape(18.dp),
    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
    elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
    modifier = Modifier.fillMaxWidth()
  ) {
    Row(
      modifier = Modifier
        .fillMaxWidth()
        .padding(14.dp),
      verticalAlignment = Alignment.Top
    ) {
      Surface(
        shape = CircleShape,
        color = MaterialTheme.colorScheme.primaryContainer,
        modifier = Modifier.size(36.dp)
      ) {
        Box(contentAlignment = Alignment.Center) {
          Icon(
            imageVector = Icons.Default.Schedule,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.primary,
            modifier = Modifier.size(18.dp)
          )
        }
      }

      Spacer(modifier = Modifier.width(12.dp))

      Column(modifier = Modifier.weight(1f)) {
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween
        ) {
          Text(
            text = discovery.condition.displayName,
            style = MaterialTheme.typography.titleSmall,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface
          )
          Text(
            text = "+${discovery.xpGained} XP",
            style = MaterialTheme.typography.labelSmall,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary
          )
        }

        Spacer(modifier = Modifier.height(2.dp))

        Text(
          text = dateFormatted,
          style = MaterialTheme.typography.bodySmall,
          color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        if (discovery.notes.isNotBlank()) {
          Spacer(modifier = Modifier.height(6.dp))
          Text(
            text = discovery.notes,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurface
          )
        }
      }
    }
  }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun UpdateStatusBottomSheet(
  onDismiss: () -> Unit,
  onSubmit: (CatStatus, String) -> Unit
) {
  val sheetState = rememberModalBottomSheetState()
  var selectedStatus by remember { mutableStateOf(CatStatus.HANDLED) }
  var notes by remember { mutableStateOf("") }

  ModalBottomSheet(
    onDismissRequest = onDismiss,
    sheetState = sheetState,
    containerColor = MaterialTheme.colorScheme.surface
  ) {
    Column(
      modifier = Modifier
        .fillMaxWidth()
        .padding(horizontal = 24.dp, vertical = 12.dp)
        .navigationBarsPadding()
    ) {
      Text(
        text = "Update Status Cuking",
        style = MaterialTheme.typography.titleLarge,
        fontWeight = FontWeight.Bold,
        color = MaterialTheme.colorScheme.onSurface
      )
      Spacer(modifier = Modifier.height(4.dp))
      Text(
        text = "Pilih kondisi terbaru untuk membantu sesama relawan.",
        style = MaterialTheme.typography.bodySmall,
        color = MaterialTheme.colorScheme.onSurfaceVariant
      )

      Spacer(modifier = Modifier.height(16.dp))

      Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        listOf(
          CatStatus.HANDLED to "Sudah Ditangani / Diberi Makan / Dirawat",
          CatStatus.HEALTHY to "Sehat dan Aman",
          CatStatus.NEED_ATTENTION to "Masih Butuh Perhatian",
          CatStatus.INJURED to "Terluka / Butuh Evakuasi"
        ).forEach { (status, desc) ->
          val isSelected = selectedStatus == status
          Surface(
            onClick = { selectedStatus = status },
            shape = RoundedCornerShape(14.dp),
            color = if (isSelected) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f),
            modifier = Modifier.fillMaxWidth()
          ) {
            Row(
              modifier = Modifier.padding(14.dp),
              verticalAlignment = Alignment.CenterVertically
            ) {
              Icon(
                imageVector = if (isSelected) Icons.Default.CheckCircle else Icons.Default.Pets,
                contentDescription = null,
                tint = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
              )
              Spacer(modifier = Modifier.width(10.dp))
              Column {
                Text(
                  text = status.displayName,
                  fontWeight = FontWeight.Bold,
                  style = MaterialTheme.typography.titleSmall
                )
                Text(
                  text = desc,
                  style = MaterialTheme.typography.bodySmall,
                  color = MaterialTheme.colorScheme.onSurfaceVariant
                )
              }
            }
          }
        }
      }

      Spacer(modifier = Modifier.height(16.dp))

      OutlinedTextField(
        value = notes,
        onValueChange = { notes = it },
        placeholder = { Text("Tulis catatan update (misal: sudah diberi wetfood & air bersih)...") },
        minLines = 2,
        maxLines = 4,
        shape = RoundedCornerShape(16.dp),
        modifier = Modifier.fillMaxWidth()
      )

      Spacer(modifier = Modifier.height(20.dp))

      Button(
        onClick = { onSubmit(selectedStatus, notes) },
        shape = RoundedCornerShape(16.dp),
        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
        modifier = Modifier
          .fillMaxWidth()
          .height(50.dp)
      ) {
        Text("Simpan Update (+25 XP)", fontWeight = FontWeight.Bold)
      }
    }
  }
}
