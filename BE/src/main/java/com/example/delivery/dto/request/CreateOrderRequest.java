package com.example.delivery.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Request DTO for creating a new order.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateOrderRequest {

    private Long customerId;

    // Pickup location
    private String pickupAddress;
    private Double pickupLat;
    private Double pickupLng;

    // Dropoff location
    private String dropoffAddress;
    private Double dropoffLat;
    private Double dropoffLng;
}
