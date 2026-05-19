package com.example.pashu_ahar.api

import okhttp3.MultipartBody
import okhttp3.OkHttpClient
import okhttp3.RequestBody
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.*

interface ApiService {
    // ... rest of the interface ...
    @POST("api/auth/login")
    suspend fun login(@Body request: LoginRequest): Response<AuthResponse>
    // ...
    // (I'll just replace the companion object and add imports)

    @Multipart
    @POST("api/auth/signup")
    suspend fun signup(
        @Part("fullName") fullName: RequestBody,
        @Part("email") email: RequestBody,
        @Part("password") password: RequestBody,
        @Part profileImage: MultipartBody.Part?
    ): Response<AuthResponse>

    @GET("api/cows")
    suspend fun getCows(@Header("Authorization") token: String): Response<CowListResponse>

    @GET("api/cows/stats")
    suspend fun getStats(@Header("Authorization") token: String): Response<StatsResponse>

    @Multipart
    @POST("api/cows")
    suspend fun createCow(
        @Header("Authorization") token: String,
        @Part("name") name: RequestBody,
        @Part("breed") breed: RequestBody,
        @Part("age") age: RequestBody,
        @Part("weight") weight: RequestBody,
        @Part("currentYield") currentYield: RequestBody,
        @Part("targetYield") targetYield: RequestBody,
        @Part profileImage: MultipartBody.Part?
    ): Response<CowResponse>

    @DELETE("api/cows/{id}")
    suspend fun deleteCow(
        @Header("Authorization") token: String,
        @Path("id") id: String
    ): Response<CowResponse>

    @GET("api/auth/me")
    suspend fun getMe(@Header("Authorization") token: String): Response<AuthResponse>

    @Multipart
    @PUT("api/auth/updatedetails")
    suspend fun updateDetails(
        @Header("Authorization") token: String,
        @Part("fullName") fullName: RequestBody,
        @Part("email") email: RequestBody,
        @Part("phoneNumber") phoneNumber: RequestBody,
        @Part("farmName") farmName: RequestBody,
        @Part("address") address: RequestBody,
        @Part profileImage: MultipartBody.Part?
    ): Response<AuthResponse>

    @PUT("api/auth/updatepassword")
    suspend fun updatePassword(
        @Header("Authorization") token: String,
        @Body body: Map<String, String>
    ): Response<AuthResponse>

    @GET("api/costs/summary")
    suspend fun getCostSummary(@Header("Authorization") token: String): Response<CostSummaryResponse>

    @GET("api/diseases/summary")
    suspend fun getDiseaseSummary(@Header("Authorization") token: String): Response<DiseaseSummaryResponse>

    companion object {
        private const val BASE_URL = "http://10.0.2.2:3000/" // For Android Emulator
        private const val USE_MOCK = true // Set to false to use real backend

        fun create(): ApiService {
            val logger = HttpLoggingInterceptor().apply { level = HttpLoggingInterceptor.Level.BODY }
            val clientBuilder = OkHttpClient.Builder()
                .addInterceptor(logger)
            
            if (USE_MOCK) {
                clientBuilder.addInterceptor(MockInterceptor())
            }

            return Retrofit.Builder()
                .baseUrl(BASE_URL)
                .client(clientBuilder.build())
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(ApiService::class.java)
        }
    }
}
