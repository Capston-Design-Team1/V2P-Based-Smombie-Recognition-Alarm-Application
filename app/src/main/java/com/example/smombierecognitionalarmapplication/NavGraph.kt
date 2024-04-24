package com.example.smombierecognitionalarmapplication

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.navigation
import com.example.smombierecognitionalarmapplication.HomeScreen
import com.example.smombierecognitionalarmapplication.MapScreen
import com.example.smombierecognitionalarmapplication.SettingsScreen

@Composable
fun AppNavGraph(navController: NavHostController) {
    NavHost(navController = navController, startDestination = "home") {
        composable("home") { HomeScreen() }
        composable("settings") { SettingsScreen() }
        composable("map") { MapScreen() }
        navigation(startDestination = "bottomNavScreen", route = "bottomNav") {
            composable("bottomNavScreen") { BottomNavScreen(navController) }
        }
    }
}
