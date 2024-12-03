package com.example.backend_events_memories.controllers;

import com.example.backend_events_memories.domain.user.User;
import com.example.backend_events_memories.dto.*;
import com.example.backend_events_memories.infra.security.TokenService;
import com.example.backend_events_memories.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import com.example.backend_events_memories.services.SendPasswordResetTokenForUserService;

import java.util.Objects;
import java.util.Optional;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final TokenService tokenService;
    private final SendPasswordResetTokenForUserService sendPasswordResetTokenForUserService;


    @PostMapping("/login")
    public ResponseEntity<LoginResponseDTO> login(@RequestBody LoginRequestDTO body){
        User user = this.userRepository.findByEmail(body.email()).orElseThrow(() -> new RuntimeException("User not found"));
        if(passwordEncoder.matches(body.password(), user.getPassword())) {
            String token = this.tokenService.generateToken(user);
            return ResponseEntity.ok(new LoginResponseDTO(user.getName(), token));
        }
        return ResponseEntity.status(401).build();
    }


    @PostMapping("/register")
    public ResponseEntity<RegisterResponseDTO> register(@RequestBody RegisterRequestDTO body){
        Optional<User> user = this.userRepository.findByEmail(body.email());

        if(user.isEmpty()) {
            User newUser = new User();
            newUser.setPassword(passwordEncoder.encode(body.password()));
            newUser.setEmail(body.email());
            newUser.setName(body.name());
            this.userRepository.save(newUser);

            return ResponseEntity.ok(new RegisterResponseDTO(newUser.getName(), newUser.getEmail()));
        }
        return ResponseEntity.badRequest().build();
    }

    @PostMapping("/user/forgotPassword/{email}")
    public ResponseEntity<String> forgotPassword(@PathVariable String email){
        Optional<User> user = this.userRepository.findByEmail(email);
        if(user.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        User userFound = user.get();
        this.sendPasswordResetTokenForUserService.sendPasswordResetTokenForUser(userFound);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/user/resetPassword")
    public ResponseEntity<String> resetPassword(@RequestBody ResetPasswordRequestDTO body){
        Optional<User> user = this.userRepository.findByPasswordResetToken(body.token());
        if(user.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        if (Objects.requireNonNull(user.get().getPasswordResetTokenExpirationDate()).before(new java.util.Date())) {
            return ResponseEntity.status(401).build();
        }
        User userFound = user.get();
        userFound.setPassword(passwordEncoder.encode(body.password()));
        userFound.setPasswordResetToken(null);
        userFound.setPasswordResetTokenExpirationDate(null);
        this.userRepository.save(userFound);
        return ResponseEntity.ok().build();
    }
}
