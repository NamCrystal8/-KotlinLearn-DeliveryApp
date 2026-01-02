package com.example.deliveryapp.data.remote.api

import com.example.deliveryapp.data.remote.dto.CreateOrderRequest
import com.example.deliveryapp.data.remote.dto.OrderResponse
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

/**
 * Retrofit API interface for order endpoints.
 */
interface OrderApi {

    @POST("api/orders")
    suspend fun createOrder(@Body request: CreateOrderRequest): OrderResponse

    @GET("api/orders/open")
    suspend fun getOpenOrders(): List<OrderResponse>

    @GET("api/orders/{id}")
    suspend fun getOrderById(@Path("id") orderId: Long): OrderResponse

    @POST("api/orders/{id}/accept")
    suspend fun acceptOrder(
        @Path("id") orderId: Long,
        @Query("courierId") courierId: Long
    ): OrderResponse
}
