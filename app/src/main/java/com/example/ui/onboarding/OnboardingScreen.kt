package com.example.ui.onboarding

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Pets
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.components.ProceduralCatCharacter
import com.example.ui.theme.PeachSecondary
import com.example.ui.theme.SagePrimary
import kotlinx.coroutines.launch

data class OnboardingPageData(
  val icon: ImageVector,
  val iconBg: Color,
  val iconColor: Color,
  val title: String,
  val subtitle: String,
  val catCharacterName: String
)

@Composable
fun OnboardingScreen(
  onFinishOnboarding: () -> Unit
) {
  val pages = listOf(
    OnboardingPageData(
      icon = Icons.Default.Pets,
      iconBg = Color(0xFFD6EAE0),
      iconColor = Color(0xFF3F6E5A),
      title = "Kenalan dengan\nPeduli Cuking",
      subtitle = "Temukan dan dokumentasikan cuking yang kamu temui di jalanan sekitar kamu.",
      catCharacterName = "milo"
    ),
    OnboardingPageData(
      icon = Icons.Default.LocationOn,
      iconBg = Color(0xFFFFDBCF),
      iconColor = Color(0xFFD96B4F),
      title = "Tandai Lokasinya",
      subtitle = "Bantu orang lain dan komunitas mengetahui keberadaan cuking di sekitar mereka.",
      catCharacterName = "belang"
    ),
    OnboardingPageData(
      icon = Icons.Default.Favorite,
      iconBg = Color(0xFFEBE6F8),
      iconColor = Color(0xFF75689E),
      title = "Setiap Penemuan\nSangat Berarti",
      subtitle = "Dapatkan XP dan achievement sambil menyebarkan kebaikan untuk kucing jalanan.",
      catCharacterName = "oyen"
    )
  )

  val pagerState = rememberPagerState(pageCount = { pages.size })
  val scope = rememberCoroutineScope()

  Column(
    modifier = Modifier
      .fillMaxSize()
      .background(MaterialTheme.colorScheme.background)
      .statusBarsPadding()
      .padding(24.dp),
    horizontalAlignment = Alignment.CenterHorizontally
  ) {
    // Skip Button
    Row(
      modifier = Modifier.fillMaxWidth(),
      horizontalArrangement = Arrangement.End
    ) {
      if (pagerState.currentPage < pages.size - 1) {
        TextButton(onClick = onFinishOnboarding) {
          Text(
            text = "Lewati",
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            fontWeight = FontWeight.Medium
          )
        }
      } else {
        Spacer(modifier = Modifier.height(48.dp))
      }
    }

    Spacer(modifier = Modifier.height(16.dp))

    // Pager Content
    HorizontalPager(
      state = pagerState,
      modifier = Modifier
        .weight(1f)
        .fillMaxWidth()
    ) { pageIndex ->
      val page = pages[pageIndex]
      Column(
        modifier = Modifier
          .fillMaxSize()
          .padding(horizontal = 8.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
      ) {
        // Illustrated Cat Preview Card
        Surface(
          shape = RoundedCornerShape(32.dp),
          color = MaterialTheme.colorScheme.surface,
          shadowElevation = 4.dp,
          modifier = Modifier.size(240.dp)
        ) {
          Box(modifier = Modifier.fillMaxSize()) {
            ProceduralCatCharacter(
              nickname = page.catCharacterName,
              modifier = Modifier.fillMaxSize()
            )

            // Top-left Floating Badge
            Surface(
              shape = CircleShape,
              color = page.iconBg,
              modifier = Modifier
                .align(Alignment.TopStart)
                .padding(14.dp)
                .size(40.dp)
            ) {
              Box(contentAlignment = Alignment.Center) {
                Icon(
                  imageVector = page.icon,
                  contentDescription = null,
                  tint = page.iconColor,
                  modifier = Modifier.size(20.dp)
                )
              }
            }
          }
        }

        Spacer(modifier = Modifier.height(36.dp))

        Text(
          text = page.title,
          style = MaterialTheme.typography.headlineLarge,
          fontWeight = FontWeight.Bold,
          textAlign = TextAlign.Center,
          color = MaterialTheme.colorScheme.onBackground,
          lineHeight = 32.sp
        )

        Spacer(modifier = Modifier.height(12.dp))

        Text(
          text = page.subtitle,
          style = MaterialTheme.typography.bodyLarge,
          textAlign = TextAlign.Center,
          color = MaterialTheme.colorScheme.onSurfaceVariant,
          modifier = Modifier.padding(horizontal = 16.dp)
        )
      }
    }

    // Pager Indicator & CTA Button
    Column(
      modifier = Modifier.fillMaxWidth(),
      horizontalAlignment = Alignment.CenterHorizontally
    ) {
      Row(
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.padding(bottom = 24.dp)
      ) {
        repeat(pages.size) { index ->
          val isSelected = pagerState.currentPage == index
          Box(
            modifier = Modifier
              .padding(horizontal = 4.dp)
              .height(8.dp)
              .width(if (isSelected) 24.dp else 8.dp)
              .clip(CircleShape)
              .background(
                if (isSelected) MaterialTheme.colorScheme.primary
                else MaterialTheme.colorScheme.outlineVariant
              )
          )
        }
      }

      Button(
        onClick = {
          if (pagerState.currentPage < pages.size - 1) {
            scope.launch {
              pagerState.animateScrollToPage(pagerState.currentPage + 1)
            }
          } else {
            onFinishOnboarding()
          }
        },
        shape = RoundedCornerShape(20.dp),
        colors = ButtonDefaults.buttonColors(
          containerColor = MaterialTheme.colorScheme.primary
        ),
        modifier = Modifier
          .fillMaxWidth()
          .height(54.dp)
      ) {
        Text(
          text = if (pagerState.currentPage == pages.size - 1) "Mulai Sekarang" else "Lanjutkan",
          style = MaterialTheme.typography.titleMedium,
          fontWeight = FontWeight.SemiBold
        )
      }
    }
  }
}
