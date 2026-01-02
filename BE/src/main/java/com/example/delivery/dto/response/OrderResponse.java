package com.example.delivery.dto.response;

import com.example.delivery.domain.enums.OrderStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * Response DTO for order data.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderResponse {

    private Long id;

    private Long customerId;
    private String customerUsername;

    private Long courierId;
    private String courierUsername;

    // Pickup location
    private String pickupAddress;
    private Double pickupLat;
    private Double pickupLng;

    // Dropoff location
    private String dropoffAddress;
    private Double dropoffLat;
    private Double dropoffLng;

    private OrderStatus status;
    private LocalDateTime createdAt;
}
