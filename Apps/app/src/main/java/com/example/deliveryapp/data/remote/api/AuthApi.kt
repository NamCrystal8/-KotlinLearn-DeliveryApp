package com.example.deliveryapp.data.remote.api

import com.example.deliveryapp.data.remote.dto.AuthRequest
import com.example.deliveryapp.data.remote.dto.AuthResponse
import com.example.deliveryapp.data.remote.dto.RegisterRequest
import retrofit2.http.Body
import retrofit2.http.POST

/**
 * Retrofit API interface for authentication endpoints.
 */
interface AuthApi {

    @POST("api/auth/login")
    suspend fun login(@Body request: AuthRequest): AuthResponse

    @POST("api/auth/register")
    suspend fun register(@Body request: RegisterRequest): AuthResponse
}
