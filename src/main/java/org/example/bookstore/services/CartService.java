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
import org.example.bookstore.security.SecurityUtil;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
@Service
public class CartService {
    private final CartItemRepository cartItemrepository;
    private final SecurityUtil securityUtil;
    private final BookRepository bookRepository;
    private final UserRepository userRepository;

    public List<CartItemResponseDto> getCart(){
        String email = securityUtil.getCurrentUserEmail();
        List<CartItem> cartItems = cartItemrepository.findByUserEmail(email);
        return cartItems.stream().map(this::toResponse).toList();
    }

    public CartItemResponseDto addToCart(AddToCartRequestDto requestDto) {
        String email = securityUtil.getCurrentUserEmail();
        Book book = bookRepository.findById(requestDto.getBookId())
                .orElseThrow(() -> new RuntimeException("Книга не найдена"));

        if (book.getStock() < requestDto.getQuantity()) {
            throw new RuntimeException("Недостаточно книг на складе");
        }

        Optional<CartItem> existing = cartItemrepository.findByUserEmailAndBook(email, book);

        CartItem saved;
        if (existing.isPresent()) {
            CartItem item = existing.get();
            int newQuantity = item.getQuantity() + requestDto.getQuantity();
            if (book.getStock() < newQuantity) {
                throw new RuntimeException("Недостаточно книг на складе после добавления");
            }
            item.setQuantity(newQuantity);
            saved = cartItemrepository.save(item);
        } else {
            CartItem newItem = CartItem.builder()
                    .user(userRepository.findUserByEmail(email).orElseThrow()) // только здесь загружаем User
                    .book(book)
                    .quantity(requestDto.getQuantity())
                    .build();
            saved = cartItemrepository.save(newItem);
        }

        return toResponse(saved);
    }
    public void updateQuantity(Long bookId, Integer newQuantity){
        String email = securityUtil.getCurrentUserEmail();
        Book book = bookRepository.findById(bookId).orElseThrow(() -> new RuntimeException("книга с не найдена"));
        User user = userRepository.findUserByEmail(email).orElseThrow(() -> new RuntimeException("пользователь не найден"));
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

    public void removeFromCart(Long bookId){
        String email = securityUtil.getCurrentUserEmail();
        Book book = bookRepository.findById(bookId).orElseThrow(() -> new RuntimeException("книга с не найдена"));
        User user = userRepository.findUserByEmail(email).orElseThrow(() -> new RuntimeException("пользователь не найден"));
        CartItem item = cartItemrepository.findByUserAndBook(user, book);
        cartItemrepository.delete(item);
    }

    public void clearCart(){
        String email = securityUtil.getCurrentUserEmail();
        List<CartItem> cartItems = cartItemrepository.findByUserEmail(email);
        cartItemrepository.deleteAll(cartItems);
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
