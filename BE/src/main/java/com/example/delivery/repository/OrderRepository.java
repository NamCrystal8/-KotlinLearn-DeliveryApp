package com.example.delivery.repository;

import com.example.delivery.domain.entity.Order;
import com.example.delivery.domain.enums.OrderStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repository for Order entity operations.
 */
@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {

    /**
     * Find all orders with CREATED status (available for couriers to accept).
     */
    List<Order> findByStatus(OrderStatus status);

    /**
     * Find all orders by customer ID.
     */
    List<Order> findByCustomerId(Long customerId);

    /**
     * Find all orders by courier ID.
     */
    List<Order> findByCourierId(Long courierId);

    /**
     * Find orders by customer ID and status.
     */
    List<Order> findByCustomerIdAndStatus(Long customerId, OrderStatus status);

    /**
     * Find orders by courier ID and status.
     */
    List<Order> findByCourierIdAndStatus(Long courierId, OrderStatus status);
}
