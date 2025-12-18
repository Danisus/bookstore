package org.example.bookstore.repositories;

import org.example.bookstore.entities.Book;
import org.example.bookstore.entities.CartItem;
import org.example.bookstore.entities.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CartItemRepository extends JpaRepository<CartItem, Long> {

    List<CartItem> findByUserId(long userId);
    Optional<CartItem> findByUserIdAndBookId(long userId, long bookId);
    CartItem findByUserAndBook(User user, Book book);
}
