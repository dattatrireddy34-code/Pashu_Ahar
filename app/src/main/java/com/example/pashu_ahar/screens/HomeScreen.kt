package com.example.pashu_ahar.screens

import android.widget.Toast
import androidx.compose.animation.*
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
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
import androidx.compose.ui.tooling.preview.Preview
import coil.compose.rememberAsyncImagePainter
import com.example.pashu_ahar.R
import com.example.pashu_ahar.api.*
import com.example.pashu_ahar.components.BottomNavigationBar
import com.example.pashu_ahar.ui.theme.DarkGreen
import com.example.pashu_ahar.ui.theme.Pashu_AharTheme
import kotlinx.coroutines.launch

@Composable
fun HomeScreen(
    onAddCowClick: () -> Unit,
    onCowClick: (Cow) -> Unit,
    onDiseaseClick: () -> Unit,
    onCostTrackerClick: () -> Unit,
    onProfileClick: () -> Unit,
    onLogout: () -> Unit
) {
    val context = LocalContext.current
    val sessionManager = remember { SessionManager(context) }
    val scope = rememberCoroutineScope()
    
    var stats by remember { mutableStateOf<Stats?>(null) }
    var cows by remember { mutableStateOf<List<Cow>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }
    var isRefreshing by remember { mutableStateOf(false) }
    var selectedCow by remember { mutableStateOf<Cow?>(null) }
    var showActions by remember { mutableStateOf(false) }

    val token = sessionManager.fetchAuthToken() ?: ""
    val userName = sessionManager.getUserName() ?: "User"
    val userProfileImage = sessionManager.getProfileImage()

    fun fetchData() {
        scope.launch {
            try {
                val api = ApiService.create()
                val statsRes = api.getStats(token)
                if (statsRes.isSuccessful) {
                    stats = statsRes.body()?.data
                }
                
                val cowsRes = api.getCows(token)
                if (cowsRes.isSuccessful) {
                    cows = cowsRes.body()?.data ?: emptyList()
                }
            } catch (e: Exception) {
                // Connection error
            } finally {
                isLoading = false
                isRefreshing = false
            }
        }
    }

    LaunchedEffect(Unit) {
        fetchData()
    }

    Scaffold(
        topBar = {
            HomeTopBar(userName, userProfileImage, onAddCowClick)
        },
        bottomBar = {
            BottomNavigationBar(
                currentRoute = "home",
                onHomeClick = { /* Already here */ },
                onDiseaseClick = onDiseaseClick,
                onAddCowClick = onAddCowClick,
                onCostTrackerClick = onCostTrackerClick,
                onProfileClick = onProfileClick
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            if (isLoading) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = DarkGreen)
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp)
                ) {
                    item {
                        Row(
                            modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                "My Herd Dashboard",
                                fontSize = 22.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = DarkGreen,
                                modifier = Modifier.weight(1f)
                            )
                            Surface(
                                shape = RoundedCornerShape(12.dp),
                                color = Color(0xFFE8F5E9)
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Box(modifier = Modifier.size(6.dp).background(Color(0xFF4CAF50), CircleShape))
                                    Text(" LIVE", fontSize = 10.sp, color = Color(0xFF4CAF50), fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                        DashboardStats(stats)
                        Spacer(modifier = Modifier.height(24.dp))
                        Text(
                            "My Herd (${cows.size})",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                    }

                    if (cows.isEmpty()) {
                        item {
                            EmptyHerdView(onAddCowClick)
                        }
                    } else {
                        items(cows, key = { it._id ?: it.hashCode() }) { cow ->
                            CowItem(cow) { 
                                selectedCow = cow
                                showActions = true
                            }
                            Spacer(modifier = Modifier.height(12.dp))
                        }
                    }
                }
            }
        }
    }

    if (showActions && selectedCow != null) {
        CowActionBottomSheet(
            cow = selectedCow!!,
            onDismiss = { showActions = false },
            onEdit = { 
                showActions = false
                onCowClick(selectedCow!!) 
            },
            onDelete = {
                scope.launch {
                    try {
                        val api = ApiService.create()
                        val res = api.deleteCow(token, selectedCow!!._id ?: "")
                        if (res.isSuccessful) {
                            Toast.makeText(context, "Cow deleted", Toast.LENGTH_SHORT).show()
                            fetchData()
                        }
                    } catch (e: Exception) {
                        Toast.makeText(context, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
                    } finally {
                        showActions = false
                    }
                }
            }
        )
    }
}

@Composable
fun EmptyHerdView(onAddClick: () -> Unit) {
    Column(
        modifier = Modifier.fillMaxWidth().padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(Icons.Default.Pets, contentDescription = null, modifier = Modifier.size(64.dp), tint = Color.LightGray)
        Spacer(modifier = Modifier.height(16.dp))
        Text("No cows added yet", color = Color.Gray, fontWeight = FontWeight.Medium)
        Button(
            onClick = onAddClick,
            modifier = Modifier.padding(top = 16.dp),
            colors = ButtonDefaults.buttonColors(containerColor = DarkGreen)
        ) {
            Text("Add Your First Cow")
        }
    }
}

@Composable
fun HomeTopBar(name: String, profileImage: String?, onAddClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
            .statusBarsPadding(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Image(
            painter = if (profileImage != null) rememberAsyncImagePainter(profileImage) else painterResource(id = R.drawable.ic_launcher_foreground),
            contentDescription = "Profile",
            modifier = Modifier
                .size(48.dp)
                .clip(CircleShape)
                .background(Color.LightGray)
                .border(1.dp, Color.White, CircleShape),
            contentScale = ContentScale.Crop
        )
        Spacer(modifier = Modifier.width(12.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text("Namaste,", fontSize = 12.sp, color = Color.Gray)
            Text(name.uppercase(), fontSize = 16.sp, fontWeight = FontWeight.Bold)
        }
        Surface(
            modifier = Modifier
                .size(40.dp)
                .clickable { onAddClick() },
            shape = CircleShape,
            color = DarkGreen,
            shadowElevation = 4.dp
        ) {
            Box(contentAlignment = Alignment.Center) {
                Icon(Icons.Default.Add, contentDescription = "Add", tint = Color.White)
            }
        }
    }
}

@Composable
fun DashboardStats(stats: Stats?) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            StatItem(Icons.Default.Pets, "${stats?.totalCows ?: 0}", "Total Cows", Color(0xFF4CAF50))
            StatItem(Icons.Default.WaterDrop, "${stats?.todayYield ?: 0f} L", "Today's Yield", Color(0xFFFF9800))
            StatItem(Icons.Default.Eco, "${stats?.avgEfficiency ?: 0}%", "Avg. Efficiency", Color(0xFF4CAF50))
            StatItem(Icons.Default.Event, "${stats?.dueHeat ?: 0}", "Due Heat", Color(0xFF2196F3))
        }
    }
}

@Composable
fun StatItem(icon: ImageVector, value: String, label: String, color: Color) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Box(
            modifier = Modifier.size(40.dp).background(color.copy(alpha = 0.1f), CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Icon(icon, contentDescription = null, tint = color, modifier = Modifier.size(20.dp))
        }
        Spacer(modifier = Modifier.height(8.dp))
        Text(value, fontSize = 16.sp, fontWeight = FontWeight.Bold, color = color)
        Text(label, fontSize = 10.sp, color = Color.Gray)
    }
}

@Composable
fun CowItem(cow: Cow, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Image(
                    painter = if (cow.profileImage != null) rememberAsyncImagePainter(cow.profileImage) else painterResource(id = R.drawable.ic_cow),
                    contentDescription = null,
                    modifier = Modifier
                        .size(64.dp)
                        .clip(RoundedCornerShape(12.dp)),
                    contentScale = ContentScale.Crop
                )
                Spacer(modifier = Modifier.width(16.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(cow.name, fontSize = 18.sp, fontWeight = FontWeight.Bold)
                        Spacer(modifier = Modifier.weight(1f))
                        Icon(Icons.Default.MoreVert, contentDescription = null, tint = Color.Gray)
                    }
                    Text(cow.breed, fontSize = 14.sp, color = Color.Gray)
                    Row(modifier = Modifier.padding(top = 6.dp)) {
                        StatusTag("Milk Cow", Color(0xFFE8F5E9), Color(0xFF2E7D32))
                        Spacer(modifier = Modifier.width(8.dp))
                        StatusTag("Healthy", Color(0xFFE3F2FD), Color(0xFF1976D2))
                    }
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
            HorizontalDivider(color = Color(0xFFF5F5F5), thickness = 1.dp)
            Spacer(modifier = Modifier.height(16.dp))
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.WaterDrop, contentDescription = null, modifier = Modifier.size(16.dp), tint = Color.Gray)
                    Text("${cow.currentYield} L/day", fontSize = 13.sp, modifier = Modifier.padding(start = 4.dp), fontWeight = FontWeight.Medium)
                }
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.TrackChanges, contentDescription = null, modifier = Modifier.size(16.dp), tint = Color.Gray)
                    Text("${cow.targetYield} L/day (Target)", fontSize = 13.sp, modifier = Modifier.padding(start = 4.dp), fontWeight = FontWeight.Medium)
                }
            }
        }
    }
}

@Composable
fun StatusTag(text: String, bgColor: Color, textColor: Color) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(8.dp))
            .background(bgColor)
            .padding(horizontal = 8.dp, vertical = 4.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
                if (text == "Healthy") Icons.Default.CheckCircle else Icons.Default.Pets,
                contentDescription = null,
                modifier = Modifier.size(12.dp),
                tint = textColor
            )
            Text(text, fontSize = 10.sp, color = textColor, fontWeight = FontWeight.Bold, modifier = Modifier.padding(start = 4.dp))
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CowActionBottomSheet(cow: Cow, onDismiss: () -> Unit, onEdit: () -> Unit, onDelete: () -> Unit) {
    ModalBottomSheet(onDismissRequest = onDismiss, containerColor = Color.White) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 32.dp, start = 24.dp, end = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text("Cow Actions: ${cow.name}", fontWeight = FontWeight.Bold, fontSize = 18.sp)
            Spacer(modifier = Modifier.height(24.dp))
            
            ActionRow(icon = Icons.Default.Edit, text = "Edit Details", color = Color(0xFF1976D2), onClick = onEdit)
            Spacer(modifier = Modifier.height(16.dp))
            ActionRow(icon = Icons.Default.Delete, text = "Delete Cow", color = Color.Red, onClick = onDelete)
            
            Spacer(modifier = Modifier.height(24.dp))
            Button(
                onClick = onDismiss,
                modifier = Modifier.fillMaxWidth().height(56.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFF5F5F5)),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text("Cancel", color = Color.Black)
            }
        }
    }
}

@Composable
fun ActionRow(icon: ImageVector, text: String, color: Color, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(44.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(color.copy(alpha = 0.1f)),
            contentAlignment = Alignment.Center
        ) {
            Icon(icon, contentDescription = null, tint = color, modifier = Modifier.size(22.dp))
        }
        Spacer(modifier = Modifier.width(16.dp))
        Text(text, color = if (color == Color.Red) color else Color.Black, fontWeight = FontWeight.SemiBold, fontSize = 16.sp)
    }
}

@Preview(showBackground = true)
@Composable
fun HomeScreenPreview() {
    Pashu_AharTheme {
        HomeScreen(
            onAddCowClick = {},
            onCowClick = {},
            onDiseaseClick = {},
            onCostTrackerClick = {},
            onProfileClick = {},
            onLogout = {}
        )
    }
}
