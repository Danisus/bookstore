package org.example.bookstore.controllers;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.bookstore.dto.AddToCartRequestDto;
import org.example.bookstore.dto.CartItemResponseDto;
import org.example.bookstore.services.CartService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/cart")
public class CartController {

    private final CartService service;

    @GetMapping("/{userId}")
    public List<CartItemResponseDto> getCart(@PathVariable Long userId){
        return service.getCart(userId);
    }

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping("/{userId}/add")
    public CartItemResponseDto addToCart(@PathVariable Long userId, @RequestBody @Valid AddToCartRequestDto requestDto){
        return service.addToCart(userId, requestDto);
    }

    @PutMapping("/{userId}/update/{bookId}")
    public void updateQuantity(@PathVariable Long userId, @PathVariable Long bookId, @RequestBody Integer newQuantity){
        service.updateQuantity(userId, bookId, newQuantity);
    }

    @ResponseStatus(HttpStatus.NO_CONTENT)
    @DeleteMapping("/{userId}/delete/{bookId}")
    public void removeFromCart(@PathVariable Long userId, @PathVariable Long bookId){
        service.removeFromCart(userId, bookId);
    }
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @DeleteMapping("/{userId}/clear")
    public void clearCart(@PathVariable Long userId){
        service.clearCart(userId);
    }

}
