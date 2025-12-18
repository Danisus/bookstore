package org.example.bookstore.services;

import lombok.RequiredArgsConstructor;
import org.example.bookstore.dto.OrderItemResponseDto;
import org.example.bookstore.dto.OrderResponseDto;
import org.example.bookstore.entities.*;
import org.example.bookstore.repositories.*;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@RequiredArgsConstructor
@Service
public class OrderService {

    private final OrderRepository orderRepository;
    private final BookRepository bookRepository;
    private final OrderItemRepository orderItemRepository;
    private final CartItemRepository cartItemRepository;
    private final UserRepository userRepository;

    public OrderResponseDto createOrder(Long userId){
        List<CartItem> cartItems = cartItemRepository.findByUserId(userId);
        if (cartItems.isEmpty())
            throw new RuntimeException("корзина пустая");
        BigDecimal totalPrice = BigDecimal.ZERO;
        for (CartItem item: cartItems){
            if (item.getQuantity() > item.getBook().getStock())
                throw new RuntimeException("кол-во книг в заказе превышает кол-во книг на складе");
            BigDecimal price = item.getBook().getPrice();
            Integer quantity = item.getQuantity();
            BigDecimal subtotal = price.multiply(new BigDecimal(quantity));

            totalPrice = totalPrice.add(subtotal);
        }
        User user = userRepository.findById(userId).orElseThrow(() -> new RuntimeException("пользователь с id " + userId + " не найден"));

        Order order = Order.builder()
                .user(user)
                .orderDate(LocalDateTime.now())
                .status(OrderStatus.PENDING)
                .totalPrice(totalPrice)
                .build();
        orderRepository.save(order);

        for (CartItem item: cartItems){
            OrderItem orderItem = OrderItem.builder()
                    .order(order)
                    .book(item.getBook())
                    .quantity(item.getQuantity())
                    .priceAtPurchase(item.getBook().getPrice())
                    .build();
            Book book = item.getBook();
            book.setStock(item.getBook().getStock() - item.getQuantity());
            bookRepository.save(book);
            orderItemRepository.save(orderItem);
        }

        for (CartItem item: cartItems)
            cartItemRepository.delete(item);

        return toResponse(order);
    }

    public List<OrderResponseDto> getOrders(Long userId){
        User user = userRepository.findById(userId).orElseThrow(() -> new RuntimeException("пользователь с id " + userId + " не найден"));
        return orderRepository.findByUser(user).stream().map(this::toResponse).toList();
    }

    public OrderResponseDto getOrderById(Long userId, Long orderId){
        User user = userRepository.findById(userId).orElseThrow(() -> new RuntimeException("пользователь с id " + userId + " не найден"));
        Order order = orderRepository.findByUserAndId(user, orderId).orElseThrow(() -> new RuntimeException("заказ с id " + orderId + " не найден"));
        return toResponse(order);
    }



    private OrderResponseDto toResponse(Order order){
        return OrderResponseDto.builder()
                .id(order.getId())
                .orderDate(order.getOrderDate())
                .status(order.getStatus().toString())
                .totalPrice(order.getTotalPrice())
                .items(orderItemRepository.findByOrder(order).stream().map(this::toItemResponse).toList())
                .build();
    }

    private OrderItemResponseDto toItemResponse(OrderItem item){
        BigDecimal subtotal = item.getPriceAtPurchase().multiply(new BigDecimal(item.getQuantity()));
        return OrderItemResponseDto.builder()
                .title(item.getBook().getTitle())
                .author(item.getBook().getAuthor())
                .quantity(item.getQuantity())
                .priceAtPurchase(item.getPriceAtPurchase())
                .subtotal(subtotal)
                .build();
    }

}
