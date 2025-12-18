package org.example.bookstore.repositories;

import org.example.bookstore.entities.Book;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.awt.print.Pageable;
import java.util.List;
import java.util.Optional;

@Repository
public interface BookRepository extends JpaRepository<Book, Long> {
    List<Book> findBookByTitleContainingIgnoreCaseOrAuthorContainingIgnoreCase(String title, String author);
}
