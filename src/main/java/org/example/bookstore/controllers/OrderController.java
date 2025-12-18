package org.example.bookstore.controllers;

import lombok.RequiredArgsConstructor;
import org.example.bookstore.dto.OrderResponseDto;
import org.example.bookstore.services.OrderService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequestMapping("/api/orders/{userId}")
@RequiredArgsConstructor
@RestController
public class OrderController {

    private final OrderService service;

    @PostMapping
    public OrderResponseDto createOrder(@PathVariable Long userId){
        return service.createOrder(userId);
    }

    @GetMapping
    public List<OrderResponseDto> getOrders(@PathVariable Long userId){
        return service.getOrders(userId);
    }

    @GetMapping("/{orderId}")
    public OrderResponseDto getOrderById(@PathVariable Long userId, @PathVariable Long orderId){
        return service.getOrderById(userId, orderId);
    }

}
