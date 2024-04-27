package com.example.smombierecognitionalarmapplication.ui.screens

import androidx.compose.foundation.layout.Row
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment

@Composable
fun SettingsScreen() {
    val (isChecked, setChecked) = remember { mutableStateOf(false) }

    Row(verticalAlignment = Alignment.CenterVertically) {
        Text("알림")
        Switch(
            checked = isChecked,
            onCheckedChange = { setChecked(it) }
        )
    }
}