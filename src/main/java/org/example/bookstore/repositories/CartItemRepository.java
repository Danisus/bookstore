package org.example.bookstore.repositories;

import org.example.bookstore.entities.Book;
import org.example.bookstore.entities.CartItem;
import org.example.bookstore.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CartItemRepository extends JpaRepository<CartItem, Long> {

    CartItem findByUserAndBook(User user, Book book);
    List<CartItem> findByUserEmail(String userEmail);
    Optional<CartItem> findByUserEmailAndBook(String userEmail, Book book);
}
