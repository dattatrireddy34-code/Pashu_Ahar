package com.example.pashu_ahar.screens

import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.*
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import coil.compose.rememberAsyncImagePainter
import com.example.pashu_ahar.R
import com.example.pashu_ahar.api.ApiService
import com.example.pashu_ahar.api.SessionManager
import com.example.pashu_ahar.components.BottomNavigationBar
import com.example.pashu_ahar.ui.theme.DarkGreen
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.File
import java.io.FileOutputStream

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddCowScreen(
    onBack: () -> Unit,
    onHomeClick: () -> Unit,
    onDiseaseClick: () -> Unit,
    onCostTrackerClick: () -> Unit,
    onProfileClick: () -> Unit,
    onSuccess: () -> Unit
) {
    var currentStep by remember { mutableStateOf(1) }
    var name by remember { mutableStateOf("") }
    var breed by remember { mutableStateOf("Select Breed") }
    var age by remember { mutableStateOf(24) }
    var weight by remember { mutableStateOf(350) }
    var currentYield by remember { mutableStateOf(10.5f) }
    var targetYield by remember { mutableStateOf(18.0f) }
    var imageUri by remember { mutableStateOf<Uri?>(null) }
    var showBreedDialog by remember { mutableStateOf(false) }
    var isLoading by remember { mutableStateOf(false) }

    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val sessionManager = remember { SessionManager(context) }

    val launcher = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri ->
        imageUri = uri
    }
    
    val scrollState = rememberScrollState()

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("Add Cow", fontSize = 18.sp, fontWeight = FontWeight.Bold)
                        Text("Create a new cow profile", fontSize = 12.sp, color = Color.Gray)
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onBack) { Icon(Icons.Default.ArrowBack, contentDescription = null) }
                },
                actions = {
                    IconButton(onClick = onBack) { Icon(Icons.Default.Close, contentDescription = null) }
                }
            )
        },
        bottomBar = {
            BottomNavigationBar(
                currentRoute = "add_cow",
                onHomeClick = onHomeClick,
                onDiseaseClick = onDiseaseClick,
                onAddCowClick = { /* Already here */ },
                onCostTrackerClick = onCostTrackerClick,
                onProfileClick = onProfileClick
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(24.dp)
                .verticalScroll(scrollState),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            StepIndicator(currentStep)
            Spacer(modifier = Modifier.height(32.dp))

            when (currentStep) {
                1 -> Step1BreedAndName(name, { name = it }, breed, { showBreedDialog = true }, imageUri, { launcher.launch("image/*") })
                2 -> Step2BodyDetails(age, { age = it }, weight, { weight = it })
                3 -> Step3YieldTarget(currentYield, { currentYield = it }, targetYield, { targetYield = it })
            }

            Spacer(modifier = Modifier.weight(1f))

            if (isLoading) {
                CircularProgressIndicator(color = DarkGreen)
            } else {
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                    if (currentStep > 1) {
                        OutlinedButton(
                            onClick = { currentStep-- },
                            modifier = Modifier.weight(1f).height(56.dp),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Text("Back", color = Color.Black)
                        }
                    }
                    Button(
                        onClick = {
                            if (currentStep < 3) {
                                currentStep++
                            } else {
                                // Submit
                                isLoading = true
                                scope.launch {
                                    try {
                                        val token = sessionManager.fetchAuthToken() ?: ""
                                        val api = ApiService.create()
                                        
                                        val namePart = name.toRequestBody("text/plain".toMediaTypeOrNull())
                                        val breedPart = breed.toRequestBody("text/plain".toMediaTypeOrNull())
                                        val agePart = age.toString().toRequestBody("text/plain".toMediaTypeOrNull())
                                        val weightPart = weight.toString().toRequestBody("text/plain".toMediaTypeOrNull())
                                        val currentYieldPart = currentYield.toString().toRequestBody("text/plain".toMediaTypeOrNull())
                                        val targetYieldPart = targetYield.toString().toRequestBody("text/plain".toMediaTypeOrNull())
                                        
                                        var imagePart: MultipartBody.Part? = null
                                        imageUri?.let { uri ->
                                            val file = getFileFromUri(context, uri)
                                            val requestFile = file.asRequestBody("image/*".toMediaTypeOrNull())
                                            imagePart = MultipartBody.Part.createFormData("profileImage", file.name, requestFile)
                                        }

                                        val res = api.createCow(token, namePart, breedPart, agePart, weightPart, currentYieldPart, targetYieldPart, imagePart)
                                        if (res.isSuccessful && res.body()?.success == true) {
                                            onSuccess()
                                        } else {
                                            Toast.makeText(context, res.body()?.message ?: "Failed", Toast.LENGTH_SHORT).show()
                                        }
                                    } catch (e: Exception) {
                                        Toast.makeText(context, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
                                    } finally {
                                        isLoading = false
                                    }
                                }
                            }
                        },
                        modifier = Modifier.weight(1f).height(56.dp),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = DarkGreen)
                    ) {
                        Text(if (currentStep < 3) "Next" else "Save", fontWeight = FontWeight.Bold)
                        if (currentStep < 3) Icon(Icons.Default.ArrowForward, contentDescription = null, modifier = Modifier.padding(start = 8.dp))
                        else Icon(Icons.Default.Check, contentDescription = null, modifier = Modifier.padding(start = 8.dp))
                    }
                }
            }
        }
    }

    if (showBreedDialog) {
        BreedSelectorDialog(onDismiss = { showBreedDialog = false }, onSelect = { breed = it; showBreedDialog = false })
    }
}

@Composable
fun StepIndicator(currentStep: Int) {
    Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth()) {
        repeat(3) { index ->
            val step = index + 1
            Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.weight(1f)) {
                Box(
                    modifier = Modifier
                        .size(30.dp)
                        .clip(CircleShape)
                        .background(if (currentStep >= step) DarkGreen else Color.LightGray),
                    contentAlignment = Alignment.Center
                ) {
                    Text(step.toString(), color = Color.White, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                }
                Text(
                    when (step) {
                        1 -> "Breed & Name"
                        2 -> "Body Details"
                        else -> "Yield Target"
                    },
                    fontSize = 10.sp,
                    color = if (currentStep >= step) DarkGreen else Color.Gray,
                    modifier = Modifier.padding(top = 4.dp)
                )
            }
            if (index < 2) {
                Box(modifier = Modifier.width(40.dp).height(2.dp).background(if (currentStep > step) DarkGreen else Color.LightGray).padding(top = 10.dp))
            }
        }
    }
}

@Composable
fun Step1BreedAndName(name: String, onNameChange: (String) -> Unit, breed: String, onBreedClick: () -> Unit, imageUri: Uri?, onImageClick: () -> Unit) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Box(
            modifier = Modifier
                .size(150.dp)
                .clip(RoundedCornerShape(24.dp))
                .background(Color(0xFFF5F5F5))
                .clickable { onImageClick() },
            contentAlignment = Alignment.Center
        ) {
            if (imageUri == null) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(painterResource(id = R.drawable.ic_cow), contentDescription = null, modifier = Modifier.size(80.dp), tint = Color.Gray)
                    IconButton(onClick = onImageClick, modifier = Modifier.background(DarkGreen, CircleShape).size(30.dp)) {
                        Icon(Icons.Default.CameraAlt, contentDescription = null, tint = Color.White, modifier = Modifier.size(16.dp))
                    }
                }
            } else {
                Image(painter = rememberAsyncImagePainter(imageUri), contentDescription = null, modifier = Modifier.fillMaxSize(), contentScale = ContentScale.Crop)
            }
        }
        
        Spacer(modifier = Modifier.height(24.dp))
        Text("Step 1 of 3", fontSize = 14.sp, color = Color.Gray)
        Text("Breed & Name", fontSize = 20.sp, fontWeight = FontWeight.Bold)
        
        Spacer(modifier = Modifier.height(32.dp))
        
        Column(modifier = Modifier.fillMaxWidth()) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.Label, contentDescription = null, tint = DarkGreen, modifier = Modifier.size(18.dp))
                Text(" Cow Name", fontWeight = FontWeight.Bold, fontSize = 14.sp)
            }
            OutlinedTextField(
                value = name,
                onValueChange = onNameChange,
                modifier = Modifier.fillMaxWidth().padding(top = 8.dp),
                placeholder = { Text("Laxmi") },
                shape = RoundedCornerShape(12.dp)
            )
            Text("Example: Gauri, Rani, Lakshmi", fontSize = 12.sp, color = Color.Gray, modifier = Modifier.padding(top = 4.dp))
        }

        Spacer(modifier = Modifier.height(24.dp))

        Column(modifier = Modifier.fillMaxWidth()) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.Pets, contentDescription = null, tint = DarkGreen, modifier = Modifier.size(18.dp))
                Text(" Breed", fontWeight = FontWeight.Bold, fontSize = 14.sp)
            }
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp)
                    .height(56.dp)
                    .border(1.dp, Color.Gray, RoundedCornerShape(12.dp))
                    .clickable { onBreedClick() }
                    .padding(horizontal = 16.dp),
                contentAlignment = Alignment.CenterStart
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(breed, modifier = Modifier.weight(1f))
                    Icon(Icons.Default.KeyboardArrowDown, contentDescription = null)
                }
            }
        }
    }
}

@Composable
fun Step2BodyDetails(age: Int, onAgeChange: (Int) -> Unit, weight: Int, onWeightChange: (Int) -> Unit) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text("Step 2 of 3", fontSize = 14.sp, color = Color.Gray)
        Text("Body Details", fontSize = 20.sp, fontWeight = FontWeight.Bold)
        
        Spacer(modifier = Modifier.height(32.dp))
        
        CounterInput(label = "Age (Months)", icon = Icons.Default.CalendarMonth, value = age, onValueChange = onAgeChange)
        Text("Enter age in months", fontSize = 12.sp, color = Color.Gray, modifier = Modifier.fillMaxWidth().padding(top = 4.dp))

        Spacer(modifier = Modifier.height(24.dp))

        CounterInput(label = "Weight (kg)", icon = Icons.Default.MonitorWeight, value = weight, onValueChange = onWeightChange)
        Text("Enter weight in kilograms", fontSize = 12.sp, color = Color.Gray, modifier = Modifier.fillMaxWidth().padding(top = 4.dp))
        
        Spacer(modifier = Modifier.height(32.dp))
        
        InfoBox("Accurate age and weight help us give better nutrition recommendations")
    }
}

@Composable
fun Step3YieldTarget(current: Float, onCurrentChange: (Float) -> Unit, target: Float, onTargetChange: (Float) -> Unit) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text("Step 3 of 3", fontSize = 14.sp, color = Color.Gray)
        Text("Yield Target", fontSize = 20.sp, fontWeight = FontWeight.Bold)
        
        Spacer(modifier = Modifier.height(32.dp))
        
        FloatCounterInput(label = "Current Daily Yield (L)", icon = Icons.Default.WaterDrop, value = current, onValueChange = onCurrentChange)
        Text("Enter current daily yield in liters", fontSize = 12.sp, color = Color.Gray, modifier = Modifier.fillMaxWidth().padding(top = 4.dp))

        Spacer(modifier = Modifier.height(24.dp))

        FloatCounterInput(label = "Target Daily Yield (L)", icon = Icons.Default.TrendingUp, value = target, onValueChange = onTargetChange)
        Text("Enter target daily yield in liters", fontSize = 12.sp, color = Color.Gray, modifier = Modifier.fillMaxWidth().padding(top = 4.dp))
        
        Spacer(modifier = Modifier.height(32.dp))
        
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(12.dp))
                .background(Color(0xFFF3E5F5))
                .padding(16.dp)
        ) {
            Row {
                Icon(Icons.Default.Lightbulb, contentDescription = null, tint = Color(0xFF7B1FA2))
                Spacer(modifier = Modifier.width(12.dp))
                Text("Set a realistic target to track progress and improve productivity.", fontSize = 12.sp, color = Color(0xFF7B1FA2))
            }
        }
    }
}

@Composable
fun CounterInput(label: String, icon: ImageVector, value: Int, onValueChange: (Int) -> Unit) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(icon, contentDescription = null, tint = DarkGreen, modifier = Modifier.size(18.dp))
            Text(" $label", fontWeight = FontWeight.Bold, fontSize = 14.sp)
        }
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 8.dp)
                .height(56.dp)
                .border(1.dp, Color.LightGray, RoundedCornerShape(12.dp)),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = { if (value > 0) onValueChange(value - 1) }) { Icon(Icons.Default.Remove, contentDescription = null) }
            Text(value.toString(), modifier = Modifier.weight(1f), textAlign = TextAlign.Center, fontWeight = FontWeight.Bold, fontSize = 18.sp)
            IconButton(onClick = { onValueChange(value + 1) }) { Icon(Icons.Default.Add, contentDescription = null) }
        }
    }
}

@Composable
fun FloatCounterInput(label: String, icon: ImageVector, value: Float, onValueChange: (Float) -> Unit) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(icon, contentDescription = null, tint = DarkGreen, modifier = Modifier.size(18.dp))
            Text(" $label", fontWeight = FontWeight.Bold, fontSize = 14.sp)
        }
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 8.dp)
                .height(56.dp)
                .border(1.dp, Color.LightGray, RoundedCornerShape(12.dp)),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = { if (value > 0.5f) onValueChange(value - 0.5f) }) { Icon(Icons.Default.Remove, contentDescription = null) }
            Text(String.format("%.1f", value), modifier = Modifier.weight(1f), textAlign = TextAlign.Center, fontWeight = FontWeight.Bold, fontSize = 18.sp)
            IconButton(onClick = { onValueChange(value + 0.5f) }) { Icon(Icons.Default.Add, contentDescription = null) }
        }
    }
}

@Composable
fun InfoBox(text: String) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(Color(0xFFE3F2FD))
            .padding(16.dp)
    ) {
        Row {
            Icon(Icons.Default.Info, contentDescription = null, tint = Color(0xFF1976D2))
            Spacer(modifier = Modifier.width(12.dp))
            Text(text, fontSize = 12.sp, color = Color(0xFF1976D2))
        }
    }
}

@Composable
fun BreedSelectorDialog(onDismiss: () -> Unit, onSelect: (String) -> Unit) {
    val breeds = listOf("Holstein Friesian", "Jersey", "Brown Swiss", "Guernsey", "Ayrshire", "Milking Shorthorn", "Gir", "Sahiwal")
    
    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier.fillMaxWidth().height(400.dp),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text("Select Breed", fontSize = 18.sp, fontWeight = FontWeight.Bold, modifier = Modifier.weight(1f))
                    IconButton(onClick = onDismiss, modifier = Modifier.background(DarkGreen, CircleShape).size(24.dp)) {
                        Icon(Icons.Default.Close, contentDescription = null, tint = Color.White, modifier = Modifier.size(16.dp))
                    }
                }
                Spacer(modifier = Modifier.height(16.dp))
                LazyVerticalGrid(columns = GridCells.Fixed(2), verticalArrangement = Arrangement.spacedBy(16.dp), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                    items(breeds) { breed ->
                        BreedCard(breed) { onSelect(breed) }
                    }
                }
            }
        }
    }
}

@Composable
fun BreedCard(name: String, onClick: () -> Unit) {
    Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.clickable { onClick() }) {
        Box(
            modifier = Modifier
                .size(70.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(Color(0xFFF5F5F5)),
            contentAlignment = Alignment.Center
        ) {
            Icon(painterResource(id = R.drawable.ic_cow), contentDescription = null, modifier = Modifier.size(40.dp), tint = Color.Gray)
        }
        Text(name, fontSize = 12.sp, textAlign = TextAlign.Center, modifier = Modifier.padding(top = 4.dp))
    }
}

private fun getFileFromUri(context: android.content.Context, uri: Uri): File {
    val inputStream = context.contentResolver.openInputStream(uri)
    val file = File(context.cacheDir, "temp_cow_image.jpg")
    val outputStream = FileOutputStream(file)
    inputStream?.copyTo(outputStream)
    return file
}
