package org.example.bookstore.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.example.bookstore.entities.Book;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AddToCartRequestDto {

    @Positive
    @NotNull
    private Long bookId;
    @NotNull
    @Min(1)
    private Integer quantity;

}
