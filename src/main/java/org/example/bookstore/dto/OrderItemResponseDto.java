package org.example.bookstore.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderItemResponseDto {

    private String title;
    private String author;
    private int quantity;
    private BigDecimal priceAtPurchase;
    private BigDecimal subtotal;

}
