package com.example.deliveryapp.data.remote.dto

/**
 * Request DTO for login.
 */
data class AuthRequest(
    val username: String,
    val password: String
)

/**
 * Request DTO for registration.
 */
data class RegisterRequest(
    val username: String,
    val password: String,
    val role: String  // "CUSTOMER" or "COURIER"
)

/**
 * Response DTO for authentication.
 */
data class AuthResponse(
    val token: String,
    val userId: Long,
    val username: String,
    val role: String
)

/**
 * Request DTO for creating an order.
 */
data class CreateOrderRequest(
    val customerId: Long,
    val pickupAddress: String,
    val pickupLat: Double,
    val pickupLng: Double,
    val dropoffAddress: String,
    val dropoffLat: Double,
    val dropoffLng: Double
)

/**
 * Response DTO for order data.
 */
data class OrderResponse(
    val id: Long,
    val customerId: Long,
    val courierId: Long?,
    val pickupAddress: String,
    val pickupLat: Double,
    val pickupLng: Double,
    val dropoffAddress: String,
    val dropoffLat: Double,
    val dropoffLng: Double,
    val status: String,
    val createdAt: String
)
