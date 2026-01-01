package org.example.bookstore.services;

import org.example.bookstore.dto.OrderItemResponseDto;
import org.example.bookstore.dto.OrderResponseDto;
import org.example.bookstore.entities.*;
import org.example.bookstore.repositories.*;
import org.example.bookstore.security.SecurityUtil;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OrderServiceTest {

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private OrderItemRepository orderItemRepository;

    @Mock
    private CartItemRepository cartItemRepository;

    @Mock
    private BookRepository bookRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private SecurityUtil securityUtil;

    @InjectMocks
    private OrderService orderService;

    @Test
    void createOrder_successfullyCreatesOrderFromCart() {
        // given
        String email = "test@example.com";

        User user = User.builder().id(1L).email(email).build();

        Book book = Book.builder()
                .id(1L)
                .title("Test Book")
                .price(new BigDecimal("1000"))
                .stock(5)
                .build();

        CartItem cartItem = CartItem.builder()
                .user(user)
                .book(book)
                .quantity(2)
                .build();

        Order order = Order.builder()
                .user(user)
                .orderDate(LocalDateTime.now())
                .status(OrderStatus.PENDING)
                .totalPrice(new BigDecimal("2000"))
                .build();

        OrderItem orderItem = OrderItem.builder()
                .order(order)
                .book(book)
                .quantity(2)
                .priceAtPurchase(new BigDecimal("1000"))
                .build();

        when(securityUtil.getCurrentUserEmail()).thenReturn(email);
        when(cartItemRepository.findByUserEmail(email)).thenReturn(List.of(cartItem));
        when(userRepository.findUserByEmail(email)).thenReturn(Optional.of(user));

        // Имитируем save: возвращаем тот же объект с id = 1
        when(orderRepository.save(any(Order.class))).thenAnswer(invocation -> {
            Order o = invocation.getArgument(0);
            o.setId(1L);
            return o;
        });

        // Для toResponse — возвращаем orderItem
        when(orderItemRepository.findByOrder(any(Order.class))).thenReturn(List.of(orderItem));

        // Для save OrderItem — просто возвращаем аргумент
        when(orderItemRepository.save(any(OrderItem.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // when
        OrderResponseDto result = orderService.createOrder();

        // then
        assertNotNull(result);
        assertEquals(new BigDecimal("2000"), result.getTotalPrice());
        assertEquals(1, result.getItems().size());
        assertEquals("Test Book", result.getItems().get(0).getTitle());
        assertEquals(2, result.getItems().get(0).getQuantity());

        // Проверяем, что stock уменьшился
        verify(bookRepository).save(book);
        assertEquals(3, book.getStock());

        // Проверяем очистку корзины
        verify(cartItemRepository).deleteAll(List.of(cartItem));

        // Проверяем сохранение OrderItem
        verify(orderItemRepository, times(1)).save(any(OrderItem.class));
    }

    @Test
    void getOrders_returnsListOfDto() {
        String email = "test@example.com";

        Order order = Order.builder()
                .id(1L)
                .orderDate(LocalDateTime.now())
                .status(OrderStatus.PENDING)
                .totalPrice(new BigDecimal("2000"))
                .build();

        when(securityUtil.getCurrentUserEmail()).thenReturn(email);
        when(orderRepository.findByUserEmail(email)).thenReturn(List.of(order));
        when(orderItemRepository.findByOrder(order)).thenReturn(List.of());

        List<OrderResponseDto> result = orderService.getOrders();

        assertEquals(1, result.size());
        assertEquals(1L, result.get(0).getId());
    }

    @Test
    void getOrderById_existingOrder_returnsDto() {
        String email = "test@example.com";

        Order order = Order.builder()
                .id(1L)
                .orderDate(LocalDateTime.now())
                .status(OrderStatus.PENDING)
                .totalPrice(new BigDecimal("2000"))
                .build();

        when(securityUtil.getCurrentUserEmail()).thenReturn(email);
        when(orderRepository.findByUserEmailAndId(email, 1L)).thenReturn(Optional.of(order));
        when(orderItemRepository.findByOrder(order)).thenReturn(List.of());

        OrderResponseDto result = orderService.getOrderById(1L);

        assertNotNull(result);
        assertEquals(1L, result.getId());
    }

    @Test
    void createOrder_emptyCart_throwsException() {
        String email = "test@example.com";

        when(securityUtil.getCurrentUserEmail()).thenReturn(email);
        when(cartItemRepository.findByUserEmail(email)).thenReturn(List.of());

        assertThrows(RuntimeException.class, () -> orderService.createOrder());
    }
}