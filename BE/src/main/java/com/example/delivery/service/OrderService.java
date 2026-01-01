package com.example.delivery.service;

import com.example.delivery.domain.entity.Order;
import com.example.delivery.domain.entity.User;
import com.example.delivery.domain.enums.OrderStatus;
import com.example.delivery.domain.enums.Role;
import com.example.delivery.dto.request.CreateOrderRequest;
import com.example.delivery.dto.response.OrderResponse;
import com.example.delivery.repository.OrderRepository;
import com.example.delivery.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Service for managing delivery orders.
 */
@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;
    private final UserRepository userRepository;

    /**
     * Create a new order.
     */
    @Transactional
    public OrderResponse createOrder(CreateOrderRequest request) {
        User customer = userRepository.findById(request.getCustomerId())
                .orElseThrow(() -> new IllegalArgumentException("Customer not found: " + request.getCustomerId()));

        if (customer.getRole() != Role.CUSTOMER) {
            throw new IllegalArgumentException("User is not a customer");
        }

        Order order = Order.builder()
                .customer(customer)
                .pickupAddress(request.getPickupAddress())
                .pickupLat(request.getPickupLat())
                .pickupLng(request.getPickupLng())
                .dropoffAddress(request.getDropoffAddress())
                .dropoffLat(request.getDropoffLat())
                .dropoffLng(request.getDropoffLng())
                .status(OrderStatus.CREATED)
                .build();

        Order savedOrder = orderRepository.save(order);
        return mapToResponse(savedOrder);
    }

    /**
     * Get all open orders (status = CREATED) for couriers to accept.
     */
    @Transactional(readOnly = true)
    public List<OrderResponse> getOpenOrders() {
        return orderRepository.findByStatus(OrderStatus.CREATED)
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    /**
     * Get an order by ID.
     */
    @Transactional(readOnly = true)
    public OrderResponse getOrderById(Long orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new IllegalArgumentException("Order not found: " + orderId));
        return mapToResponse(order);
    }

    /**
     * Courier accepts an order.
     */
    @Transactional
    public OrderResponse acceptOrder(Long orderId, Long courierId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new IllegalArgumentException("Order not found: " + orderId));

        if (order.getStatus() != OrderStatus.CREATED) {
            throw new IllegalStateException(
                    "Order is not available for acceptance. Current status: " + order.getStatus());
        }

        User courier = userRepository.findById(courierId)
                .orElseThrow(() -> new IllegalArgumentException("Courier not found: " + courierId));

        if (courier.getRole() != Role.COURIER) {
            throw new IllegalArgumentException("User is not a courier");
        }

        order.setCourier(courier);
        order.setStatus(OrderStatus.ACCEPTED);

        Order savedOrder = orderRepository.save(order);
        return mapToResponse(savedOrder);
    }

    /**
     * Update order status (PICKED_UP, DELIVERED, CANCELLED).
     */
    @Transactional
    public OrderResponse updateOrderStatus(Long orderId, OrderStatus newStatus) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new IllegalArgumentException("Order not found: " + orderId));

        validateStatusTransition(order.getStatus(), newStatus);

        order.setStatus(newStatus);
        Order savedOrder = orderRepository.save(order);
        return mapToResponse(savedOrder);
    }

    /**
     * Validate order status transition.
     */
    private void validateStatusTransition(OrderStatus currentStatus, OrderStatus newStatus) {
        boolean valid = switch (newStatus) {
            case PICKED_UP -> currentStatus == OrderStatus.ACCEPTED;
            case DELIVERED -> currentStatus == OrderStatus.PICKED_UP;
            case CANCELLED -> currentStatus == OrderStatus.CREATED || currentStatus == OrderStatus.ACCEPTED;
            default -> false;
        };

        if (!valid) {
            throw new IllegalStateException(
                    String.format("Invalid status transition from %s to %s", currentStatus, newStatus));
        }
    }

    /**
     * Map Order entity to OrderResponse DTO.
     */
    private OrderResponse mapToResponse(Order order) {
        return OrderResponse.builder()
                .id(order.getId())
                .customerId(order.getCustomer().getId())
                .customerUsername(order.getCustomer().getUsername())
                .courierId(order.getCourier() != null ? order.getCourier().getId() : null)
                .courierUsername(order.getCourier() != null ? order.getCourier().getUsername() : null)
                .pickupAddress(order.getPickupAddress())
                .pickupLat(order.getPickupLat())
                .pickupLng(order.getPickupLng())
                .dropoffAddress(order.getDropoffAddress())
                .dropoffLat(order.getDropoffLat())
                .dropoffLng(order.getDropoffLng())
                .status(order.getStatus())
                .createdAt(order.getCreatedAt())
                .build();
    }
}
