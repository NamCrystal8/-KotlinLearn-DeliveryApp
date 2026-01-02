package com.example.deliveryapp.ui.navigation

/**
 * Navigation routes for the app.
 */
sealed class Screen(val route: String) {
    // Auth screens
    object Login : Screen("login")
    object Register : Screen("register")
    
    // Customer screens
    object CustomerHome : Screen("customer/home")
    object CreateOrder : Screen("customer/create_order")
    object TrackOrder : Screen("customer/track/{orderId}") {
        fun createRoute(orderId: Long) = "customer/track/$orderId"
    }
    
    // Courier screens
    object CourierHome : Screen("courier/home")
    object AvailableOrders : Screen("courier/available_orders")
    object ActiveDelivery : Screen("courier/active/{orderId}") {
        fun createRoute(orderId: Long) = "courier/active/$orderId"
    }
}
