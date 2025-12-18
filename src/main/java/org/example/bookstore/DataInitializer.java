package org.example.bookstore;

import lombok.RequiredArgsConstructor;
import org.example.bookstore.entities.Book;
import org.example.bookstore.entities.Role;
import org.example.bookstore.entities.User;
import org.example.bookstore.repositories.BookRepository;
import org.example.bookstore.repositories.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private final BookRepository bookRepository;
    private final UserRepository userRepository;
    //private final PasswordEncoder passwordEncoder; // добавим позже, когда будет Security

    @Override
    public void run(String... args) {
        // Добавляем тестовые книги, если их нет
        if (bookRepository.count() == 2) {
            bookRepository.save(Book.builder()
                    .title("Effective Java")
                    .author("Joshua Bloch")
                    .description("Best practices for Java")
                    .price(new BigDecimal("2500.00"))
                    .genre("PROGRAMMING")
                    .stock(10)
                    .build());

            bookRepository.save(Book.builder()
                    .title("Clean Code")
                    .author("Robert C. Martin")
                    .description("A Handbook of Agile Software Craftsmanship")
                    .price(new BigDecimal("2000.00"))
                    .genre("PROGRAMMING")
                    .stock(15)
                    .build());

            bookRepository.save(Book.builder()
                    .title("Head First Design Patterns")
                    .author("Eric Freeman")
                    .description("Learning design patterns")
                    .price(new BigDecimal("3000.00"))
                    .genre("PROGRAMMING")
                    .stock(5)
                    .build());

            // Добавь ещё 2–3 книги, если хочешь
        }

        // Добавляем пользователей (временно без пароля, потом зашифруем)
        if (userRepository.findUserByEmail("user@example.com").isEmpty()) {
            userRepository.save(User.builder()
                    .name("Test User")
                    .email("user@example.com")
                    .password("temp") // потом зашифруем
                    .role(Role.USER)
                    .build());
        }

        if (userRepository.findUserByEmail("admin@example.com").isEmpty()) {
            userRepository.save(User.builder()
                    .name("Admin")
                    .email("admin@example.com")
                    .password("temp")
                    .role(Role.ADMIN)
                    .build());
        }
    }
}