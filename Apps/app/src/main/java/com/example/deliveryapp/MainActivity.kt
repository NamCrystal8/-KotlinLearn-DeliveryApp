package com.example.deliveryapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.navigation.compose.rememberNavController
import com.example.deliveryapp.data.local.TokenManager
import com.example.deliveryapp.domain.model.Role
import com.example.deliveryapp.ui.navigation.NavGraph
import com.example.deliveryapp.ui.navigation.Screen
import com.example.deliveryapp.ui.theme.DeliveryAppTheme
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    @Inject
    lateinit var tokenManager: TokenManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            DeliveryAppTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    val navController = rememberNavController()
                    val token by tokenManager.token.collectAsState(initial = null)
                    val roleString by tokenManager.role.collectAsState(initial = null)
                    var startDestination by remember { mutableStateOf<String?>(null) }

                    LaunchedEffect(token, roleString) {
                        val role = Role.fromString(roleString)
                        startDestination = when {
                            token != null && role == Role.CUSTOMER -> Screen.CustomerHome.route
                            token != null && role == Role.COURIER -> Screen.CourierHome.route
                            else -> Screen.Login.route
                        }
                    }

                    startDestination?.let { destination ->
                        NavGraph(
                            navController = navController,
                            startDestination = destination
                        )
                    }
                }
            }
        }
    }
}