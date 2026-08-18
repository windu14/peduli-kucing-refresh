package com.example.ui.navigation

sealed class Screen(val route: String) {
  object Onboarding : Screen("onboarding")
  object Home : Screen("home")
  object Explore : Screen("explore")
  object Camera : Screen("camera")
  object DiscoveryForm : Screen("discovery_form?photoUri={photoUri}") {
    fun createRoute(photoUri: String): String = "discovery_form?photoUri=$photoUri"
  }
  object CatDetail : Screen("cat_detail/{catId}") {
    fun createRoute(catId: Long): String = "cat_detail/$catId"
  }
  object Profile : Screen("profile")
  object Collection : Screen("collection")
}
