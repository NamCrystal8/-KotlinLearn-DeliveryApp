package com.example.deliveryapp.ui.components

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.MapProperties
import com.google.maps.android.compose.MapUiSettings
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.MarkerState
import com.google.maps.android.compose.rememberCameraPositionState

/**
 * Reusable composable for displaying delivery map with pickup, dropoff, and courier markers.
 *
 * @param pickupLocation The pickup location coordinates
 * @param dropoffLocation The dropoff location coordinates
 * @param courierLocation The current courier location (optional, for customer tracking)
 * @param modifier Modifier for the composable
 * @param showMyLocation Whether to show the user's current location on the map
 * @param onMapLoaded Callback when the map is fully loaded
 */
@Composable
fun DeliveryMapView(
    pickupLocation: LatLng?,
    dropoffLocation: LatLng?,
    courierLocation: LatLng? = null,
    modifier: Modifier = Modifier,
    showMyLocation: Boolean = false,
    onMapLoaded: () -> Unit = {}
) {
    // Default camera position (Da Nang, Vietnam as fallback)
    val defaultPosition = LatLng(16.0544, 108.2022)
    
    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(
            pickupLocation ?: defaultPosition,
            14f
        )
    }
    
    // Map UI settings
    val uiSettings = remember {
        MapUiSettings(
            zoomControlsEnabled = true,
            myLocationButtonEnabled = showMyLocation,
            mapToolbarEnabled = true,
            compassEnabled = true
        )
    }
    
    // Map properties
    val mapProperties = remember(showMyLocation) {
        MapProperties(
            isMyLocationEnabled = showMyLocation
        )
    }
    
    // Fit camera to show all markers when locations change
    LaunchedEffect(pickupLocation, dropoffLocation, courierLocation) {
        val points = listOfNotNull(pickupLocation, dropoffLocation, courierLocation)
        if (points.size >= 2) {
            val boundsBuilder = LatLngBounds.builder()
            points.forEach { boundsBuilder.include(it) }
            val bounds = boundsBuilder.build()
            cameraPositionState.animate(
                CameraUpdateFactory.newLatLngBounds(bounds, 100)
            )
        } else if (points.size == 1) {
            cameraPositionState.animate(
                CameraUpdateFactory.newLatLngZoom(points.first(), 15f)
            )
        }
    }
    
    // Update camera to follow courier location
    LaunchedEffect(courierLocation) {
        courierLocation?.let { location ->
            // Smooth animation to courier position without changing zoom
            cameraPositionState.animate(
                CameraUpdateFactory.newLatLng(location),
                durationMs = 500
            )
        }
    }
    
    GoogleMap(
        modifier = modifier.fillMaxSize(),
        cameraPositionState = cameraPositionState,
        properties = mapProperties,
        uiSettings = uiSettings,
        onMapLoaded = onMapLoaded
    ) {
        // Pickup marker (green)
        pickupLocation?.let { location ->
            Marker(
                state = MarkerState(position = location),
                title = "Pickup",
                snippet = "Pickup location",
                icon = BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN)
            )
        }
        
        // Dropoff marker (red)
        dropoffLocation?.let { location ->
            Marker(
                state = MarkerState(position = location),
                title = "Dropoff",
                snippet = "Delivery destination",
                icon = BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED)
            )
        }
        
        // Courier marker (blue) - only shown when tracking
        courierLocation?.let { location ->
            Marker(
                state = MarkerState(position = location),
                title = "Courier",
                snippet = "Current courier location",
                icon = BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE)
            )
        }
    }
}

/**
 * Simple map view showing a single location marker
 */
@Composable
fun SimpleMapView(
    location: LatLng,
    markerTitle: String = "Location",
    modifier: Modifier = Modifier,
    zoom: Float = 15f
) {
    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(location, zoom)
    }
    
    GoogleMap(
        modifier = modifier.fillMaxSize(),
        cameraPositionState = cameraPositionState,
        uiSettings = MapUiSettings(
            zoomControlsEnabled = true,
            mapToolbarEnabled = false
        )
    ) {
        Marker(
            state = MarkerState(position = location),
            title = markerTitle
        )
    }
}
