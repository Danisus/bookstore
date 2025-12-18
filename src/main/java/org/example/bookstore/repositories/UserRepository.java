package org.example.bookstore.repositories;

import org.example.bookstore.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

    List<User> findUserByEmail(String email);

    @Override
    Optional<User> findById(Long aLong);
}
