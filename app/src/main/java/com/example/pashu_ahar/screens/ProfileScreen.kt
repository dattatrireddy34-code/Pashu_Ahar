package com.example.pashu_ahar.screens

import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.rememberAsyncImagePainter
import com.example.pashu_ahar.R
import com.example.pashu_ahar.api.ApiService
import com.example.pashu_ahar.api.SessionManager
import com.example.pashu_ahar.api.Stats
import com.example.pashu_ahar.components.BottomNavigationBar
import com.example.pashu_ahar.ui.theme.DarkGreen
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    onHomeClick: () -> Unit,
    onDiseaseClick: () -> Unit,
    onAddCowClick: () -> Unit,
    onCostTrackerClick: () -> Unit,
    onLogout: () -> Unit,
    onPersonalInfoClick: () -> Unit,
    onSecurityClick: () -> Unit,
    onVeterinaryTipsClick: () -> Unit,
    onHelpSupportClick: () -> Unit,
    onAboutClick: () -> Unit
) {
    val context = LocalContext.current
    val sessionManager = remember { SessionManager(context) }
    val scope = rememberCoroutineScope()
    
    var stats by remember { mutableStateOf<Stats?>(null) }
    var isLoading by remember { mutableStateOf(true) }

    val userName = sessionManager.getUserName() ?: "User"
    val userEmail = sessionManager.getUserEmail() ?: ""
    val profileImage = sessionManager.getProfileImage()
    val token = sessionManager.fetchAuthToken() ?: ""

    LaunchedEffect(Unit) {
        scope.launch {
            try {
                val api = ApiService.create()
                val res = api.getStats(token)
                if (res.isSuccessful) {
                    stats = res.body()?.data
                }
                
                // Fetch fresh user profile to keep UI updated
                val meRes = api.getMe(token)
                if (meRes.isSuccessful && meRes.body()?.success == true) {
                    meRes.body()?.user?.let { sessionManager.saveUserDetail(it) }
                }
            } catch (e: Exception) {
                // Connection Error
            } finally {
                isLoading = false
            }
        }
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("My Profile", fontWeight = FontWeight.Bold, color = DarkGreen) },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(containerColor = Color.Transparent)
            )
        },
        bottomBar = {
            BottomNavigationBar(
                currentRoute = "profile",
                onHomeClick = onHomeClick,
                onDiseaseClick = onDiseaseClick,
                onAddCowClick = onAddCowClick,
                onCostTrackerClick = onCostTrackerClick,
                onProfileClick = {}
            )
        }
    ) { innerPadding ->
        if (isLoading) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = DarkGreen)
            }
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
            ) {
                LazyColumn(
                    modifier = Modifier
                        .weight(1f)
                        .padding(horizontal = 16.dp)
                ) {
                    item {
                        ProfileHeader(userName, userEmail, profileImage)
                        Spacer(modifier = Modifier.height(24.dp))
                        ProfileStatsRow(stats)
                        Spacer(modifier = Modifier.height(24.dp))
                        PremiumBanner()
                        Spacer(modifier = Modifier.height(24.dp))
                        Text("Account Settings", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = Color.Gray)
                        Spacer(modifier = Modifier.height(12.dp))
                        SettingsItem(Icons.Default.Person, "Personal Info", onPersonalInfoClick)
                        SettingsItem(Icons.Default.Palette, "App Theme", {}, "Light")
                        SettingsItem(Icons.Default.Notifications, "Notifications", {}, "Alerts & Reminders")
                        SettingsItem(Icons.Default.Security, "Security", onSecurityClick, "Password & Security")
                        Spacer(modifier = Modifier.height(24.dp))
                        Text("More", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = Color.Gray)
                        Spacer(modifier = Modifier.height(12.dp))
                        SettingsItem(Icons.Default.MedicalServices, "Veterinary Tips", onVeterinaryTipsClick)
                        SettingsItem(Icons.Default.Help, "Help & Support", onHelpSupportClick)
                        SettingsItem(Icons.Default.Info, "About", onAboutClick)
                        Spacer(modifier = Modifier.height(16.dp))
                    }
                }
                
                // Fixed Logout Button
                Box(modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)) {
                    LogoutButton {
                        sessionManager.logout()
                        onLogout()
                    }
                }
            }
        }
    }
}

@Composable
fun ProfileHeader(name: String, email: String, image: String?) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(contentAlignment = Alignment.BottomEnd) {
            Image(
                painter = if (image != null) rememberAsyncImagePainter(image) else painterResource(id = R.drawable.ic_launcher_foreground),
                contentDescription = null,
                modifier = Modifier
                    .size(80.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(Color.LightGray),
                contentScale = ContentScale.Crop
            )
            Box(
                modifier = Modifier
                    .size(24.dp)
                    .clip(CircleShape)
                    .background(DarkGreen)
                    .border(2.dp, Color.White, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(Icons.Default.CameraAlt, contentDescription = null, tint = Color.White, modifier = Modifier.size(12.dp))
            }
        }
        Spacer(modifier = Modifier.width(16.dp))
        Column {
            Text(name.uppercase(), fontSize = 18.sp, fontWeight = FontWeight.Bold)
            Text(email, fontSize = 12.sp, color = Color.Gray)
            Surface(
                modifier = Modifier.padding(top = 4.dp),
                shape = RoundedCornerShape(4.dp),
                color = Color(0xFFE8F5E9)
            ) {
                Text(
                    "Active Member",
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp),
                    color = DarkGreen,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

@Composable
fun ProfileStatsRow(stats: Stats?) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        ProfileStatItem(Icons.Default.Pets, "${stats?.totalCows ?: 0}", "Total Cows", Color(0xFF4CAF50))
        ProfileStatItem(Icons.Default.WaterDrop, "${stats?.todayYield ?: 0f} L", "Yield", Color(0xFF2196F3))
        ProfileStatItem(Icons.Default.HelpOutline, "${stats?.avgEfficiency ?: 0}%", "Efficiency", Color(0xFFFF9800))
        ProfileStatItem(Icons.Default.CalendarMonth, "${stats?.dueHeat ?: 0}", "Due Heat", Color(0xFFFF5722))
    }
}

@Composable
fun ProfileStatItem(icon: ImageVector, value: String, label: String, color: Color) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Icon(icon, contentDescription = null, tint = color, modifier = Modifier.size(24.dp))
        Text(value, fontSize = 16.sp, fontWeight = FontWeight.Bold)
        Text(label, fontSize = 10.sp, color = Color.Gray)
    }
}

@Composable
fun PremiumBanner() {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        color = Color(0xFFF1F8E9)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(Icons.Default.WorkspacePremium, contentDescription = null, tint = Color(0xFFFFB300))
            Spacer(modifier = Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text("Pashu-Aahar Premium", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                Text("Unlock advanced insights and reports.", fontSize = 11.sp, color = Color.Gray)
            }
            Text(
                "Upgrade",
                color = DarkGreen,
                fontWeight = FontWeight.Bold,
                fontSize = 12.sp,
                modifier = Modifier.clickable { }
            )
        }
    }
}

@Composable
fun SettingsItem(icon: ImageVector, title: String, onClick: () -> Unit, subtitle: String? = null) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(icon, contentDescription = null, tint = Color.Gray, modifier = Modifier.size(20.dp))
        Spacer(modifier = Modifier.width(16.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(title, fontSize = 14.sp, fontWeight = FontWeight.SemiBold)
            if (subtitle != null) {
                Text(subtitle, fontSize = 11.sp, color = Color.Gray)
            }
        }
        if (title == "App Theme") {
            Text("Light >", fontSize = 12.sp, color = DarkGreen, fontWeight = FontWeight.Bold)
        } else {
            Icon(Icons.AutoMirrored.Filled.KeyboardArrowRight, contentDescription = null, tint = Color.LightGray)
        }
    }
}

@Composable
fun LogoutButton(onClick: () -> Unit) {
    Button(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .height(56.dp),
        shape = RoundedCornerShape(12.dp),
        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFFEBEE))
    ) {
        Icon(Icons.Default.Logout, contentDescription = null, tint = Color.Red)
        Spacer(modifier = Modifier.width(8.dp))
        Text("Logout", color = Color.Red, fontWeight = FontWeight.Bold)
    }
}
