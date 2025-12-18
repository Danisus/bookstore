package org.example.bookstore.entities;

public enum OrderStatus {
    //в корзине, заказ подтвержден(оплачен), заказ отправлен, заказ отменен
    PENDING, CONFIRMED, SHIPPED, CANCELLED
}
