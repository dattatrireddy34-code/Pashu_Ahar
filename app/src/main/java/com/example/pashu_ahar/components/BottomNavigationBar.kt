package com.example.pashu_ahar.components

import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.pashu_ahar.ui.theme.DarkGreen

@Composable
fun BottomNavigationBar(
    currentRoute: String,
    onHomeClick: () -> Unit,
    onDiseaseClick: () -> Unit,
    onAddCowClick: () -> Unit,
    onCostTrackerClick: () -> Unit,
    onProfileClick: () -> Unit
) {
    NavigationBar(containerColor = Color.White) {
        NavigationBarItem(
            icon = { Icon(Icons.Default.Home, contentDescription = null) },
            label = { Text("Home", fontSize = 10.sp) },
            selected = currentRoute == "home",
            onClick = onHomeClick,
            colors = NavigationBarItemDefaults.colors(selectedIconColor = DarkGreen, selectedTextColor = DarkGreen)
        )
        NavigationBarItem(
            icon = { Icon(Icons.Default.Eco, contentDescription = null) },
            label = { Text("Disease", fontSize = 10.sp) },
            selected = currentRoute == "disease",
            onClick = onDiseaseClick,
            colors = NavigationBarItemDefaults.colors(selectedIconColor = DarkGreen, selectedTextColor = DarkGreen)
        )
        NavigationBarItem(
            icon = { Icon(Icons.Default.AddCircle, contentDescription = null, modifier = Modifier.size(32.dp)) },
            label = { Text("Add Cow", fontSize = 10.sp) },
            selected = currentRoute == "add_cow",
            onClick = onAddCowClick,
            colors = NavigationBarItemDefaults.colors(unselectedIconColor = DarkGreen, selectedIconColor = DarkGreen)
        )
        NavigationBarItem(
            icon = { Icon(Icons.Default.Calculate, contentDescription = null) },
            label = { Text("Cost Tracker", fontSize = 10.sp) },
            selected = currentRoute == "cost",
            onClick = onCostTrackerClick,
            colors = NavigationBarItemDefaults.colors(selectedIconColor = DarkGreen, selectedTextColor = DarkGreen)
        )
        NavigationBarItem(
            icon = { Icon(Icons.Default.Person, contentDescription = null) },
            label = { Text("Profile", fontSize = 10.sp) },
            selected = currentRoute == "profile",
            onClick = onProfileClick,
            colors = NavigationBarItemDefaults.colors(selectedIconColor = DarkGreen, selectedTextColor = DarkGreen)
        )
    }
}
