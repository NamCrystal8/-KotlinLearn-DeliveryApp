package com.example.delivery.controller;

import com.example.delivery.dto.request.CreateOrderRequest;
import com.example.delivery.dto.request.UpdateOrderStatusRequest;
import com.example.delivery.dto.response.OrderResponse;
import com.example.delivery.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST Controller for Order operations.
 */
@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;

    /**
     * POST /api/orders - Customer creates an order.
     */
    @PostMapping
    public ResponseEntity<OrderResponse> createOrder(@RequestBody CreateOrderRequest request) {
        OrderResponse response = orderService.createOrder(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /**
     * GET /api/orders/open - Courier fetches available orders (Status = CREATED).
     */
    @GetMapping("/open")
    public ResponseEntity<List<OrderResponse>> getOpenOrders() {
        List<OrderResponse> orders = orderService.getOpenOrders();
        return ResponseEntity.ok(orders);
    }

    /**
     * GET /api/orders/{id} - Get order by ID.
     */
    @GetMapping("/{id}")
    public ResponseEntity<OrderResponse> getOrderById(@PathVariable Long id) {
        OrderResponse response = orderService.getOrderById(id);
        return ResponseEntity.ok(response);
    }

    /**
     * POST /api/orders/{id}/accept - Courier accepts an order.
     */
    @PostMapping("/{id}/accept")
    public ResponseEntity<OrderResponse> acceptOrder(
            @PathVariable Long id,
            @RequestParam Long courierId) {
        OrderResponse response = orderService.acceptOrder(id, courierId);
        return ResponseEntity.ok(response);
    }

    /**
     * PATCH /api/orders/{id}/status - Update order status (PICKED_UP, DELIVERED).
     */
    @PatchMapping("/{id}/status")
    public ResponseEntity<OrderResponse> updateOrderStatus(
            @PathVariable Long id,
            @RequestBody UpdateOrderStatusRequest request) {
        OrderResponse response = orderService.updateOrderStatus(id, request.getStatus());
        return ResponseEntity.ok(response);
    }
}
