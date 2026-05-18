package com.example.pashu_ahar.api

data class AuthResponse(
    val success: Boolean,
    val token: String?,
    val message: String?,
    val user: User?
)

data class User(
    val id: String,
    val fullName: String,
    val email: String,
    val profileImage: String?,
    val phoneNumber: String? = "",
    val farmName: String? = "",
    val address: String? = ""
)

data class LoginRequest(
    val email: String,
    val password: String
)

data class Cow(
    val _id: String? = null,
    val name: String,
    val breed: String,
    val age: Int,
    val weight: Int,
    val currentYield: Float,
    val targetYield: Float,
    val profileImage: String? = null,
    val status: List<String> = listOf("Milk Cow", "Healthy")
)

data class CowResponse(
    val success: Boolean,
    val data: Cow?,
    val message: String?
)

data class CowListResponse(
    val success: Boolean,
    val count: Int,
    val data: List<Cow>
)

data class Stats(
    val totalCows: Int,
    val todayYield: Float,
    val avgEfficiency: Int,
    val dueHeat: Int
)

data class StatsResponse(
    val success: Boolean,
    val data: Stats
)

data class Expense(
    val _id: String?,
    val category: String,
    val itemName: String,
    val amount: Float,
    val date: String,
    val quantity: String?
)

data class CostSummary(
    val totalExpense: Float,
    val milkIncome: Float,
    val netProfit: Float,
    val costPerCow: Float,
    val categoryTotals: Map<String, Float>,
    val recentTransactions: List<Expense>
)

data class CostSummaryResponse(
    val success: Boolean,
    val data: CostSummary
)

data class DiseaseCase(
    val _id: String?,
    val cow: Cow,
    val diseaseName: String,
    val status: String,
    val severity: String,
    val detectedOn: String,
    val symptoms: String?
)

data class DiseaseSummary(
    val stats: Map<String, Int>,
    val diseaseDistribution: Map<String, Int>,
    val cases: List<DiseaseCase>
)

data class DiseaseSummaryResponse(
    val success: Boolean,
    val data: DiseaseSummary
)
