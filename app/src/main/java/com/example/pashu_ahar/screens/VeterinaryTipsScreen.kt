package com.example.pashu_ahar.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.pashu_ahar.ui.theme.DarkGreen

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VeterinaryTipsScreen(onBack: () -> Unit) {
    val tips = listOf(
        Triple(Icons.Default.WaterDrop, "Fresh Water Always", "Ensure your cows have access to clean, fresh water at all times. A dairy cow can drink up to 100 liters per day."),
        Triple(Icons.Default.Eco, "Balanced Diet", "Feed a balanced mix of roughage and concentrates. Maintain correct ratios of fibre, protein, and energy for optimal milk production."),
        Triple(Icons.Default.Healing, "Regular Vaccination", "Vaccinate your herd regularly against FMD, HS, and BQ. Consult your local vet for a vaccination schedule."),
        Triple(Icons.Default.BugReport, "Deworming Schedule", "Deworm every 3-4 months. Internal parasites reduce feed efficiency and can severely lower milk production."),
        Triple(Icons.Default.Home, "Clean Housing", "Keep the shed clean, dry, and well-ventilated. Clean manure daily and disinfect the floor weekly to prevent diseases."),
        Triple(Icons.Default.MedicalServices, "Monitor Health Daily", "Check for signs of illness every day - lethargy, loss of appetite, reduced milk, or nasal discharge. Early detection saves lives."),
        Triple(Icons.Default.CalendarMonth, "Heat Detection", "Monitor cows for heat signs like restlessness and reduced milk. Timely insemination (12-18 hours after heat) improves conception rates.")
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = { 
                    Column {
                        Text("Veterinary Tips", fontWeight = FontWeight.Bold, fontSize = 18.sp)
                        Text("Expert care tips for your animals", fontSize = 12.sp, color = Color.Gray)
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = null)
                    }
                }
            )
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 16.dp)
        ) {
            itemsIndexed(tips) { index, tip ->
                TipCard(index + 1, tip.first, tip.second, tip.third)
                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    }
}

@Composable
fun TipCard(number: Int, icon: ImageVector, title: String, desc: String) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(modifier = Modifier.padding(16.dp)) {
            Icon(icon, contentDescription = null, tint = DarkGreen, modifier = Modifier.size(24.dp))
            Spacer(modifier = Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(title, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                Text(desc, fontSize = 12.sp, color = Color.Gray, lineHeight = 18.sp)
            }
            Spacer(modifier = Modifier.width(8.dp))
            Surface(
                shape = RoundedCornerShape(4.dp),
                color = Color(0xFFE8F5E9)
            ) {
                Text(
                    text = String.format("%02d", number),
                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                    color = DarkGreen,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}
