package org.example.bookstore.controllers;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.bookstore.dto.BookRequestDto;
import org.example.bookstore.dto.BookResponseDto;
import org.example.bookstore.services.BookService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/books")
public class BookController {

    private final BookService service;

    @GetMapping
    public Page<BookResponseDto> getAllBooks(Pageable pageable){
        return service.getAllBooks(pageable);
    }

    @GetMapping("/{id}")
    public BookResponseDto getBookById(@PathVariable Long id){
        return service.getBookById(id);
    }

    @GetMapping("/search")
    public List<BookResponseDto> searchBooks(@RequestParam String query){
        return service.searchBooks(query);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping
    public BookResponseDto createBook(@RequestBody @Valid BookRequestDto requestDto){
        return service.createBook(requestDto);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/{id}")
    public BookResponseDto updateBook(@PathVariable Long id, @RequestBody @Valid BookRequestDto requestDto){
        return service.updateBook(id, requestDto);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @DeleteMapping("/{id}")
    public void deleteBook(@PathVariable Long id){
        service.deleteBook(id);
    }
}
