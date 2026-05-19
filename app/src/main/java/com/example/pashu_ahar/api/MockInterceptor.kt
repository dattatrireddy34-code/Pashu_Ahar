package com.example.pashu_ahar.api

import android.util.Log
import okhttp3.Interceptor
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.Protocol
import okhttp3.Response
import okhttp3.ResponseBody.Companion.toResponseBody

class MockInterceptor : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()
        val path = request.url.encodedPath
        val method = request.method
        
        Log.d("MockInterceptor", "Intercepting: ${method} ${path}")

        if (path.endsWith("/api/auth/login") && method == "POST") {
            Log.d("MockInterceptor", "Returning Mock Login")
            return createResponse(chain, """
                {
                    "success": true,
                    "token": "mock_token",
                    "message": "Login successful (MOCK)",
                    "user": {
                        "id": "123",
                        "fullName": "Test Farmer",
                        "email": "test@example.com",
                        "profileImage": null,
                        "phoneNumber": "1234567890",
                        "farmName": "Test Farm",
                        "address": "Test Village"
                    }
                }
            """.trimIndent())
        }

        if (path.endsWith("/api/auth/signup") && method == "POST") {
            Log.d("MockInterceptor", "Returning Mock Signup")
            return createResponse(chain, """
                {
                    "success": true,
                    "token": "mock_token",
                    "message": "Signup successful (MOCK)",
                    "user": {
                        "id": "123",
                        "fullName": "Test Farmer",
                        "email": "test@example.com",
                        "profileImage": null
                    }
                }
            """.trimIndent())
        }

        if (path.contains("/api/cows/stats") && method == "GET") {
            return createResponse(chain, """
                {
                    "success": true,
                    "data": {
                        "totalCows": 12,
                        "todayYield": 45.5,
                        "avgEfficiency": 85,
                        "dueHeat": 2
                    }
                }
            """.trimIndent())
        }

        if (path.endsWith("/api/cows") && method == "POST") {
            Log.d("MockInterceptor", "Returning Mock Create Cow")
            return createResponse(chain, """
                {
                    "success": true,
                    "message": "Cow added successfully (MOCK)",
                    "data": {
                        "_id": "mock_cow_${System.currentTimeMillis()}",
                        "name": "Mock Cow",
                        "breed": "Gir",
                        "age": 3,
                        "weight": 400,
                        "currentYield": 10.0,
                        "targetYield": 12.0
                    }
                }
            """.trimIndent())
        }

        if (path.endsWith("/api/cows") && method == "GET") {
            return createResponse(chain, """
                {
                    "success": true,
                    "count": 2,
                    "data": [
                        {
                            "_id": "c1",
                            "name": "Ganga",
                            "breed": "Gir",
                            "age": 5,
                            "weight": 450,
                            "currentYield": 12.5,
                            "targetYield": 15.0,
                            "status": ["Milk Cow", "Healthy"]
                        },
                        {
                            "_id": "c2",
                            "name": "Yamuna",
                            "breed": "Sahiwal",
                            "age": 4,
                            "weight": 420,
                            "currentYield": 10.0,
                            "targetYield": 12.0,
                            "status": ["Milk Cow", "Healthy"]
                        }
                    ]
                }
            """.trimIndent())
        }
        
        if (path.contains("/api/diseases/summary")) {
             return createResponse(chain, """
                {
                    "success": true,
                    "data": {
                        "stats": {
                            "totalCases": 5,
                            "activeCases": 2,
                            "recovered": 3
                        },
                        "diseaseDistribution": {
                            "Mastitis": 2,
                            "FMD": 1,
                            "Lumpy": 2
                        },
                        "cases": [
                            {
                                "_id": "d1",
                                "cow": { "name": "Ganga", "profileImage": null },
                                "diseaseName": "Mastitis",
                                "status": "Active",
                                "severity": "High",
                                "detectedOn": "2024-03-20T10:00:00Z"
                            }
                        ]
                    }
                }
            """.trimIndent())
        }

        Log.d("MockInterceptor", "No mock match, proceeding with real request")
        return chain.proceed(request)
    }

    private fun createResponse(chain: Interceptor.Chain, json: String, code: Int = 200): Response {
        return Response.Builder()
            .request(chain.request())
            .protocol(Protocol.HTTP_1_1)
            .code(code)
            .message("OK")
            .body(json.toResponseBody("application/json".toMediaTypeOrNull()))
            .addHeader("content-type", "application/json")
            .build()
    }
}
