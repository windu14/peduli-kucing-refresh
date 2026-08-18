package com.example.ui.navigation

import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.data.local.database.DatabaseProvider
import com.example.data.repository.CatRepository
import com.example.data.repository.UserRepository
import com.example.ui.camera.CameraCaptureScreen
import com.example.ui.camera.DiscoveryFormScreen
import com.example.ui.camera.DiscoveryViewModel
import com.example.ui.collection.CollectionScreen
import com.example.ui.collection.CollectionViewModel
import com.example.ui.components.GamificationFeedbackDialog
import com.example.ui.components.PeduliCukingBottomBar
import com.example.ui.detail.CatDetailScreen
import com.example.ui.detail.CatDetailViewModel
import com.example.ui.explore.ExploreMapScreen
import com.example.ui.explore.ExploreViewModel
import com.example.ui.home.HomeScreen
import com.example.ui.home.HomeViewModel
import com.example.ui.onboarding.OnboardingScreen
import com.example.ui.profile.ProfileScreen
import com.example.ui.profile.ProfileViewModel

@Composable
fun AppNavigation(
  navController: NavHostController = rememberNavController()
) {
  val context = LocalContext.current
  val database = remember { DatabaseProvider.getDatabase(context) }
  val userRepository = remember { UserRepository(database.userDao(), database.achievementDao()) }
  val catRepository = remember { CatRepository(database.catDao(), database.discoveryDao(), userRepository) }

  val navBackStackEntry by navController.currentBackStackEntryAsState()
  val currentRoute = navBackStackEntry?.destination?.route

  val showBottomBar = currentRoute in listOf(
    Screen.Home.route,
    Screen.Explore.route,
    Screen.Profile.route
  )

  var showGamificationDialog by remember { mutableStateOf(false) }
  var dialogXp by remember { mutableStateOf(40) }
  var dialogTitle by remember { mutableStateOf("Cuking Berhasil Ditandai!") }
  var dialogMsg by remember { mutableStateOf("Dokumentasi kamu membantu komunitas menjaga kucing ini.") }

  Scaffold(
    bottomBar = {
      if (showBottomBar) {
        PeduliCukingBottomBar(
          currentRoute = currentRoute,
          onNavigate = { route ->
            if (route == Screen.Camera.route) {
              navController.navigate(route)
            } else {
              navController.navigate(route) {
                popUpTo(Screen.Home.route) {
                  saveState = true
                }
                launchSingleTop = true
                restoreState = true
              }
            }
          }
        )
      }
    }
  ) { paddingValues ->
    NavHost(
      navController = navController,
      startDestination = Screen.Home.route,
      modifier = Modifier
        .fillMaxSize()
        .padding(paddingValues),
      enterTransition = { fadeIn() },
      exitTransition = { fadeOut() }
    ) {
      composable(Screen.Onboarding.route) {
        OnboardingScreen(
          onFinishOnboarding = {
            navController.navigate(Screen.Home.route) {
              popUpTo(Screen.Onboarding.route) { inclusive = true }
            }
          }
        )
      }

      composable(Screen.Home.route) {
        val homeViewModel: HomeViewModel = viewModel(
          factory = SimpleViewModelFactory { HomeViewModel(catRepository, userRepository) }
        )
        HomeScreen(
          viewModel = homeViewModel,
          onNavigateToCamera = { navController.navigate(Screen.Camera.route) },
          onNavigateToExplore = { navController.navigate(Screen.Explore.route) },
          onNavigateToCollection = { navController.navigate(Screen.Collection.route) },
          onNavigateToCatDetail = { catId -> navController.navigate(Screen.CatDetail.createRoute(catId)) },
          onNavigateToProfile = { navController.navigate(Screen.Profile.route) }
        )
      }

      composable(Screen.Explore.route) {
        val exploreViewModel: ExploreViewModel = viewModel(
          factory = SimpleViewModelFactory { ExploreViewModel(catRepository) }
        )
        ExploreMapScreen(
          viewModel = exploreViewModel,
          onNavigateToCatDetail = { catId -> navController.navigate(Screen.CatDetail.createRoute(catId)) },
          onNavigateToCamera = { navController.navigate(Screen.Camera.route) }
        )
      }

      composable(Screen.Camera.route) {
        CameraCaptureScreen(
          onPhotoCaptured = { photoUri ->
            navController.navigate(Screen.DiscoveryForm.createRoute(android.net.Uri.encode(photoUri)))
          },
          onNavigateBack = { navController.popBackStack() }
        )
      }

      composable(
        route = Screen.DiscoveryForm.route,
        arguments = listOf(navArgument("photoUri") {
          type = NavType.StringType
          defaultValue = ""
        })
      ) { backStackEntry ->
        val photoUri = backStackEntry.arguments?.getString("photoUri") ?: ""
        val discoveryViewModel: DiscoveryViewModel = viewModel(
          factory = SimpleViewModelFactory { DiscoveryViewModel(catRepository, userRepository) }
        )
        DiscoveryFormScreen(
          photoUri = photoUri,
          viewModel = discoveryViewModel,
          onNavigateBack = { navController.popBackStack() },
          onDiscoverySuccess = { catId, xpGained ->
            dialogXp = xpGained
            dialogTitle = "Cuking Berhasil Ditandai! 🐾"
            dialogMsg = "Terima kasih sudah peduli! Jejak keberadaan cuking sudah tercatat di peta."
            showGamificationDialog = true
            navController.navigate(Screen.Home.route) {
              popUpTo(Screen.Home.route) { inclusive = false }
              launchSingleTop = true
            }
          }
        )
      }

      composable(
        route = Screen.CatDetail.route,
        arguments = listOf(navArgument("catId") {
          type = NavType.LongType
          defaultValue = 1L
        })
      ) { backStackEntry ->
        val catId = backStackEntry.arguments?.getLong("catId") ?: 1L
        val detailViewModel: CatDetailViewModel = viewModel(
          key = "cat_detail_$catId",
          factory = SimpleViewModelFactory { CatDetailViewModel(catId, catRepository) }
        )
        CatDetailScreen(
          viewModel = detailViewModel,
          onNavigateBack = { navController.popBackStack() },
          onOpenMap = { lat, lng ->
            navController.navigate(Screen.Explore.route)
          }
        )
      }

      composable(Screen.Profile.route) {
        val profileViewModel: ProfileViewModel = viewModel(
          factory = SimpleViewModelFactory { ProfileViewModel(userRepository) }
        )
        ProfileScreen(
          viewModel = profileViewModel,
          onNavigateToCollection = { navController.navigate(Screen.Collection.route) }
        )
      }

      composable(Screen.Collection.route) {
        val collectionViewModel: CollectionViewModel = viewModel(
          factory = SimpleViewModelFactory { CollectionViewModel(catRepository) }
        )
        CollectionScreen(
          viewModel = collectionViewModel,
          onNavigateBack = { navController.popBackStack() },
          onNavigateToCatDetail = { catId -> navController.navigate(Screen.CatDetail.createRoute(catId)) }
        )
      }
    }

    GamificationFeedbackDialog(
      show = showGamificationDialog,
      xpGained = dialogXp,
      title = dialogTitle,
      message = dialogMsg,
      onDismiss = { showGamificationDialog = false }
    )
  }
}

class SimpleViewModelFactory<T : androidx.lifecycle.ViewModel>(
  private val creator: () -> T
) : androidx.lifecycle.ViewModelProvider.Factory {
  @Suppress("UNCHECKED_CAST")
  override fun <VM : androidx.lifecycle.ViewModel> create(modelClass: Class<VM>): VM {
    return creator() as VM
  }
}
