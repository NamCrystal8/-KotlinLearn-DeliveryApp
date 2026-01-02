package com.example.delivery.domain.enums;

/**
 * Represents the status of an order in the delivery lifecycle.
 */
public enum OrderStatus {
    CREATED,
    ACCEPTED,
    PICKED_UP,
    DELIVERED,
    CANCELLED
}
