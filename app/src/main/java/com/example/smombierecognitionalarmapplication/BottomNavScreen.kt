package com.example.smombierecognitionalarmapplication
import androidx.compose.material3.BottomNavigation
import androidx.compose.material3.BottomNavigationItem
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.painterResource
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState

@Composable
fun BottomNavScreen(navController: NavHostController) {
    val items = listOf(
        Screen.Home,
        Screen.Settings,
        Screen.Map
    )
    BottomNavigation {
        val currentRoute = navController.currentDestination?.route
        items.forEach { screen ->
            BottomNavigationItem(
                icon = { Icon(painterResource(id = screen.icon), contentDescription = null) },
                selected = currentRoute == screen.route,
                onClick = {
                    // 현재 위치가 아닐 경우 이동
                    if (currentRoute != screen.route) {
                        navController.navigate(screen.route) {
                            // BottomNavigation에서 탭 간 전환 시, 스택을 리셋합니다.
                            popUpTo(navController.graph.startDestinationId)
                            launchSingleTop = true
                        }
                    }
                }
            )
        }
    }
}
