package com.example.deliveryapp.util

import com.example.deliveryapp.data.local.TokenManager
import kotlinx.coroutines.runBlocking
import okhttp3.Interceptor
import okhttp3.Response

/**
 * OkHttp interceptor that adds JWT token to Authorization header.
 */
class AuthInterceptor(private val tokenManager: TokenManager) : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        val originalRequest = chain.request()
        
        // Skip auth header for auth endpoints
        if (originalRequest.url.encodedPath.contains("/api/auth/")) {
            return chain.proceed(originalRequest)
        }

        val token = runBlocking { tokenManager.getTokenSync() }
        
        return if (token != null) {
            val newRequest = originalRequest.newBuilder()
                .header("Authorization", "Bearer $token")
                .build()
            chain.proceed(newRequest)
        } else {
            chain.proceed(originalRequest)
        }
    }
}
