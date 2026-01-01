package org.example.bookstore.repositories;

import org.aspectj.weaver.ast.Or;
import org.example.bookstore.entities.Order;
import org.example.bookstore.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {

    Optional<Order> findByUserAndId(User user, long id);
    List<Order> findByUser(User user);

    List<Order> findByUserEmail(String testEmail);

    Optional<Order> findByUserEmailAndId(String testEmail, long l);
}
