package com.example.deliveryapp

import android.app.Application
import dagger.hilt.android.HiltAndroidApp

/**
 * Application class annotated with @HiltAndroidApp to enable Hilt dependency injection.
 */
@HiltAndroidApp
class DeliveryApp : Application()
