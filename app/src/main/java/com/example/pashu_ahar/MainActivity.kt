package com.example.pashu_ahar

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.pashu_ahar.screens.ForgotPasswordScreen
import com.example.pashu_ahar.screens.HomeScreen
import com.example.pashu_ahar.screens.AddCowScreen
import com.example.pashu_ahar.screens.DiseaseScreen
import com.example.pashu_ahar.screens.CostTrackerScreen
import com.example.pashu_ahar.screens.ProfileScreen
import com.example.pashu_ahar.screens.PersonalInfoScreen
import com.example.pashu_ahar.screens.ChangePasswordScreen
import com.example.pashu_ahar.screens.VeterinaryTipsScreen
import com.example.pashu_ahar.screens.HelpSupportScreen
import com.example.pashu_ahar.screens.AboutScreen
import com.example.pashu_ahar.screens.LoginScreen
import com.example.pashu_ahar.screens.SignUpScreen
import com.example.pashu_ahar.ui.theme.Pashu_AharTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            Pashu_AharTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    AppNavigation()
                }
            }
        }
    }
}

@Composable
fun AppNavigation() {
    val navController = rememberNavController()
    val context = LocalContext.current

    NavHost(navController = navController, startDestination = "login") {
        composable("login") {
            LoginScreen(
                onSignUpClick = { navController.navigate("signup") },
                onForgotPasswordClick = { navController.navigate("forgot_password") },
                onLoginSuccess = { message ->
                    Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
                    navController.navigate("home") {
                        popUpTo("login") { inclusive = true }
                    }
                }
            )
        }
        composable("signup") {
            SignUpScreen(
                onLoginClick = { navController.navigate("login") },
                onSignUpSuccess = { message ->
                    Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
                    navController.navigate("home") {
                        popUpTo("signup") { inclusive = true }
                    }
                }
            )
        }
        composable("home") {
            HomeScreen(
                onAddCowClick = { navController.navigate("add_cow") },
                onCowClick = { cow ->
                    // Could navigate to details
                },
                onDiseaseClick = { navController.navigate("disease") },
                onCostTrackerClick = { navController.navigate("cost") },
                onProfileClick = { navController.navigate("profile") },
                onLogout = {
                    navController.navigate("login") {
                        popUpTo("home") { inclusive = true }
                    }
                }
            )
        }
        composable("disease") {
            DiseaseScreen(
                onHomeClick = { navController.navigate("home") },
                onAddCowClick = { navController.navigate("add_cow") },
                onCostTrackerClick = { navController.navigate("cost") },
                onProfileClick = { navController.navigate("profile") }
            )
        }
        composable("cost") {
            CostTrackerScreen(
                onHomeClick = { navController.navigate("home") },
                onDiseaseClick = { navController.navigate("disease") },
                onAddCowClick = { navController.navigate("add_cow") },
                onProfileClick = { navController.navigate("profile") }
            )
        }
        composable("profile") {
            ProfileScreen(
                onHomeClick = { navController.navigate("home") },
                onDiseaseClick = { navController.navigate("disease") },
                onAddCowClick = { navController.navigate("add_cow") },
                onCostTrackerClick = { navController.navigate("cost") },
                onLogout = {
                    navController.navigate("login") {
                        popUpTo("home") { inclusive = true }
                    }
                },
                onPersonalInfoClick = { navController.navigate("personal_info") },
                onSecurityClick = { navController.navigate("change_password") },
                onVeterinaryTipsClick = { navController.navigate("vet_tips") },
                onHelpSupportClick = { navController.navigate("help_support") },
                onAboutClick = { navController.navigate("about") }
            )
        }
        composable("personal_info") {
            PersonalInfoScreen(onBack = { navController.popBackStack() })
        }
        composable("change_password") {
            ChangePasswordScreen(onBack = { navController.popBackStack() })
        }
        composable("vet_tips") {
            VeterinaryTipsScreen(onBack = { navController.popBackStack() })
        }
        composable("help_support") {
            HelpSupportScreen(onBack = { navController.popBackStack() })
        }
        composable("about") {
            AboutScreen(onBack = { navController.popBackStack() })
        }
        composable("add_cow") {
            AddCowScreen(
                onBack = { navController.popBackStack() },
                onHomeClick = { navController.navigate("home") },
                onDiseaseClick = { navController.navigate("disease") },
                onCostTrackerClick = { navController.navigate("cost") },
                onProfileClick = { navController.navigate("profile") },
                onSuccess = {
                    Toast.makeText(context, "Cow added successfully!", Toast.LENGTH_SHORT).show()
                    navController.popBackStack()
                }
            )
        }
        composable("forgot_password") {
            ForgotPasswordScreen(
                onBackClick = { navController.popBackStack() },
                onResetPasswordClick = { email ->
                    Toast.makeText(context, "Reset link sent to $email", Toast.LENGTH_SHORT).show()
                    navController.popBackStack()
                }
            )
        }
    }
}
