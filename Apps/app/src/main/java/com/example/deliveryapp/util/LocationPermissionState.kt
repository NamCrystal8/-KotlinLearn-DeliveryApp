package com.example.deliveryapp.util

/**
 * Sealed class representing the current state of location permissions
 */
sealed class LocationPermissionState {
    /**
     * All required location permissions are granted
     */
    data object Granted : LocationPermissionState()
    
    /**
     * Foreground location permission is granted, but background is not
     * (Android 10+ requires separate background location permission)
     */
    data object ForegroundOnly : LocationPermissionState()
    
    /**
     * Permission was denied by the user - should show rationale
     */
    data object ShowRationale : LocationPermissionState()
    
    /**
     * Permission was denied by the user
     */
    data object Denied : LocationPermissionState()
    
    /**
     * Permission was permanently denied - user must go to settings
     */
    data object PermanentlyDenied : LocationPermissionState()
    
    /**
     * Initial state - permissions haven't been checked yet
     */
    data object NotDetermined : LocationPermissionState()
}
