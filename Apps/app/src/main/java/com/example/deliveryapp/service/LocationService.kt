package com.example.deliveryapp.service

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.Binder
import android.os.Build
import android.os.IBinder
import android.os.Looper
import android.util.Log
import androidx.core.app.NotificationCompat
import com.example.deliveryapp.MainActivity
import com.example.deliveryapp.R
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

/**
 * Foreground Service for capturing courier GPS location.
 * Runs with a persistent notification so location updates continue
 * even when the app is minimized or in the background.
 */
class LocationService : Service() {

    companion object {
        private const val TAG = "LocationService"
        private const val NOTIFICATION_ID = 1001
        private const val CHANNEL_ID = "location_service_channel"
        
        // Location update parameters (battery optimized)
        private const val UPDATE_INTERVAL_MS = 5000L      // 5 seconds
        private const val FASTEST_INTERVAL_MS = 3000L     // 3 seconds minimum
        private const val MIN_DISPLACEMENT_METERS = 10f   // 10 meters minimum distance
        
        // Actions for controlling the service
        const val ACTION_START = "com.example.deliveryapp.action.START_LOCATION"
        const val ACTION_STOP = "com.example.deliveryapp.action.STOP_LOCATION"
        
        // Extra keys
        const val EXTRA_ORDER_ID = "extra_order_id"
    }

    // Binder for Activity/ViewModel binding
    private val binder = LocationBinder()
    
    // Location client
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var locationCallback: LocationCallback
    
    // Current location as StateFlow for observers
    private val _currentLocation = MutableStateFlow<LocationData?>(null)
    val currentLocation: StateFlow<LocationData?> = _currentLocation.asStateFlow()
    
    // Tracking state
    private val _isTracking = MutableStateFlow(false)
    val isTracking: StateFlow<Boolean> = _isTracking.asStateFlow()
    
    // Current order being tracked
    private var currentOrderId: Long? = null

    inner class LocationBinder : Binder() {
        fun getService(): LocationService = this@LocationService
    }

    override fun onCreate() {
        super.onCreate()
        Log.d(TAG, "LocationService onCreate")
        
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        
        locationCallback = object : LocationCallback() {
            override fun onLocationResult(result: LocationResult) {
                result.lastLocation?.let { location ->
                    val locationData = LocationData(
                        latitude = location.latitude,
                        longitude = location.longitude,
                        accuracy = location.accuracy,
                        timestamp = System.currentTimeMillis(),
                        orderId = currentOrderId
                    )
                    _currentLocation.value = locationData
                    Log.d(TAG, "Location update: ${location.latitude}, ${location.longitude}")
                }
            }
        }
        
        createNotificationChannel()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        Log.d(TAG, "onStartCommand: ${intent?.action}")
        
        when (intent?.action) {
            ACTION_START -> {
                currentOrderId = intent.getLongExtra(EXTRA_ORDER_ID, -1).takeIf { it != -1L }
                startForegroundService()
                startLocationUpdates()
            }
            ACTION_STOP -> {
                stopLocationUpdates()
                stopForeground(STOP_FOREGROUND_REMOVE)
                stopSelf()
            }
        }
        
        return START_STICKY
    }

    override fun onBind(intent: Intent?): IBinder {
        return binder
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.d(TAG, "LocationService onDestroy")
        stopLocationUpdates()
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                getString(R.string.location_channel_name),
                NotificationManager.IMPORTANCE_LOW
            ).apply {
                description = getString(R.string.location_channel_description)
                setShowBadge(false)
            }
            
            val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }

    private fun startForegroundService() {
        val notification = createNotification()
        startForeground(NOTIFICATION_ID, notification)
    }

    private fun createNotification(): Notification {
        val intent = Intent(this, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        
        val pendingIntent = PendingIntent.getActivity(
            this,
            0,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        
        return NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle(getString(R.string.location_notification_title))
            .setContentText(getString(R.string.location_notification_text))
            .setSmallIcon(R.drawable.ic_location)
            .setContentIntent(pendingIntent)
            .setOngoing(true)
            .setPriority(NotificationCompat.PRIORITY_LOW)
            .setCategory(NotificationCompat.CATEGORY_SERVICE)
            .build()
    }

    @Suppress("MissingPermission")
    private fun startLocationUpdates() {
        Log.d(TAG, "Starting location updates")
        
        val locationRequest = LocationRequest.Builder(
            Priority.PRIORITY_HIGH_ACCURACY,
            UPDATE_INTERVAL_MS
        )
            .setMinUpdateIntervalMillis(FASTEST_INTERVAL_MS)
            .setMinUpdateDistanceMeters(MIN_DISPLACEMENT_METERS)
            .build()
        
        try {
            fusedLocationClient.requestLocationUpdates(
                locationRequest,
                locationCallback,
                Looper.getMainLooper()
            )
            _isTracking.value = true
        } catch (e: SecurityException) {
            Log.e(TAG, "Lost location permission", e)
            _isTracking.value = false
        }
    }

    private fun stopLocationUpdates() {
        Log.d(TAG, "Stopping location updates")
        fusedLocationClient.removeLocationUpdates(locationCallback)
        _isTracking.value = false
        _currentLocation.value = null
    }
}

/**
 * Data class representing a location update
 */
data class LocationData(
    val latitude: Double,
    val longitude: Double,
    val accuracy: Float,
    val timestamp: Long,
    val orderId: Long? = null
)
