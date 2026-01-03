package com.example.deliveryapp.util

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.content.ContextCompat

/**
 * Utility object for checking and managing location permissions
 */
object PermissionUtils {
    
    /**
     * Required permissions for foreground location
     */
    val FOREGROUND_LOCATION_PERMISSIONS = arrayOf(
        Manifest.permission.ACCESS_FINE_LOCATION,
        Manifest.permission.ACCESS_COARSE_LOCATION
    )
    
    /**
     * Required permission for background location (Android 10+)
     */
    val BACKGROUND_LOCATION_PERMISSION = Manifest.permission.ACCESS_BACKGROUND_LOCATION
    
    /**
     * Required permission for notifications (Android 13+)
     */
    val NOTIFICATION_PERMISSION = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        Manifest.permission.POST_NOTIFICATIONS
    } else null
    
    /**
     * Check if foreground location permission is granted
     */
    fun hasForegroundLocationPermission(context: Context): Boolean {
        return FOREGROUND_LOCATION_PERMISSIONS.all { permission ->
            ContextCompat.checkSelfPermission(context, permission) == PackageManager.PERMISSION_GRANTED
        }
    }
    
    /**
     * Check if background location permission is granted (Android 10+)
     */
    fun hasBackgroundLocationPermission(context: Context): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            ContextCompat.checkSelfPermission(
                context,
                BACKGROUND_LOCATION_PERMISSION
            ) == PackageManager.PERMISSION_GRANTED
        } else {
            // Background location is implicit in older versions if foreground is granted
            hasForegroundLocationPermission(context)
        }
    }
    
    /**
     * Check if notification permission is granted (Android 13+)
     */
    fun hasNotificationPermission(context: Context): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.POST_NOTIFICATIONS
            ) == PackageManager.PERMISSION_GRANTED
        } else {
            // Notification permission is implicit in older versions
            true
        }
    }
    
    /**
     * Check if all required permissions for location tracking are granted
     */
    fun hasAllLocationPermissions(context: Context): Boolean {
        return hasForegroundLocationPermission(context) &&
                hasBackgroundLocationPermission(context) &&
                hasNotificationPermission(context)
    }
    
    /**
     * Get list of permissions that need to be requested for location tracking
     */
    fun getMissingLocationPermissions(context: Context): List<String> {
        val missing = mutableListOf<String>()
        
        // Check foreground location
        if (!hasForegroundLocationPermission(context)) {
            missing.addAll(FOREGROUND_LOCATION_PERMISSIONS)
        }
        
        // Check notification permission (Android 13+)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU && 
            !hasNotificationPermission(context)) {
            missing.add(Manifest.permission.POST_NOTIFICATIONS)
        }
        
        return missing
    }
    
    /**
     * Check if background location permission needs to be requested separately
     * (Android 11+ requires separate request)
     */
    fun needsBackgroundLocationRequest(context: Context): Boolean {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q &&
                hasForegroundLocationPermission(context) &&
                !hasBackgroundLocationPermission(context)
    }
}
