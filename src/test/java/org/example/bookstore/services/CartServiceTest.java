package org.example.bookstore.services;

import org.example.bookstore.dto.AddToCartRequestDto;
import org.example.bookstore.dto.CartItemResponseDto;
import org.example.bookstore.entities.Book;
import org.example.bookstore.entities.CartItem;
import org.example.bookstore.entities.User;
import org.example.bookstore.repositories.BookRepository;
import org.example.bookstore.repositories.CartItemRepository;
import org.example.bookstore.repositories.UserRepository;
import org.example.bookstore.security.SecurityUtil;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CartServiceTest {

    @Mock
    private CartItemRepository cartItemRepository;

    @Mock
    private BookRepository bookRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private SecurityUtil securityUtil;

    @InjectMocks
    private CartService cartService;

    @Test
    void addToCart_newItem_createsAndReturnsDto() {
        // given
        String testEmail = "test@example.com";

        User testUser = User.builder()
                .id(1L)
                .email(testEmail)
                .build();

        Book book = Book.builder()
                .id(1L)
                .title("Test Book")
                .price(new BigDecimal("1000"))
                .stock(10)
                .build();

        CartItem savedItem = CartItem.builder()
                .id(1L)
                .user(testUser)
                .book(book)
                .quantity(2)
                .build();

        when(securityUtil.getCurrentUserEmail()).thenReturn(testEmail);
        when(userRepository.findUserByEmail(testEmail)).thenReturn(Optional.of(testUser));  // <-- мок для user
        when(bookRepository.findById(1L)).thenReturn(Optional.of(book));
        when(cartItemRepository.findByUserEmailAndBook(testEmail, book)).thenReturn(Optional.empty());
        when(cartItemRepository.save(any(CartItem.class))).thenReturn(savedItem);

        AddToCartRequestDto request = new AddToCartRequestDto();
        request.setBookId(1L);
        request.setQuantity(2);

        // when
        CartItemResponseDto result = cartService.addToCart(request);

        // then
        assertNotNull(result);
        assertEquals("Test Book", result.getTitle());
        assertEquals(2, result.getQuantity());
        assertEquals(new BigDecimal("2000"), result.getSubtotal());

        verify(cartItemRepository).save(any(CartItem.class));
    }

    @Test
    void getCart_returnsListOfDto() {
        String testEmail = "test@example.com";

        when(securityUtil.getCurrentUserEmail()).thenReturn(testEmail);

        Book book = Book.builder()
                .id(1L)
                .title("Book")
                .price(new BigDecimal("1000"))
                .build();

        CartItem item = CartItem.builder()
                .book(book)
                .quantity(1)
                .build();

        when(cartItemRepository.findByUserEmail(testEmail)).thenReturn(List.of(item));

        List<CartItemResponseDto> result = cartService.getCart();

        assertEquals(1, result.size());
        assertEquals("Book", result.get(0).getTitle());
    }
}