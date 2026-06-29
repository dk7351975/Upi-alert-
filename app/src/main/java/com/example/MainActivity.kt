package com.example

import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.app.NotificationManagerCompat
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.*
import com.example.ui.MainViewModel
import com.example.ui.components.AdBanner
import com.example.ui.screens.AuthScreen
import com.example.ui.screens.DashboardScreen
import com.example.ui.screens.HistoryScreen
import com.example.ui.screens.ProfileScreen
import com.example.ui.theme.MyApplicationTheme
import com.google.android.gms.ads.MobileAds
import androidx.compose.material.icons.filled.Person

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        // Initialize AdMob
        MobileAds.initialize(this) {}
        
        enableEdgeToEdge()
        val app = application as MyApplication
        val viewModel: MainViewModel by viewModels { MainViewModel.Factory(app.repository, app.settingsRepository, app.authManager) }
        
        setContent {
            MyApplicationTheme {
                MainApp(viewModel)
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainApp(viewModel: MainViewModel) {
    val navController = rememberNavController()
    val isLoggedIn by viewModel.isLoggedIn.collectAsState()
    val mpin by viewModel.mpin.collectAsState()
    var isUnlockedSession by remember { mutableStateOf(false) }
    
    val context = LocalContext.current
    
    // Check if notification permission is granted
    val isNotificationPermissionGranted = remember {
        NotificationManagerCompat.getEnabledListenerPackages(context).contains(context.packageName)
    }

    val language by viewModel.language.collectAsState()
    com.example.utils.AppStrings.currentLang = language

    if (!isLoggedIn) {
        AuthScreen(onLoginSuccess = { 
            viewModel.setLoggedIn(true) 
            isUnlockedSession = true
        })
    } else if (mpin == null) {
        com.example.ui.screens.SetMpinScreen(onMpinSet = { newPin -> 
            viewModel.setMpin(newPin)
            isUnlockedSession = true
        })
    } else if (!isUnlockedSession) {
        com.example.ui.screens.UnlockMpinScreen(
            correctMpin = mpin!!,
            onUnlockSuccess = { isUnlockedSession = true },
            onForgotPin = {
                viewModel.setMpin(null)
                viewModel.setLoggedIn(false)
            }
        )
    } else {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text(com.example.utils.AppStrings.upiAlert) },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = MaterialTheme.colorScheme.primaryContainer,
                        titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer
                    ),
                    actions = {
                        IconButton(onClick = { navController.navigate("profile") }) {
                            Icon(androidx.compose.material.icons.Icons.Filled.Person, contentDescription = "Profile")
                        }
                    }
                )
            },
            bottomBar = {
                NavigationBar {
                    val navBackStackEntry by navController.currentBackStackEntryAsState()
                    val currentDestination = navBackStackEntry?.destination

                    NavigationBarItem(
                        icon = { Icon(Icons.Filled.Home, contentDescription = "Dashboard") },
                        label = { Text(com.example.utils.AppStrings.dashboard) },
                        selected = currentDestination?.hierarchy?.any { it.route == "dashboard" } == true,
                        onClick = {
                            navController.navigate("dashboard") {
                                popUpTo(navController.graph.findStartDestination().id) {
                                    saveState = true
                                }
                                launchSingleTop = true
                                restoreState = true
                            }
                        }
                    )
                    NavigationBarItem(
                        icon = { Icon(Icons.Filled.List, contentDescription = "History") },
                        label = { Text(com.example.utils.AppStrings.history) },
                        selected = currentDestination?.hierarchy?.any { it.route == "history" } == true,
                        onClick = {
                            navController.navigate("history") {
                                popUpTo(navController.graph.findStartDestination().id) {
                                    saveState = true
                                }
                                launchSingleTop = true
                                restoreState = true
                            }
                        }
                    )
                }
            }
        ) { innerPadding ->
            Column(modifier = Modifier.padding(innerPadding).fillMaxSize()) {
                if (!isNotificationPermissionGranted) {
                    Card(
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.errorContainer),
                        modifier = Modifier.fillMaxWidth().padding(16.dp)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text("Notification Access Required", color = MaterialTheme.colorScheme.onErrorContainer, style = MaterialTheme.typography.titleMedium)
                            Spacer(modifier = Modifier.height(8.dp))
                            Text("Please enable notification access in settings for the app to detect payments.", color = MaterialTheme.colorScheme.onErrorContainer)
                            Spacer(modifier = Modifier.height(8.dp))
                            Button(onClick = {
                                val intent = Intent(Settings.ACTION_NOTIFICATION_LISTENER_SETTINGS)
                                context.startActivity(intent)
                            }) {
                                Text("Enable")
                            }
                        }
                    }
                }
                
                Box(modifier = Modifier.weight(1f)) {
                    NavHost(navController = navController, startDestination = "dashboard") {
                        composable("dashboard") { DashboardScreen(viewModel) }
                        composable("history") { HistoryScreen(viewModel) }
                        composable("profile") { com.example.ui.screens.ProfileScreen(viewModel, onLogout = {
                            isUnlockedSession = false
                        }) }
                    }
                }
                
                // Banner Ad at the bottom of content
                AdBanner()
            }
        }
    }
}
