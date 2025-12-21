package org.example.bookstore.repositories;

import org.example.bookstore.entities.Book;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface BookRepository extends JpaRepository<Book, Long> {
    List<Book> findBookByTitleContainingIgnoreCaseOrAuthorContainingIgnoreCase(String title, String author);
}
