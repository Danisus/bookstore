package org.example.bookstore.services;

import org.example.bookstore.dto.BookRequestDto;
import org.example.bookstore.dto.BookResponseDto;
import org.example.bookstore.entities.Book;
import org.example.bookstore.repositories.BookRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BookServiceTest {

    @Mock
    private BookRepository bookRepository;

    @InjectMocks
    private BookService bookService;

    @Test
    void getAllBooks_returnsPageOfDto() {
        Book book = Book.builder()
                .id(1L)
                .title("Test Book")
                .author("Author")
                .price(new BigDecimal("1000"))
                .stock(10)
                .build();

        Page<Book> page = new PageImpl<>(List.of(book));
        Pageable pageable = PageRequest.of(0, 10);

        when(bookRepository.findAll(pageable)).thenReturn(page);

        Page<BookResponseDto> result = bookService.getAllBooks(pageable);

        assertEquals(1, result.getTotalElements());
        assertEquals("Test Book", result.getContent().get(0).getTitle());
    }

    @Test
    void getBookById_existingId_returnsDto() {
        // given
        Book book = Book.builder()
                .id(1L)
                .title("Test Book")
                .price(new BigDecimal("1000"))
                .build();

        when(bookRepository.findById(1L)).thenReturn(Optional.of(book));

        BookResponseDto result = bookService.getBookById(1L);

        assertEquals("Test Book", result.getTitle());
    }

    @Test
    void getBookById_notExistingId_throwsException() {
        when(bookRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> bookService.getBookById(999L));
    }

    @Test
    void createBook_savesAndReturnsDto() {
        // given
        Book savedBook = Book.builder()
                .id(1L)
                .title("New Book")
                .price(new BigDecimal("2000"))
                .build();

        when(bookRepository.save(any(Book.class))).thenReturn(savedBook);

        // when
        BookResponseDto result = bookService.createBook(TestData.bookRequestDto());

        // then
        assertEquals("New Book", result.getTitle());
        verify(bookRepository).save(any(Book.class));
    }
}

// Вспомогательный класс для тестовых данных
class TestData {
    static BookRequestDto bookRequestDto() {
        BookRequestDto dto = new BookRequestDto();
        dto.setTitle("New Book");
        dto.setAuthor("Author");
        dto.setPrice(new BigDecimal("2000"));
        dto.setStock(10);
        return dto;
    }
}