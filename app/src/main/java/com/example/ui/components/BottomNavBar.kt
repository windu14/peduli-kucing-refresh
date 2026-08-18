package com.example.ui.components

import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddAPhoto
import androidx.compose.material.icons.filled.Explore
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.outlined.AddAPhoto
import androidx.compose.material.icons.outlined.Explore
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import com.example.ui.navigation.Screen

data class NavItem(
  val route: String,
  val label: String,
  val selectedIcon: ImageVector,
  val unselectedIcon: ImageVector
)

@Composable
fun PeduliCukingBottomBar(
  currentRoute: String?,
  onNavigate: (String) -> Unit,
  modifier: Modifier = Modifier
) {
  val items = listOf(
    NavItem(
      route = Screen.Home.route,
      label = "Home",
      selectedIcon = Icons.Filled.Home,
      unselectedIcon = Icons.Outlined.Home
    ),
    NavItem(
      route = Screen.Explore.route,
      label = "Jelajah",
      selectedIcon = Icons.Filled.Explore,
      unselectedIcon = Icons.Outlined.Explore
    ),
    NavItem(
      route = Screen.Camera.route,
      label = "Tambah Cuking",
      selectedIcon = Icons.Filled.AddAPhoto,
      unselectedIcon = Icons.Outlined.AddAPhoto
    ),
    NavItem(
      route = Screen.Profile.route,
      label = "Profil",
      selectedIcon = Icons.Filled.Person,
      unselectedIcon = Icons.Outlined.Person
    )
  )

  NavigationBar(
    containerColor = MaterialTheme.colorScheme.surface,
    tonalElevation = 6.dp,
    modifier = modifier.windowInsetsPadding(WindowInsets.navigationBars)
  ) {
    items.forEach { item ->
      val isSelected = currentRoute == item.route

      NavigationBarItem(
        selected = isSelected,
        onClick = { onNavigate(item.route) },
        icon = {
          Icon(
            imageVector = if (isSelected) item.selectedIcon else item.unselectedIcon,
            contentDescription = item.label,
            modifier = Modifier.size(24.dp)
          )
        },
        label = {
          Text(
            text = item.label,
            style = MaterialTheme.typography.labelMedium
          )
        },
        colors = NavigationBarItemDefaults.colors(
          selectedIconColor = MaterialTheme.colorScheme.onPrimaryContainer,
          indicatorColor = MaterialTheme.colorScheme.primaryContainer,
          selectedTextColor = MaterialTheme.colorScheme.primary,
          unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
          unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant
        )
      )
    }
  }
}
