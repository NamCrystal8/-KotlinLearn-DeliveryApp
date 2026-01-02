package com.example.deliveryapp.domain.model

/**
 * User roles in the delivery app.
 * Matches backend Role enum values.
 */
enum class Role(val value: String) {
    CUSTOMER("CUSTOMER"),
    COURIER("COURIER");

    companion object {
        fun fromString(value: String?): Role? {
            return entries.find { it.value == value }
        }
    }
}
