package org.example.bookstore.controllers;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.bookstore.dto.AddToCartRequestDto;
import org.example.bookstore.dto.CartItemResponseDto;
import org.example.bookstore.services.CartService;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@PreAuthorize("hasRole('USER')")
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/cart")
public class CartController {

    private final CartService service;

    @GetMapping
    public List<CartItemResponseDto> getCart(){
        return service.getCart();
    }

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping("/add")
    public CartItemResponseDto addToCart(@RequestBody @Valid AddToCartRequestDto requestDto){
        return service.addToCart(requestDto);
    }

    @PutMapping("/update/{bookId}")
    public void updateQuantity(@PathVariable Long bookId, @RequestBody Integer newQuantity){
        service.updateQuantity(bookId, newQuantity);
    }

    @ResponseStatus(HttpStatus.NO_CONTENT)
    @DeleteMapping("/delete/{bookId}")
    public void removeFromCart(@PathVariable Long bookId){
        service.removeFromCart(bookId);
    }
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @DeleteMapping("/clear")
    public void clearCart(){
        service.clearCart();
    }

}
