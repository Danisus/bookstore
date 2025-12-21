package org.example.bookstore.services;

import lombok.RequiredArgsConstructor;
import org.example.bookstore.dto.BookRequestDto;
import org.example.bookstore.dto.BookResponseDto;
import org.example.bookstore.entities.Book;
import org.example.bookstore.repositories.BookRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@RequiredArgsConstructor
@Service
public class BookService {

    private final BookRepository bookRepository;

    public Page<BookResponseDto> getAllBooks(Pageable pageable){
        Page<Book> bookPage = bookRepository.findAll(pageable);
        return bookPage.map(this::toResponseDto);
    }

    public BookResponseDto getBookById(Long id){
        Book book = bookRepository.findById(id).orElseThrow(() -> new RuntimeException("книга с не найдена"));
        return toResponseDto(book);
    }

    public List<BookResponseDto> searchBooks(String query){
        return bookRepository.findBookByTitleContainingIgnoreCaseOrAuthorContainingIgnoreCase(query, query).stream().map(this::toResponseDto).toList();
    }
    public BookResponseDto createBook(BookRequestDto requestDto){
        Book book = Book.builder()
                .title(requestDto.getTitle())
                .author(requestDto.getAuthor())
                .description(requestDto.getDescription())
                .price(requestDto.getPrice())
                .genre(requestDto.getGenre())
                .stock(requestDto.getStock())
                .build();

        bookRepository.save(book);
        return toResponseDto(book);
    }

    public BookResponseDto updateBook(Long id, BookRequestDto requestDto){
        Book book = bookRepository.findById(id).orElseThrow(() -> new RuntimeException("книга с не найдена"));
        book.setAuthor(requestDto.getAuthor());
        book.setTitle(requestDto.getTitle());
        book.setDescription(requestDto.getDescription());
        book.setPrice(requestDto.getPrice());
        book.setGenre(requestDto.getGenre());
        book.setStock(requestDto.getStock());
        bookRepository.save(book);
        return toResponseDto(book);
    }

    public void deleteBook(Long id){
        Book book = bookRepository.findById(id).orElseThrow(() -> new RuntimeException("книга с не найдена"));
        bookRepository.delete(book);
    }


    private BookResponseDto toResponseDto(Book book){
        return new BookResponseDto(
                book.getId(),
                book.getTitle(),
                book.getAuthor(),
                book.getDescription(),
                book.getPrice(),
                book.getGenre(),
                book.getStock()
        );
    }


}
