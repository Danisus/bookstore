package org.example.bookstore.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.*;

import java.math.BigDecimal;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class BookRequestDto {
    @NotBlank
    private String title;
    @NotBlank
    private String author;
    private String description;
    @NotNull
    @Positive
    private BigDecimal price;
    private String genre;
    @NotNull
    @Min(0)
    private Integer stock;


}
