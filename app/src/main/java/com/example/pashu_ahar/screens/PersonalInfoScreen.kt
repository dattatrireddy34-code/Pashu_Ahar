package com.example.pashu_ahar.screens

import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.pashu_ahar.api.ApiService
import com.example.pashu_ahar.api.SessionManager
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
fun PersonalInfoScreen(onBack: () -> Unit) {
    val context = LocalContext.current
    val sessionManager = remember { SessionManager(context) }
    val scope = rememberCoroutineScope()
    
    var fullName by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("") }
    var farmName by remember { mutableStateOf("") }
    var address by remember { mutableStateOf("") }
    var imageUri by remember { mutableStateOf<Uri?>(null) }
    
    var isLoading by remember { mutableStateOf(true) }
    val scrollState = rememberScrollState()

    LaunchedEffect(Unit) {
        scope.launch {
            try {
                val api = ApiService.create()
                val token = sessionManager.fetchAuthToken() ?: ""
                val res = api.getMe(token)
                if (res.isSuccessful && res.body()?.success == true) {
                    val user = res.body()?.user
                    user?.let {
                        fullName = it.fullName
                        email = it.email
                        phone = it.phoneNumber ?: ""
                        farmName = it.farmName ?: ""
                        address = it.address ?: ""
                        sessionManager.saveUserDetail(it)
                    }
                }
            } catch (e: Exception) {
                // Use cached values if offline
                fullName = sessionManager.getUserName() ?: ""
                email = sessionManager.getUserEmail() ?: ""
                phone = sessionManager.getPhoneNumber() ?: ""
                farmName = sessionManager.getFarmName() ?: ""
                address = sessionManager.getAddress() ?: ""
            } finally {
                isLoading = false
            }
        }
    }

    val launcher = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri ->
        imageUri = uri
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Personal Information", fontWeight = FontWeight.Bold) },
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
                .padding(24.dp)
                .verticalScroll(scrollState)
        ) {
            InfoTextField("Full Name *", fullName, { fullName = it }, Icons.Default.Person, "Enter full name")
            Spacer(modifier = Modifier.height(16.dp))
            InfoTextField("Email Address *", email, { email = it }, Icons.Default.Email, "Enter email")
            Spacer(modifier = Modifier.height(16.dp))
            InfoTextField("Phone Number", phone, { phone = it }, Icons.Default.Phone, "Enter phone number")
            Spacer(modifier = Modifier.height(16.dp))
            InfoTextField("Farm Name", farmName, { farmName = it }, Icons.Default.Agriculture, "Enter farm name")
            Spacer(modifier = Modifier.height(16.dp))
            InfoTextField("Address", address, { address = it }, Icons.Default.LocationOn, "Enter full address")
            
            Spacer(modifier = Modifier.height(40.dp))
            
            if (isLoading) {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.CenterHorizontally), color = DarkGreen)
            } else {
                Button(
                    onClick = {
                        isLoading = true
                        scope.launch {
                            try {
                                val token = sessionManager.fetchAuthToken() ?: ""
                                val api = ApiService.create()
                                
                                val namePart = fullName.toRequestBody("text/plain".toMediaTypeOrNull())
                                val emailPart = email.toRequestBody("text/plain".toMediaTypeOrNull())
                                val phonePart = phone.toRequestBody("text/plain".toMediaTypeOrNull())
                                val farmPart = farmName.toRequestBody("text/plain".toMediaTypeOrNull())
                                val addressPart = address.toRequestBody("text/plain".toMediaTypeOrNull())
                                
                                var imagePart: MultipartBody.Part? = null
                                imageUri?.let { uri ->
                                    val file = getFileFromUri(context, uri)
                                    val requestFile = file.asRequestBody("image/*".toMediaTypeOrNull())
                                    imagePart = MultipartBody.Part.createFormData("profileImage", file.name, requestFile)
                                }

                                val res = api.updateDetails(token, namePart, emailPart, phonePart, farmPart, addressPart, imagePart)
                                if (res.isSuccessful && res.body()?.success == true) {
                                    res.body()?.user?.let { sessionManager.saveUserDetail(it) }
                                    Toast.makeText(context, "Details updated successfully", Toast.LENGTH_SHORT).show()
                                } else {
                                    Toast.makeText(context, res.body()?.message ?: "Update failed", Toast.LENGTH_SHORT).show()
                                }
                            } catch (e: Exception) {
                                Toast.makeText(context, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
                            } finally {
                                isLoading = false
                            }
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = DarkGreen)
                ) {
                    Text("Save Changes", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                }
            }
        }
    }
}

@Composable
fun InfoTextField(label: String, value: String, onValueChange: (String) -> Unit, icon: ImageVector, placeholder: String) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Text(label, fontSize = 14.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(bottom = 8.dp))
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            modifier = Modifier.fillMaxWidth(),
            placeholder = { Text(placeholder) },
            leadingIcon = { Icon(icon, contentDescription = null, tint = DarkGreen) },
            shape = RoundedCornerShape(12.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = DarkGreen,
                unfocusedBorderColor = Color.LightGray
            )
        )
    }
}

private fun getFileFromUri(context: android.content.Context, uri: Uri): File {
    val inputStream = context.contentResolver.openInputStream(uri)
    val file = File(context.cacheDir, "temp_profile_update.jpg")
    val outputStream = FileOutputStream(file)
    inputStream?.copyTo(outputStream)
    return file
}
