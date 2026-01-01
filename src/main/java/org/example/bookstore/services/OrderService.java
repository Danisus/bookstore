package org.example.bookstore.services;

import lombok.RequiredArgsConstructor;
import org.example.bookstore.dto.OrderItemResponseDto;
import org.example.bookstore.dto.OrderResponseDto;
import org.example.bookstore.entities.*;
import org.example.bookstore.repositories.*;
import org.example.bookstore.security.SecurityUtil;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@RequiredArgsConstructor
@Service
public class OrderService {

    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;
    private final CartItemRepository cartItemRepository;
    private final BookRepository bookRepository;
    private final UserRepository userRepository;
    private final SecurityUtil securityUtil;

    public OrderResponseDto createOrder() {
        String email = securityUtil.getCurrentUserEmail();

        List<CartItem> cartItems = cartItemRepository.findByUserEmail(email);

        if (cartItems.isEmpty()) {
            throw new RuntimeException("Корзина пуста");
        }

        BigDecimal totalPrice = BigDecimal.ZERO;

        for (CartItem cartItem : cartItems) {
            Book book = cartItem.getBook();
            if (cartItem.getQuantity() > book.getStock()) {
                throw new RuntimeException("Недостаточно книг на складе: " + book.getTitle());
            }
            BigDecimal subtotal = book.getPrice().multiply(BigDecimal.valueOf(cartItem.getQuantity()));
            totalPrice = totalPrice.add(subtotal);
        }

        User user = userRepository.findUserByEmail(email)
                .orElseThrow(() -> new RuntimeException("Пользователь не найден"));

        Order order = Order.builder()
                .user(user)
                .orderDate(LocalDateTime.now())
                .status(OrderStatus.PENDING)
                .totalPrice(totalPrice)
                .build();

        order = orderRepository.save(order);

        for (CartItem cartItem : cartItems) {
            Book book = cartItem.getBook();

            OrderItem orderItem = OrderItem.builder()
                    .order(order)
                    .book(book)
                    .quantity(cartItem.getQuantity())
                    .priceAtPurchase(book.getPrice())
                    .build();

            orderItemRepository.save(orderItem);

            book.setStock(book.getStock() - cartItem.getQuantity());
            bookRepository.save(book);
        }

        cartItemRepository.deleteAll(cartItems);

        return toResponse(order);
    }

    public List<OrderResponseDto> getOrders() {
        String email = securityUtil.getCurrentUserEmail();
        return orderRepository.findByUserEmail(email)
                .stream()
                .map(this::toResponse)
                .toList();
    }

    public OrderResponseDto getOrderById(Long orderId) {
        String email = securityUtil.getCurrentUserEmail();
        Order order = orderRepository.findByUserEmailAndId(email, orderId)
                .orElseThrow(() -> new RuntimeException("Заказ не найден"));

        return toResponse(order);
    }

    private OrderResponseDto toResponse(Order order) {
        List<OrderItemResponseDto> itemDtos = orderItemRepository.findByOrder(order)
                .stream()
                .map(this::toItemResponse)
                .toList();

        return OrderResponseDto.builder()
                .id(order.getId())
                .orderDate(order.getOrderDate())
                .status(order.getStatus().name())
                .totalPrice(order.getTotalPrice())
                .items(itemDtos)
                .build();
    }

    private OrderItemResponseDto toItemResponse(OrderItem orderItem) {
        Book book = orderItem.getBook();
        BigDecimal subtotal = orderItem.getPriceAtPurchase()
                .multiply(BigDecimal.valueOf(orderItem.getQuantity()));

        return OrderItemResponseDto.builder()
                .title(book.getTitle())
                .author(book.getAuthor())
                .quantity(orderItem.getQuantity())
                .priceAtPurchase(orderItem.getPriceAtPurchase())
                .subtotal(subtotal)
                .build();
    }
}