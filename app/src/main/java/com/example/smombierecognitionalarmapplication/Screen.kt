package com.example.smombierecognitionalarmapplication

import androidx.annotation.DrawableRes

enum class Screen(val route: String, @DrawableRes val icon: Int) {
    Home("home", R.drawable.ic_home),
    Settings("settings", R.drawable.ic_settings),
    Map("map", R.drawable.ic_map)
}
