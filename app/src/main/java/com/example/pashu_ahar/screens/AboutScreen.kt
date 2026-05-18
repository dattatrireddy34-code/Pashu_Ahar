package com.example.pashu_ahar.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Launch
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.pashu_ahar.ui.theme.DarkGreen

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AboutScreen(onBack: () -> Unit) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("About", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = null)
                    }
                }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(32.dp))
            Box(
                modifier = Modifier
                    .size(80.dp)
                    .clip(CircleShape)
                    .background(DarkGreen),
                contentAlignment = Alignment.Center
            ) {
                Text("🐄", fontSize = 40.sp)
            }
            Spacer(modifier = Modifier.height(16.dp))
            Text("Pashu-Aahar", fontSize = 24.sp, fontWeight = FontWeight.Bold, color = DarkGreen)
            Text("Version 1.0.0 (Build 100)", fontSize = 12.sp, color = Color.Gray)
            
            Spacer(modifier = Modifier.height(24.dp))
            Text(
                "Pashu-Aahar is a comprehensive dairy management platform designed to empower farmers with data-driven insights. Our mission is to modernize dairy farming through technology, making it more efficient and profitable.",
                fontSize = 13.sp,
                color = Color.Gray,
                textAlign = TextAlign.Center,
                lineHeight = 20.sp
            )
            
            Spacer(modifier = Modifier.height(32.dp))
            
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceAround) {
                AboutIconItem(Icons.Default.Analytics, "Analytics")
                AboutIconItem(Icons.Default.NotificationsActive, "Reminders")
                AboutIconItem(Icons.Default.CloudSync, "Cloud Sync")
            }
            
            Spacer(modifier = Modifier.height(40.dp))
            
            AboutLinkItem("Terms of Service")
            AboutLinkItem("Privacy Policy")
            AboutLinkItem("Open Source Licenses")
            
            Spacer(modifier = Modifier.weight(1f))
            Text("© 2024 Pashu-Aahar Tech Pvt Ltd.", fontSize = 11.sp, color = Color.Gray)
            Text("All rights reserved.", fontSize = 11.sp, color = Color.Gray)
            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

@Composable
fun AboutIconItem(icon: ImageVector, label: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Box(
            modifier = Modifier
                .size(48.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(Color(0xFFF5F5F5)),
            contentAlignment = Alignment.Center
        ) {
            Icon(icon, contentDescription = null, tint = DarkGreen, modifier = Modifier.size(24.dp))
        }
        Text(label, fontSize = 11.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(top = 8.dp))
    }
}

@Composable
fun AboutLinkItem(title: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(title, modifier = Modifier.weight(1f), fontSize = 14.sp, fontWeight = FontWeight.SemiBold, color = DarkGreen)
        Icon(Icons.AutoMirrored.Filled.Launch, contentDescription = null, tint = Color.LightGray, modifier = Modifier.size(16.dp))
    }
}
