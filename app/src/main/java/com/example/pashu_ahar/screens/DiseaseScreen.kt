package com.example.pashu_ahar.screens

import android.widget.Toast
import androidx.compose.animation.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
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
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.rememberAsyncImagePainter
import com.example.pashu_ahar.api.*
import com.example.pashu_ahar.components.BottomNavigationBar
import com.example.pashu_ahar.components.SimplePieChart
import com.example.pashu_ahar.ui.theme.DarkGreen
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DiseaseScreen(
    onHomeClick: () -> Unit,
    onAddCowClick: () -> Unit,
    onCostTrackerClick: () -> Unit,
    onProfileClick: () -> Unit
) {
    val context = LocalContext.current
    val sessionManager = remember { SessionManager(context) }
    val scope = rememberCoroutineScope()
    val token = sessionManager.fetchAuthToken() ?: ""
    
    var diseaseSummary by remember { mutableStateOf<DiseaseSummary?>(null) }
    var isLoading by remember { mutableStateOf(true) }

    LaunchedEffect(Unit) {
        scope.launch {
            try {
                val api = ApiService.create()
                val res = api.getDiseaseSummary(token)
                if (res.isSuccessful) {
                    diseaseSummary = res.body()?.data
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
            CenterAlignedTopAppBar(title = { Text("Disease Tracking", fontWeight = FontWeight.Bold) })
        },
        bottomBar = {
            BottomNavigationBar(
                currentRoute = "disease",
                onHomeClick = onHomeClick,
                onDiseaseClick = {},
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
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = DarkGreen)
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp)
                ) {
                    item {
                        Spacer(Modifier.height(16.dp))
                        DiseaseStatsRow(diseaseSummary?.stats ?: emptyMap())
                        Spacer(Modifier.height(24.dp))
                        DiseaseOverviewCard(diseaseSummary?.diseaseDistribution ?: emptyMap())
                        Spacer(Modifier.height(24.dp))
                        Text("Active Cases (${diseaseSummary?.cases?.size ?: 0})", fontSize = 18.sp, fontWeight = FontWeight.Bold)
                        Spacer(Modifier.height(12.dp))
                    }
                    
                    items(diseaseSummary?.cases ?: emptyList()) { case ->
                        DiseaseCaseItem(case)
                        Spacer(Modifier.height(12.dp))
                    }
                }
            }
        }
    }
}

@Composable
fun DiseaseStatsRow(stats: Map<String, Int>) {
    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        StatBox("Total Cases", "${stats["totalCases"] ?: 0}", Icons.Default.AddModerator, Color(0xFF4CAF50), Modifier.weight(1f))
        StatBox("Active Cases", "${stats["activeCases"] ?: 0}", Icons.Default.Warning, Color(0xFFFF9800), Modifier.weight(1f))
        StatBox("Recovered", "${stats["recovered"] ?: 0}", Icons.Default.CheckCircle, Color(0xFF2196F3), Modifier.weight(1f))
    }
}

@Composable
fun StatBox(label: String, value: String, icon: androidx.compose.ui.graphics.vector.ImageVector, color: Color, modifier: Modifier) {
    Card(modifier, shape = RoundedCornerShape(12.dp), colors = CardDefaults.cardColors(containerColor = Color.White), elevation = CardDefaults.cardElevation(2.dp)) {
        Column(Modifier.padding(12.dp), horizontalAlignment = Alignment.CenterHorizontally) {
            Icon(icon, null, tint = color, modifier = Modifier.size(20.dp))
            Text(value, fontWeight = FontWeight.Bold, fontSize = 16.sp)
            Text(label, fontSize = 10.sp, color = Color.Gray)
        }
    }
}

@Composable
fun DiseaseOverviewCard(distribution: Map<String, Int>) {
    val total = distribution.values.sum().toFloat()
    Card(Modifier.fillMaxWidth(), shape = RoundedCornerShape(20.dp), colors = CardDefaults.cardColors(containerColor = Color.White), elevation = CardDefaults.cardElevation(2.dp)) {
        Row(Modifier.padding(20.dp), verticalAlignment = Alignment.CenterVertically) {
            Box(Modifier.size(100.dp), contentAlignment = Alignment.Center) {
                SimplePieChart(distribution.mapValues { it.value.toFloat() }, total)
                Text("${total.toInt()}", fontWeight = FontWeight.Bold)
            }
            Spacer(Modifier.width(24.dp))
            Column {
                distribution.forEach { (name, count) ->
                    Text("$name: $count", fontSize = 12.sp, color = Color.Gray)
                }
            }
        }
    }
}

@Composable
fun DiseaseCaseItem(case: DiseaseCase) {
    Card(Modifier.fillMaxWidth(), shape = RoundedCornerShape(16.dp), colors = CardDefaults.cardColors(containerColor = Color.White), elevation = CardDefaults.cardElevation(1.dp)) {
        Row(Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
            val profileImage = case.cow?.profileImage
            val cowName = case.cow?.name ?: "Unknown Cow"
            
            Image(
                painter = if (profileImage != null) rememberAsyncImagePainter(profileImage) else painterResource(id = R.drawable.ic_cow), 
                contentDescription = null, 
                modifier = Modifier.size(50.dp).clip(CircleShape), 
                contentScale = androidx.compose.ui.layout.ContentScale.Crop
            )
            Spacer(Modifier.width(16.dp))
            Column(Modifier.weight(1f)) {
                Text(cowName, fontWeight = FontWeight.Bold)
                Text(case.diseaseName, color = Color.Red, fontSize = 14.sp)
                Text("Detected: ${case.detectedOn.take(10)}", fontSize = 11.sp, color = Color.Gray)
            }
            StatusBadge(case.status)
        }
    }
}

@Composable
fun StatusBadge(status: String) {
    Surface(shape = RoundedCornerShape(8.dp), color = if (status == "Recovered") Color(0xFFE8F5E9) else Color(0xFFFFF3E0)) {
        Text(status, modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp), fontSize = 10.sp, fontWeight = FontWeight.Bold, color = if (status == "Recovered") Color(0xFF2E7D32) else Color(0xFFE65100))
    }
}
