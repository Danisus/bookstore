package org.example.bookstore.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "books")
public class Book {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    @Column(nullable = false)
    private String title;
    @Column(nullable = false)
    private String author;
    @Column(length = 2000)
    private String description;
    @Column(nullable = false)
    private BigDecimal price;
    //жанр
    private String genre;
    //кол-во книг на складе
    @Column(nullable = false)
    private int stock;
    @CreationTimestamp
    private LocalDateTime createdAt;

}
