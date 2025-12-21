package org.example.bookstore.controllers;

import lombok.RequiredArgsConstructor;
import org.example.bookstore.dto.OrderResponseDto;
import org.example.bookstore.services.OrderService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@PreAuthorize("hasRole('USER')")
@RequestMapping("/api/orders")
@RequiredArgsConstructor
@RestController
public class OrderController {

    private final OrderService service;

    @PostMapping
    public OrderResponseDto createOrder(){
        return service.createOrder();
    }

    @GetMapping
    public List<OrderResponseDto> getOrders(){
        return service.getOrders();
    }

    @GetMapping("/{orderId}")
    public OrderResponseDto getOrderById(@PathVariable Long orderId){
        return service.getOrderById(orderId);
    }

}
