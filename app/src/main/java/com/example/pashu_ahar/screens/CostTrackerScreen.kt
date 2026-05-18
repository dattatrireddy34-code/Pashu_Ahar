package com.example.pashu_ahar.screens

import android.widget.Toast
import androidx.compose.animation.*
import androidx.compose.foundation.Canvas
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
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.pashu_ahar.api.*
import com.example.pashu_ahar.components.BottomNavigationBar
import com.example.pashu_ahar.components.SimplePieChart
import com.example.pashu_ahar.ui.theme.DarkGreen
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CostTrackerScreen(
    onHomeClick: () -> Unit,
    onDiseaseClick: () -> Unit,
    onAddCowClick: () -> Unit,
    onProfileClick: () -> Unit
) {
    val context = LocalContext.current
    val sessionManager = remember { SessionManager(context) }
    val scope = rememberCoroutineScope()
    val token = sessionManager.fetchAuthToken() ?: ""
    
    var costData by remember { mutableStateOf<CostSummary?>(null) }
    var isLoading by remember { mutableStateOf(true) }

    LaunchedEffect(Unit) {
        scope.launch {
            try {
                val api = ApiService.create()
                val res = api.getCostSummary(token)
                if (res.isSuccessful) {
                    costData = res.body()?.data
                }
            } catch (e: Exception) {
                Toast.makeText(context, "Connection Error", Toast.LENGTH_SHORT).show()
            } finally {
                isLoading = false
            }
        }
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Cost Tracker", fontWeight = FontWeight.Bold) },
                actions = { IconButton(onClick = {}) { Icon(Icons.Default.FilterAlt, null) } }
            )
        },
        bottomBar = {
            BottomNavigationBar(
                currentRoute = "cost",
                onHomeClick = onHomeClick,
                onDiseaseClick = onDiseaseClick,
                onAddCowClick = onAddCowClick,
                onCostTrackerClick = {},
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
                        OverviewRow(costData)
                        Spacer(Modifier.height(24.dp))
                        ExpenseOverviewCard(costData?.categoryTotals ?: emptyMap(), costData?.totalExpense ?: 0f)
                        Spacer(Modifier.height(24.dp))
                        Text("Recent Transactions", fontSize = 18.sp, fontWeight = FontWeight.Bold)
                        Spacer(Modifier.height(12.dp))
                    }
                    
                    items(costData?.recentTransactions ?: emptyList()) { transaction ->
                        ExpenseItem(transaction)
                        Spacer(Modifier.height(12.dp))
                    }
                    
                    item {
                        Spacer(Modifier.height(12.dp))
                        Text("Cow-wise Cost Analysis", fontSize = 18.sp, fontWeight = FontWeight.Bold)
                        Spacer(Modifier.height(12.dp))
                        CowCostTable()
                        Spacer(Modifier.height(24.dp))
                    }
                }
            }
        }
    }
}

@Composable
fun CowCostTable() {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Column(Modifier.padding(16.dp)) {
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text("Cow Name", fontWeight = FontWeight.Bold, fontSize = 12.sp, modifier = Modifier.weight(1.5f))
                Text("Fixed", fontWeight = FontWeight.Bold, fontSize = 12.sp, modifier = Modifier.weight(1f))
                Text("Medical", fontWeight = FontWeight.Bold, fontSize = 12.sp, modifier = Modifier.weight(1f))
                Text("Total", fontWeight = FontWeight.Bold, fontSize = 12.sp, modifier = Modifier.weight(1f))
            }
            HorizontalDivider(Modifier.padding(vertical = 8.dp), thickness = 0.5.dp)
            listOf("Lakshmi", "Gauri", "Suga").forEach { name ->
                Row(Modifier.fillMaxWidth().padding(vertical = 4.dp), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text(name, fontSize = 12.sp, modifier = Modifier.weight(1.5f))
                    Text("₹3,250", fontSize = 12.sp, modifier = Modifier.weight(1f))
                    Text("₹650", fontSize = 12.sp, modifier = Modifier.weight(1f))
                    Text("₹4,700", fontWeight = FontWeight.Bold, fontSize = 12.sp, color = DarkGreen, modifier = Modifier.weight(1f))
                }
            }
        }
    }
}

@Composable
fun OverviewRow(data: CostSummary?) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        SummaryCard("Total Expense", "₹${data?.totalExpense?.toInt() ?: 0}", Icons.Default.AccountBalanceWallet, Color(0xFF4CAF50), Modifier.weight(1f))
        SummaryCard("Milk Income", "₹${data?.milkIncome?.toInt() ?: 0}", Icons.Default.Opacity, Color(0xFF2196F3), Modifier.weight(1f))
    }
}

@Composable
fun SummaryCard(label: String, value: String, icon: ImageVector, color: Color, modifier: Modifier) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Column(Modifier.padding(16.dp)) {
            Icon(icon, null, tint = color, modifier = Modifier.size(20.dp))
            Spacer(Modifier.height(8.dp))
            Text(value, fontSize = 18.sp, fontWeight = FontWeight.Bold)
            Text(label, fontSize = 10.sp, color = Color.Gray)
        }
    }
}

@Composable
fun ExpenseOverviewCard(categoryTotals: Map<String, Float>, total: Float) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Column(Modifier.padding(20.dp)) {
            Text("Expense Overview", fontWeight = FontWeight.Bold, fontSize = 16.sp)
            Spacer(Modifier.height(20.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(Modifier.size(120.dp), contentAlignment = Alignment.Center) {
                    SimplePieChart(categoryTotals, total)
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("Total", fontSize = 10.sp, color = Color.Gray)
                        Text("₹${total.toInt()}", fontSize = 14.sp, fontWeight = FontWeight.Bold)
                    }
                }
                Spacer(Modifier.width(24.dp))
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    categoryTotals.forEach { (cat, amount) ->
                        CategoryLegend(cat, amount, total)
                    }
                }
            }
        }
    }
}

@Composable
fun CategoryLegend(label: String, amount: Float, total: Float) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Box(Modifier.size(8.dp).background(DarkGreen, CircleShape))
        Spacer(Modifier.width(8.dp))
        Text(label, fontSize = 11.sp, color = Color.Gray, modifier = Modifier.weight(1f))
        Text("₹${amount.toInt()}", fontSize = 11.sp, fontWeight = FontWeight.Bold)
    }
}

@Composable
fun ExpenseItem(expense: Expense) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(1.dp)
    ) {
        Row(Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
            Box(Modifier.size(40.dp).background(Color(0xFFF5F5F5), RoundedCornerShape(8.dp)), contentAlignment = Alignment.Center) {
                Icon(Icons.Default.ShoppingBag, null, tint = DarkGreen, modifier = Modifier.size(20.dp))
            }
            Spacer(Modifier.width(16.dp))
            Column(Modifier.weight(1f)) {
                Text(expense.itemName, fontWeight = FontWeight.Bold, fontSize = 15.sp)
                Text(expense.category, fontSize = 11.sp, color = Color.Gray)
            }
            Text("₹${expense.amount.toInt()}", fontWeight = FontWeight.Bold, color = if (expense.category == "Income") Color(0xFF4CAF50) else Color.Red)
        }
    }
}
