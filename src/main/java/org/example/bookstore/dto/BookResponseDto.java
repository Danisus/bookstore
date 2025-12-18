package org.example.bookstore.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class BookResponseDto {

    private long id;
    private String title;
    private String author;
    private String description;
    private BigDecimal price;
    private String genre;
    private int stock;
}
