package com.example.deliveryapp.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.deliveryapp.domain.model.Role
import com.example.deliveryapp.ui.auth.AuthViewModel
import com.example.deliveryapp.ui.auth.LoginScreen
import com.example.deliveryapp.ui.auth.RegisterScreen
import com.example.deliveryapp.ui.customer.CreateOrderScreen
import com.example.deliveryapp.ui.customer.CustomerHomeScreen
import com.example.deliveryapp.ui.courier.AvailableOrdersScreen
import com.example.deliveryapp.ui.courier.CourierHomeScreen

/**
 * Main navigation graph with role-based routing.
 */
@Composable
fun NavGraph(
    navController: NavHostController,
    startDestination: String = Screen.Login.route
) {
    NavHost(
        navController = navController,
        startDestination = startDestination
    ) {
        // Auth screens
        composable(Screen.Login.route) {
            val viewModel: AuthViewModel = hiltViewModel()
            LoginScreen(
                viewModel = viewModel,
                onNavigateToRegister = {
                    navController.navigate(Screen.Register.route)
                },
                onLoginSuccess = { role ->
                    val destination = if (role == Role.CUSTOMER) {
                        Screen.CustomerHome.route
                    } else {
                        Screen.CourierHome.route
                    }
                    navController.navigate(destination) {
                        popUpTo(Screen.Login.route) { inclusive = true }
                    }
                }
            )
        }

        composable(Screen.Register.route) {
            val viewModel: AuthViewModel = hiltViewModel()
            RegisterScreen(
                viewModel = viewModel,
                onNavigateToLogin = {
                    navController.popBackStack()
                },
                onRegisterSuccess = { role ->
                    val destination = if (role == Role.CUSTOMER) {
                        Screen.CustomerHome.route
                    } else {
                        Screen.CourierHome.route
                    }
                    navController.navigate(destination) {
                        popUpTo(Screen.Login.route) { inclusive = true }
                    }
                }
            )
        }

        // Customer screens
        composable(Screen.CustomerHome.route) {
            CustomerHomeScreen(
                onCreateOrder = {
                    navController.navigate(Screen.CreateOrder.route)
                },
                onLogout = {
                    navController.navigate(Screen.Login.route) {
                        popUpTo(0) { inclusive = true }
                    }
                }
            )
        }

        composable(Screen.CreateOrder.route) {
            CreateOrderScreen(
                onOrderCreated = {
                    navController.popBackStack()
                },
                onBack = {
                    navController.popBackStack()
                }
            )
        }

        // Courier screens
        composable(Screen.CourierHome.route) {
            CourierHomeScreen(
                onViewAvailableOrders = {
                    navController.navigate(Screen.AvailableOrders.route)
                },
                onLogout = {
                    navController.navigate(Screen.Login.route) {
                        popUpTo(0) { inclusive = true }
                    }
                }
            )
        }

        composable(Screen.AvailableOrders.route) {
            AvailableOrdersScreen(
                onOrderAccepted = {
                    navController.popBackStack()
                },
                onBack = {
                    navController.popBackStack()
                }
            )
        }
    }
}
