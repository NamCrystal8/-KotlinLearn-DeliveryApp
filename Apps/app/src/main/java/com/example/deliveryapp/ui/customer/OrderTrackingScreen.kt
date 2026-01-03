package com.example.deliveryapp.ui.customer

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.deliveryapp.R
import com.example.deliveryapp.ui.components.DeliveryMapView
import com.google.android.gms.maps.model.LatLng

/**
 * Screen for customers to track their active order on a map.
 * Shows pickup, dropoff, and real-time courier location.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OrderTrackingScreen(
    orderId: Long,
    onNavigateBack: () -> Unit
) {
    // TODO: In Phase 4, these will come from WebSocket real-time updates
    // For now, using placeholder data to demonstrate the map UI
    
    var isMapLoaded by remember { mutableStateOf(false) }
    
    // Placeholder coordinates (will be replaced with actual order data)
    val pickupLocation = remember { LatLng(16.0544, 108.2022) }
    val dropoffLocation = remember { LatLng(16.0600, 108.2100) }
    
    // Courier location will be updated via WebSocket in Phase 4
    val courierLocation: LatLng? = null
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.tracking_order_title, orderId)) },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = stringResource(R.string.back_content_desc)
                        )
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            // Map takes most of the screen
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
            ) {
                DeliveryMapView(
                    pickupLocation = pickupLocation,
                    dropoffLocation = dropoffLocation,
                    courierLocation = courierLocation,
                    onMapLoaded = { isMapLoaded = true }
                )
                
                // Loading overlay
                if (!isMapLoaded) {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator()
                    }
                }
            }
            
            // Order status card at bottom
            OrderStatusCard(
                orderId = orderId,
                status = "ACCEPTED", // TODO: Get from actual order data
                courierLocation = courierLocation
            )
        }
    }
}

@Composable
private fun OrderStatusCard(
    orderId: Long,
    status: String,
    courierLocation: LatLng?
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = stringResource(R.string.order_number, orderId),
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )
                
                StatusChip(status = status)
            }
            
            Spacer(modifier = Modifier.height(12.dp))
            
            if (courierLocation != null) {
                Text(
                    text = stringResource(R.string.courier_on_the_way),
                    fontSize = 14.sp,
                    color = MaterialTheme.colorScheme.primary
                )
            } else {
                Text(
                    text = stringResource(R.string.waiting_for_courier_location),
                    fontSize = 14.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@Composable
private fun StatusChip(status: String) {
    val (backgroundColor, textColor) = when (status) {
        "CREATED" -> MaterialTheme.colorScheme.secondaryContainer to MaterialTheme.colorScheme.onSecondaryContainer
        "ACCEPTED" -> MaterialTheme.colorScheme.primaryContainer to MaterialTheme.colorScheme.onPrimaryContainer
        "PICKED_UP" -> MaterialTheme.colorScheme.tertiaryContainer to MaterialTheme.colorScheme.onTertiaryContainer
        "DELIVERED" -> MaterialTheme.colorScheme.primary to MaterialTheme.colorScheme.onPrimary
        else -> MaterialTheme.colorScheme.surfaceVariant to MaterialTheme.colorScheme.onSurfaceVariant
    }
    
    Surface(
        color = backgroundColor,
        shape = MaterialTheme.shapes.small
    ) {
        Text(
            text = status,
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp),
            color = textColor,
            fontSize = 12.sp,
            fontWeight = FontWeight.Medium
        )
    }
}
