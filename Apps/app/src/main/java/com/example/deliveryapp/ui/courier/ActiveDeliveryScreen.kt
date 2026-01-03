package com.example.deliveryapp.ui.courier

import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.deliveryapp.R
import com.example.deliveryapp.service.LocationData
import com.example.deliveryapp.service.LocationService
import com.example.deliveryapp.ui.components.DeliveryMapView
import com.example.deliveryapp.util.PermissionUtils
import com.google.android.gms.maps.model.LatLng

/**
 * Screen for couriers to manage their active delivery.
 * Includes map with pickup/dropoff, location tracking controls, and status updates.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ActiveDeliveryScreen(
    orderId: Long,
    onNavigateBack: () -> Unit
) {
    val context = LocalContext.current
    var isMapLoaded by remember { mutableStateOf(false) }
    var isTracking by remember { mutableStateOf(false) }
    var currentLocation by remember { mutableStateOf<LocationData?>(null) }
    var showPermissionDialog by remember { mutableStateOf(false) }
    var showBackgroundPermissionDialog by remember { mutableStateOf(false) }
    
    // Placeholder coordinates (will be replaced with actual order data)
    val pickupLocation = remember { LatLng(16.0544, 108.2022) }
    val dropoffLocation = remember { LatLng(16.0600, 108.2100) }
    
    // Convert current location to LatLng for map
    val courierLatLng = currentLocation?.let { LatLng(it.latitude, it.longitude) }
    
    // Permission launcher for foreground location
    val foregroundPermissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        val allGranted = permissions.values.all { it }
        if (allGranted) {
            // Check if we need background permission too
            if (PermissionUtils.needsBackgroundLocationRequest(context)) {
                showBackgroundPermissionDialog = true
            } else {
                startLocationService(context, orderId)
                isTracking = true
            }
        } else {
            showPermissionDialog = true
        }
    }
    
    // Permission launcher for background location (Android 10+)
    val backgroundPermissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { granted ->
        if (granted) {
            startLocationService(context, orderId)
            isTracking = true
        }
        // Even if denied, we can still track in foreground
        // Just won't work when app is fully in background
    }
    
    // Permission dialog
    if (showPermissionDialog) {
        AlertDialog(
            onDismissRequest = { showPermissionDialog = false },
            title = { Text(stringResource(R.string.permission_required_title)) },
            text = { Text(stringResource(R.string.location_permission_rationale)) },
            confirmButton = {
                TextButton(onClick = { 
                    showPermissionDialog = false
                    foregroundPermissionLauncher.launch(PermissionUtils.FOREGROUND_LOCATION_PERMISSIONS)
                }) {
                    Text(stringResource(R.string.grant_permission))
                }
            },
            dismissButton = {
                TextButton(onClick = { showPermissionDialog = false }) {
                    Text(stringResource(R.string.cancel))
                }
            }
        )
    }
    
    // Background permission dialog
    if (showBackgroundPermissionDialog) {
        AlertDialog(
            onDismissRequest = { showBackgroundPermissionDialog = false },
            title = { Text(stringResource(R.string.background_location_title)) },
            text = { Text(stringResource(R.string.background_location_rationale)) },
            confirmButton = {
                TextButton(onClick = {
                    showBackgroundPermissionDialog = false
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                        backgroundPermissionLauncher.launch(PermissionUtils.BACKGROUND_LOCATION_PERMISSION)
                    }
                }) {
                    Text(stringResource(R.string.allow_background))
                }
            },
            dismissButton = {
                TextButton(onClick = {
                    showBackgroundPermissionDialog = false
                    // Start tracking anyway without background permission
                    startLocationService(context, orderId)
                    isTracking = true
                }) {
                    Text(stringResource(R.string.skip))
                }
            }
        )
    }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.active_delivery_title)) },
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
                    courierLocation = courierLatLng,
                    showMyLocation = isTracking,
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
            
            // Controls at bottom
            DeliveryControlsCard(
                orderId = orderId,
                isTracking = isTracking,
                currentLocation = currentLocation,
                onStartTracking = {
                    if (PermissionUtils.hasForegroundLocationPermission(context)) {
                        if (PermissionUtils.needsBackgroundLocationRequest(context)) {
                            showBackgroundPermissionDialog = true
                        } else {
                            startLocationService(context, orderId)
                            isTracking = true
                        }
                    } else {
                        foregroundPermissionLauncher.launch(
                            PermissionUtils.getMissingLocationPermissions(context).toTypedArray()
                        )
                    }
                },
                onStopTracking = {
                    stopLocationService(context)
                    isTracking = false
                    currentLocation = null
                },
                onUpdateStatus = { newStatus ->
                    // TODO: Call API to update order status
                }
            )
        }
    }
}

@Composable
private fun DeliveryControlsCard(
    orderId: Long,
    isTracking: Boolean,
    currentLocation: LocationData?,
    onStartTracking: () -> Unit,
    onStopTracking: () -> Unit,
    onUpdateStatus: (String) -> Unit
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
            // Order info
            Text(
                text = stringResource(R.string.order_number, orderId),
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            // Current location display
            currentLocation?.let { location ->
                Text(
                    text = stringResource(
                        R.string.current_location_display,
                        location.latitude,
                        location.longitude
                    ),
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.height(8.dp))
            }
            
            // Tracking toggle button
            Button(
                onClick = { if (isTracking) onStopTracking() else onStartTracking() },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (isTracking) 
                        MaterialTheme.colorScheme.error 
                    else 
                        MaterialTheme.colorScheme.primary
                )
            ) {
                Icon(
                    if (isTracking) Icons.Default.Close else Icons.Default.LocationOn,
                    contentDescription = null,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    if (isTracking) 
                        stringResource(R.string.stop_tracking) 
                    else 
                        stringResource(R.string.start_tracking)
                )
            }
            
            Spacer(modifier = Modifier.height(12.dp))
            
            // Status update buttons
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedButton(
                    onClick = { onUpdateStatus("PICKED_UP") },
                    modifier = Modifier.weight(1f)
                ) {
                    Text(stringResource(R.string.picked_up))
                }
                
                Button(
                    onClick = { onUpdateStatus("DELIVERED") },
                    modifier = Modifier.weight(1f)
                ) {
                    Text(stringResource(R.string.delivered))
                }
            }
        }
    }
}

private fun startLocationService(context: Context, orderId: Long) {
    val intent = Intent(context, LocationService::class.java).apply {
        action = LocationService.ACTION_START
        putExtra(LocationService.EXTRA_ORDER_ID, orderId)
    }
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        context.startForegroundService(intent)
    } else {
        context.startService(intent)
    }
}

private fun stopLocationService(context: Context) {
    val intent = Intent(context, LocationService::class.java).apply {
        action = LocationService.ACTION_STOP
    }
    context.startService(intent)
}
