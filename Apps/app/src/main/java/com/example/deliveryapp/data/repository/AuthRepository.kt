package com.example.deliveryapp.data.repository

import com.example.deliveryapp.data.local.TokenManager
import com.example.deliveryapp.data.remote.api.AuthApi
import com.example.deliveryapp.data.remote.dto.AuthRequest
import com.example.deliveryapp.data.remote.dto.AuthResponse
import com.example.deliveryapp.data.remote.dto.RegisterRequest
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Repository for authentication operations.
 */
@Singleton
class AuthRepository @Inject constructor(
    private val authApi: AuthApi,
    private val tokenManager: TokenManager
) {

    suspend fun login(username: String, password: String): Result<AuthResponse> {
        return try {
            val response = authApi.login(AuthRequest(username, password))
            tokenManager.saveAuthData(
                token = response.token,
                userId = response.userId,
                username = response.username,
                role = response.role
            )
            Result.success(response)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun register(username: String, password: String, role: String): Result<AuthResponse> {
        return try {
            val response = authApi.register(RegisterRequest(username, password, role))
            tokenManager.saveAuthData(
                token = response.token,
                userId = response.userId,
                username = response.username,
                role = response.role
            )
            Result.success(response)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun logout() {
        tokenManager.clearAll()
    }

    suspend fun isLoggedIn(): Boolean {
        return tokenManager.isLoggedIn()
    }

    fun getRole() = tokenManager.role
    
    fun getUserId() = tokenManager.userId
    
    fun getUsername() = tokenManager.username
}
