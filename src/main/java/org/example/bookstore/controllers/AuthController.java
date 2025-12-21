package org.example.bookstore.controllers;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.bookstore.dto.AuthResponseDto;
import org.example.bookstore.dto.LoginRequestDto;
import org.example.bookstore.dto.RegisterRequestDto;
import org.example.bookstore.entities.Role;
import org.example.bookstore.entities.User;
import org.example.bookstore.repositories.UserRepository;
import org.example.bookstore.security.JwtUtil;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    @PostMapping("/register")
    @ResponseStatus(HttpStatus.CREATED)
    public AuthResponseDto register(@Valid @RequestBody RegisterRequestDto requestDto) {
        if (userRepository.findUserByEmail(requestDto.getEmail()).isPresent()) {
            throw new RuntimeException("Email уже зарегистрирован");
        }

        User user = User.builder()
                .name(requestDto.getName())
                .email(requestDto.getEmail())
                .password(passwordEncoder.encode(requestDto.getPassword()))
                .role(Role.USER)
                .createdAt(LocalDateTime.now())
                .build();

        userRepository.save(user);

        String token = jwtUtil.generateToken(user.getEmail(), user.getRole().name());

        return AuthResponseDto.builder().token(token).build();
    }

    @PostMapping("/login")
    public AuthResponseDto login(@Valid @RequestBody LoginRequestDto requestDto) {
        User user = userRepository.findUserByEmail(requestDto.getEmail()).orElseThrow(() -> new RuntimeException("Неверный email или пароль"));

        if (!passwordEncoder.matches(requestDto.getPassword(), user.getPassword())) {
            throw new RuntimeException("Неверный email или пароль");
        }

        String token = jwtUtil.generateToken(user.getEmail(), user.getRole().name());

        return AuthResponseDto.builder().token(token).build();
    }
}