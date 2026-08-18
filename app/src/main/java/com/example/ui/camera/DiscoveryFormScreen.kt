package com.example.ui.camera

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.EditLocation
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Help
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Pets
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.model.CatCondition
import com.example.data.model.CatOwnership
import com.example.data.model.CatPhysique
import com.example.ui.components.CatAvatarImage
import com.example.ui.components.MascotAvatarSelector
import com.example.ui.components.MascotAvatarView
import com.example.ui.components.MascotCatalog
import com.example.ui.theme.PeachSecondary
import com.example.ui.theme.SagePrimary
import com.example.ui.theme.StatusHealthy
import com.example.ui.theme.StatusInjured
import com.example.ui.theme.StatusNeedHelp

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun DiscoveryFormScreen(
  photoUri: String,
  viewModel: DiscoveryViewModel,
  onNavigateBack: () -> Unit,
  onDiscoverySuccess: (Long, Int) -> Unit
) {
  val uiState by viewModel.uiState.collectAsStateWithLifecycle()

  LaunchedEffect(photoUri) {
    if (photoUri.isNotBlank()) {
      viewModel.initPhoto(photoUri)
    }
  }

  val popularColors = listOf(
    "Oranye / Oyen",
    "Belang Tiga / Calico",
    "Hitam Putih / Tuxedo",
    "Hitam Pekat",
    "Putih Bersih",
    "Abu-abu",
    "Belang Macan (Tabby)",
    "Tortoiseshell (Kembang)"
  )

  Column(
    modifier = Modifier
      .fillMaxSize()
      .background(MaterialTheme.colorScheme.background)
  ) {
    // Top App Bar
    Row(
      modifier = Modifier
        .fillMaxWidth()
        .statusBarsPadding()
        .padding(horizontal = 16.dp, vertical = 8.dp),
      verticalAlignment = Alignment.CenterVertically
    ) {
      IconButton(onClick = onNavigateBack) {
        Icon(
          imageVector = Icons.Default.ArrowBack,
          contentDescription = "Kembali",
          tint = MaterialTheme.colorScheme.onBackground
        )
      }
      Spacer(modifier = Modifier.width(8.dp))
      Column {
        Text(
          text = "Formulir Cuking Baru",
          style = MaterialTheme.typography.titleLarge,
          fontWeight = FontWeight.Bold,
          color = MaterialTheme.colorScheme.onBackground
        )
        Text(
          text = "Isi detail cuking yang kamu temukan",
          style = MaterialTheme.typography.bodySmall,
          color = MaterialTheme.colorScheme.onSurfaceVariant
        )
      }
    }

    LazyColumn(
      modifier = Modifier
        .weight(1f)
        .fillMaxWidth(),
      contentPadding = PaddingValues(horizontal = 20.dp, vertical = 12.dp),
      verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {
      // 1. Photo & Selected Mascot Preview Card
      item {
        Card(
          shape = RoundedCornerShape(24.dp),
          colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
          elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
          modifier = Modifier.fillMaxWidth()
        ) {
          Column(modifier = Modifier.padding(16.dp)) {
            Row(
              modifier = Modifier.fillMaxWidth(),
              verticalAlignment = Alignment.CenterVertically
            ) {
              // Photo Thumbnail
              Box(
                modifier = Modifier
                  .size(88.dp)
                  .clip(RoundedCornerShape(18.dp))
                  .background(MaterialTheme.colorScheme.surfaceVariant)
              ) {
                CatAvatarImage(
                  nickname = uiState.nickname.ifBlank { "cuking" },
                  photoUri = uiState.photoUri.ifBlank { "asset://cat_milo.png" },
                  modifier = Modifier.fillMaxSize(),
                  shapeRadiusDp = 18
                )
              }

              Spacer(modifier = Modifier.width(16.dp))

              // Avatar Mascot Indicator
              Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                  MascotAvatarView(
                    mascotId = uiState.avatarPresetId,
                    size = 38.dp
                  )
                  Spacer(modifier = Modifier.width(8.dp))
                  Column {
                    Text(
                      text = MascotCatalog.getById(uiState.avatarPresetId).name,
                      style = MaterialTheme.typography.titleMedium,
                      fontWeight = FontWeight.Bold
                    )
                    Text(
                      text = "Ikon Peta & Profil",
                      style = MaterialTheme.typography.labelSmall,
                      color = MaterialTheme.colorScheme.primary
                    )
                  }
                }
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                  text = "Foto berhasil disimpan. Silakan lengkapi info di bawah:",
                  style = MaterialTheme.typography.bodySmall,
                  color = MaterialTheme.colorScheme.onSurfaceVariant
                )
              }
            }
          }
        }
      }

      // 2. Mascot Avatar Selector
      item {
        MascotAvatarSelector(
          selectedId = uiState.avatarPresetId,
          onSelect = { preset ->
            viewModel.updateAvatarPreset(preset.id)
          }
        )
      }

      // 3. Nama Panggilan Cuking
      item {
        Column {
          Text(
            text = "Nama / Panggilan Cuking",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onBackground
          )
          Spacer(modifier = Modifier.height(6.dp))
          OutlinedTextField(
            value = uiState.nickname,
            onValueChange = { viewModel.updateNickname(it) },
            placeholder = { Text("Contoh: Si Oyen, Belang, Milo, Si Bobo") },
            singleLine = true,
            shape = RoundedCornerShape(16.dp),
            colors = OutlinedTextFieldDefaults.colors(
              focusedBorderColor = MaterialTheme.colorScheme.primary,
              unfocusedBorderColor = MaterialTheme.colorScheme.outlineVariant,
              focusedContainerColor = MaterialTheme.colorScheme.surface,
              unfocusedContainerColor = MaterialTheme.colorScheme.surface
            ),
            modifier = Modifier.fillMaxWidth()
          )
        }
      }

      // 4. Warna / Corak Cuking
      item {
        Column {
          Text(
            text = "Warna / Corak Bulu",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onBackground
          )
          Spacer(modifier = Modifier.height(8.dp))

          FlowRow(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.fillMaxWidth()
          ) {
            popularColors.forEach { colorName ->
              val isSelected = uiState.colorPattern == colorName
              FilterChip(
                selected = isSelected,
                onClick = { viewModel.updateColorPattern(colorName) },
                label = { Text(colorName) },
                shape = RoundedCornerShape(12.dp),
                colors = FilterChipDefaults.filterChipColors(
                  selectedContainerColor = MaterialTheme.colorScheme.primaryContainer,
                  selectedLabelColor = MaterialTheme.colorScheme.onPrimaryContainer
                ),
                border = FilterChipDefaults.filterChipBorder(
                  enabled = true,
                  selected = isSelected,
                  borderColor = MaterialTheme.colorScheme.outlineVariant
                )
              )
            }
          }

          Spacer(modifier = Modifier.height(8.dp))
          OutlinedTextField(
            value = uiState.colorPattern,
            onValueChange = { viewModel.updateColorPattern(it) },
            placeholder = { Text("Ketik warna/corak lainnya...") },
            singleLine = true,
            shape = RoundedCornerShape(14.dp),
            colors = OutlinedTextFieldDefaults.colors(
              focusedBorderColor = MaterialTheme.colorScheme.primary,
              unfocusedBorderColor = MaterialTheme.colorScheme.outlineVariant,
              focusedContainerColor = MaterialTheme.colorScheme.surface,
              unfocusedContainerColor = MaterialTheme.colorScheme.surface
            ),
            modifier = Modifier.fillMaxWidth()
          )
        }
      }

      // 5. Detail / Deskripsi Cuking
      item {
        Column {
          Text(
            text = "Detail & Deskripsi Cuking",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onBackground
          )
          Text(
            text = "Ceritakan ciri khas, kebiasaan, atau situasi di sekitar saat ditemukan",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
          )
          Spacer(modifier = Modifier.height(6.dp))
          OutlinedTextField(
            value = uiState.description,
            onValueChange = { viewModel.updateDescription(it) },
            placeholder = { Text("Contoh: Sering istirahat dekat teras toko, ramah saat diberi makan...") },
            minLines = 3,
            maxLines = 5,
            shape = RoundedCornerShape(16.dp),
            colors = OutlinedTextFieldDefaults.colors(
              focusedBorderColor = MaterialTheme.colorScheme.primary,
              unfocusedBorderColor = MaterialTheme.colorScheme.outlineVariant,
              focusedContainerColor = MaterialTheme.colorScheme.surface,
              unfocusedContainerColor = MaterialTheme.colorScheme.surface
            ),
            modifier = Modifier.fillMaxWidth()
          )
        }
      }

      // 6. Kondisi Kesehatan Cuking (sehat, sakit, prihatin, nampak tidak sehat, sedang)
      item {
        Column {
          Text(
            text = "Kondisi Kesehatan",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onBackground
          )
          Text(
            text = "Pilih kondisi kesehatan cuking saat kamu temukan",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
          )
          Spacer(modifier = Modifier.height(10.dp))

          Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            ConditionCardOption(
              title = "Sehat",
              subtitle = "Lincah, bersih, mata cerah, tidak ada tanda luka.",
              badgeColor = Color(0xFF2E7D32),
              isSelected = uiState.condition == CatCondition.SEHAT,
              onClick = { viewModel.updateCondition(CatCondition.SEHAT) }
            )
            ConditionCardOption(
              title = "Sedang",
              subtitle = "Kondisi umum cukup stabil, agak pasif/mengantuk.",
              badgeColor = Color(0xFFF57C00),
              isSelected = uiState.condition == CatCondition.SEDANG,
              onClick = { viewModel.updateCondition(CatCondition.SEDANG) }
            )
            ConditionCardOption(
              title = "Nampak Tidak Sehat",
              subtitle = "Kusam, lesu, mata berair, atau butuh makanan/minum.",
              badgeColor = Color(0xFFE65100),
              isSelected = uiState.condition == CatCondition.NAMPAK_TIDAK_SEHAT,
              onClick = { viewModel.updateCondition(CatCondition.NAMPAK_TIDAK_SEHAT) }
            )
            ConditionCardOption(
              title = "Sakit",
              subtitle = "Demam, flu, lemas, ada keluhan penyakit terlihat.",
              badgeColor = Color(0xFFC62828),
              isSelected = uiState.condition == CatCondition.SAKIT,
              onClick = { viewModel.updateCondition(CatCondition.SAKIT) }
            )
            ConditionCardOption(
              title = "Prihatin",
              subtitle = "Terluka parah, kaki pincang berat, butuh pertolongan darurat.",
              badgeColor = Color(0xFFD32F2F),
              isSelected = uiState.condition == CatCondition.PRIHATIN,
              onClick = { viewModel.updateCondition(CatCondition.PRIHATIN) }
            )
          }
        }
      }

      // 7. Kondisi Fisik (gemuk, kurus, ideal)
      item {
        Column {
          Text(
            text = "Kondisi Fisik / Postur",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onBackground
          )
          Spacer(modifier = Modifier.height(10.dp))

          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
          ) {
            CatPhysique.values().forEach { physique ->
              val isSelected = uiState.physique == physique
              Surface(
                onClick = { viewModel.updatePhysique(physique) },
                shape = RoundedCornerShape(16.dp),
                color = if (isSelected) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surface,
                border = if (isSelected) BorderStroke(2.dp, MaterialTheme.colorScheme.primary) else BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f)),
                modifier = Modifier.weight(1f)
              ) {
                Column(
                  modifier = Modifier.padding(vertical = 12.dp, horizontal = 8.dp),
                  horizontalAlignment = Alignment.CenterHorizontally
                ) {
                  Text(
                    text = when (physique) {
                      CatPhysique.GEMUK -> "🐱 Gemuk"
                      CatPhysique.KURUS -> "🐈 Kurus"
                      CatPhysique.IDEAL -> "✨ Ideal"
                    },
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Bold,
                    color = if (isSelected) MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.onSurface
                  )
                  Spacer(modifier = Modifier.height(2.dp))
                  Text(
                    text = physique.description,
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

      // 8. Status Kepemilikan (ada pemilik, kucing liar, tidak jelas)
      item {
        Column {
          Text(
            text = "Status Kepemilikan",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onBackground
          )
          Spacer(modifier = Modifier.height(10.dp))

          Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            CatOwnership.values().forEach { ownership ->
              val isSelected = uiState.ownership == ownership
              Surface(
                onClick = { viewModel.updateOwnership(ownership) },
                shape = RoundedCornerShape(16.dp),
                color = if (isSelected) MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.6f) else MaterialTheme.colorScheme.surface,
                border = if (isSelected) BorderStroke(2.dp, MaterialTheme.colorScheme.secondary) else BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f)),
                modifier = Modifier.fillMaxWidth()
              ) {
                Row(
                  modifier = Modifier
                    .fillMaxWidth()
                    .padding(14.dp),
                  verticalAlignment = Alignment.CenterVertically
                ) {
                  Icon(
                    imageVector = when (ownership) {
                      CatOwnership.ADA_PEMILIK -> Icons.Default.Home
                      CatOwnership.KUCING_LIAR -> Icons.Default.Pets
                      CatOwnership.TIDAK_JELAS -> Icons.Default.Help
                    },
                    contentDescription = null,
                    tint = if (isSelected) MaterialTheme.colorScheme.secondary else MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.size(24.dp)
                  )
                  Spacer(modifier = Modifier.width(12.dp))
                  Column(modifier = Modifier.weight(1f)) {
                    Text(
                      text = ownership.displayName,
                      style = MaterialTheme.typography.bodyLarge,
                      fontWeight = FontWeight.SemiBold,
                      color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                      text = ownership.description,
                      style = MaterialTheme.typography.bodySmall,
                      color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                  }
                  if (isSelected) {
                    Icon(
                      imageVector = Icons.Default.CheckCircle,
                      contentDescription = null,
                      tint = MaterialTheme.colorScheme.secondary,
                      modifier = Modifier.size(20.dp)
                    )
                  }
                }
              }
            }
          }
        }
      }

      // 9. Pilih Titik Lokasi
      item {
        Column {
          Text(
            text = "Titik Lokasi di Peta",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onBackground
          )
          Text(
            text = "Cuking akan disematkan di titik koordinat ini pada peta Jelajah",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
          )
          Spacer(modifier = Modifier.height(10.dp))

          Card(
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
            modifier = Modifier.fillMaxWidth()
          ) {
            Column(modifier = Modifier.padding(16.dp)) {
              Row(verticalAlignment = Alignment.CenterVertically) {
                Surface(
                  shape = CircleShape,
                  color = MaterialTheme.colorScheme.primaryContainer,
                  modifier = Modifier.size(44.dp)
                ) {
                  Box(contentAlignment = Alignment.Center) {
                    Icon(
                      imageVector = Icons.Default.LocationOn,
                      contentDescription = null,
                      tint = MaterialTheme.colorScheme.primary,
                      modifier = Modifier.size(24.dp)
                    )
                  }
                }

                Spacer(modifier = Modifier.width(12.dp))

                Column(modifier = Modifier.weight(1f)) {
                  Text(
                    text = uiState.locationName,
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                  )
                  Text(
                    text = "Lat: ${"%.4f".format(uiState.latitude)}, Lng: ${"%.4f".format(uiState.longitude)}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                  )
                }
              }

              Spacer(modifier = Modifier.height(12.dp))

              OutlinedTextField(
                value = uiState.locationName,
                onValueChange = { viewModel.updateLocation(uiState.latitude, uiState.longitude, it) },
                label = { Text("Nama Tempat / Patokan Jalan") },
                singleLine = true,
                shape = RoundedCornerShape(14.dp),
                modifier = Modifier.fillMaxWidth()
              )
            }
          }
        }
      }

      // 10. Space before bottom bar
      item {
        Spacer(modifier = Modifier.height(16.dp))
      }
    }

    // Bottom Action Bar
    Surface(
      color = MaterialTheme.colorScheme.surface,
      tonalElevation = 8.dp,
      shadowElevation = 8.dp,
      modifier = Modifier
        .fillMaxWidth()
        .navigationBarsPadding()
    ) {
      Column(modifier = Modifier.padding(16.dp)) {
        Button(
          onClick = {
            viewModel.submitDiscovery { newCatId, xpGained ->
              onDiscoverySuccess(newCatId, xpGained)
            }
          },
          enabled = !uiState.isSubmitting,
          shape = RoundedCornerShape(18.dp),
          colors = ButtonDefaults.buttonColors(
            containerColor = MaterialTheme.colorScheme.primary
          ),
          modifier = Modifier
            .fillMaxWidth()
            .height(54.dp)
        ) {
          if (uiState.isSubmitting) {
            CircularProgressIndicator(
              color = Color.White,
              modifier = Modifier.size(24.dp)
            )
          } else {
            Row(verticalAlignment = Alignment.CenterVertically) {
              Icon(
                imageVector = Icons.Default.Pets,
                contentDescription = null,
                modifier = Modifier.size(20.dp)
              )
              Spacer(modifier = Modifier.width(8.dp))
              Text(
                text = "Simpan & Tampilkan di Peta (+50 XP)",
                fontWeight = FontWeight.Bold,
                fontSize = 15.sp
              )
            }
          }
        }
      }
    }
  }
}

@Composable
private fun ConditionCardOption(
  title: String,
  subtitle: String,
  badgeColor: Color,
  isSelected: Boolean,
  onClick: () -> Unit
) {
  Surface(
    onClick = onClick,
    shape = RoundedCornerShape(16.dp),
    color = if (isSelected) badgeColor.copy(alpha = 0.12f) else MaterialTheme.colorScheme.surface,
    border = if (isSelected) BorderStroke(2.dp, badgeColor) else BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f)),
    modifier = Modifier.fillMaxWidth()
  ) {
    Row(
      modifier = Modifier
        .fillMaxWidth()
        .padding(14.dp),
      verticalAlignment = Alignment.CenterVertically
    ) {
      Box(
        modifier = Modifier
          .size(16.dp)
          .clip(CircleShape)
          .background(badgeColor)
      )
      Spacer(modifier = Modifier.width(12.dp))
      Column(modifier = Modifier.weight(1f)) {
        Text(
          text = title,
          style = MaterialTheme.typography.bodyLarge,
          fontWeight = FontWeight.Bold,
          color = if (isSelected) badgeColor else MaterialTheme.colorScheme.onSurface
        )
        Text(
          text = subtitle,
          style = MaterialTheme.typography.bodySmall,
          color = MaterialTheme.colorScheme.onSurfaceVariant
        )
      }
      if (isSelected) {
        Icon(
          imageVector = Icons.Default.CheckCircle,
          contentDescription = null,
          tint = badgeColor,
          modifier = Modifier.size(20.dp)
        )
      }
    }
  }
}
