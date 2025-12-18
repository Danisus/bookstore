package org.example.bookstore.services;

import lombok.RequiredArgsConstructor;
import org.example.bookstore.dto.AddToCartRequestDto;
import org.example.bookstore.dto.CartItemResponseDto;
import org.example.bookstore.entities.Book;
import org.example.bookstore.entities.CartItem;
import org.example.bookstore.entities.User;
import org.example.bookstore.repositories.BookRepository;
import org.example.bookstore.repositories.CartItemRepository;
import org.example.bookstore.repositories.UserRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

@RequiredArgsConstructor
@Service
public class CartService {
    private final CartItemRepository cartItemrepository;
    private final UserRepository userRepository;
    private final BookRepository bookRepository;

    public List<CartItemResponseDto> getCart(Long userId){
        List<CartItem> cartItems = cartItemrepository.findByUserId(userId);
        return cartItems.stream().map(this::toResponse).toList();
    }

    public CartItemResponseDto addToCart(Long userId, AddToCartRequestDto requestDto){
        User user = userRepository.findById(userId).orElseThrow(() -> new RuntimeException("пользователь с id " + userId + " не найден"));
        Book book = bookRepository.findById(requestDto.getBookId()).orElseThrow(() -> new RuntimeException("книга с id " + requestDto.getBookId() + " не найдена"));
        if (book.getStock() < requestDto.getQuantity())
            throw new RuntimeException("недостаточно книг на складе");

        CartItem cartItem = cartItemrepository.findByUserAndBook(user, book);
        CartItem res;
        if (cartItem != null){
            int newQuantity = cartItem.getQuantity() + requestDto.getQuantity();
            if (book.getStock() < newQuantity)
                throw new RuntimeException("недостаточно книг на складе");
            cartItem.setQuantity(newQuantity);
            res = cartItemrepository.save(cartItem);
        }else {
            CartItem item = CartItem.builder()
                    .user(user)
                    .book(book)
                    .quantity(requestDto.getQuantity())
                    .build();

            res = cartItemrepository.save(item);
        }
        return toResponse(res);
    }

    public void updateQuantity(Long userId, Long bookId, Integer newQuantity){
        Book book = bookRepository.findById(bookId).orElseThrow(() -> new RuntimeException("книга с id " + bookId + " не найдена"));
        User user = userRepository.findById(userId).orElseThrow(() -> new RuntimeException("пользователь с id " + userId + " не найден"));
        CartItem item = cartItemrepository.findByUserAndBook(user, book);
        if (newQuantity <= 0) {
            cartItemrepository.delete(item);
            throw new RuntimeException("кол-во не может быть 0 или меньше");
        }
        if (book.getStock() < newQuantity)
            throw new RuntimeException("недостаточно книг на складе");
        item.setQuantity(newQuantity);
        cartItemrepository.save(item);
    }

    public void removeFromCart(Long userId, Long bookId){
        Book book = bookRepository.findById(bookId).orElseThrow(() -> new RuntimeException("книга с id " + bookId + " не найдена"));
        User user = userRepository.findById(userId).orElseThrow(() -> new RuntimeException("пользователь с id " + userId + " не найден"));
        CartItem item = cartItemrepository.findByUserAndBook(user, book);
        cartItemrepository.delete(item);
    }

    public void clearCart(Long userId){
        List<CartItem> cartItems = cartItemrepository.findByUserId(userId);
        for(CartItem item: cartItems)
            cartItemrepository.delete(item);
    }



    private CartItemResponseDto toResponse(CartItem item){
        Book book = item.getBook();
        return CartItemResponseDto.builder()
                .bookId(book.getId())
                .title(book.getTitle())
                .author(book.getAuthor())
                .price(book.getPrice())
                .quantity(item.getQuantity())
                .subtotal(book.getPrice().multiply(new BigDecimal(item.getQuantity())))
                .build();
    }

}
