package org.example.bookstore.dto;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CartItemResponseDto {

    private Long bookId;
    private String title;
    private String author;
    private BigDecimal price;
    private Integer quantity;
    private BigDecimal subtotal;

}
