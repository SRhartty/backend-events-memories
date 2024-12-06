package com.example.backend_events_memories.services;


import com.example.backend_events_memories.domain.User;
import com.example.backend_events_memories.dto.LoginRequestDTO;
import com.example.backend_events_memories.dto.RegisterRequestDTO;
import com.example.backend_events_memories.dto.ResetPasswordRequestDTO;
import com.example.backend_events_memories.infra.security.TokenService;
import com.example.backend_events_memories.repositories.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class LoginService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final TokenService tokenService;
    private final EmailService emailService;


    public String login(LoginRequestDTO body){
        Optional<User> user = userRepository.findByEmail(body.email());
        if(user.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found");
        }
        User userFound = user.get();
        if(passwordEncoder.matches(body.password(), userFound.getPassword())) {
            return tokenService.generateToken(userFound);
        }
        throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid user or password");
    }

    public User register(RegisterRequestDTO body){
        Optional<User> user = userRepository.findByEmail(body.email());

        if(user.isEmpty()) {
            return CreateUser(body.email(), body.password(), body.name());
        }
        throw new ResponseStatusException(HttpStatus.CONFLICT, "User already exists");
    }

    public void forgotPassword(String email){
        Optional<User> user = userRepository.findByEmail(email);
        if(user.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found");
        }
        User userFound = user.get();
        emailService.sendEmail(userFound);
    }

    public void resetUserPassword(ResetPasswordRequestDTO body){
        Optional<User> user = userRepository.findByPasswordResetToken(body.token());
        if(user.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "invalid token");
        }
        User userFound = user.get();
        resetPassword(userFound, body.password());
    }

    @Transactional
    private User CreateUser(String email, String password, String name) {
        User newUser = new User();
        newUser.setPassword(passwordEncoder.encode(password));
        newUser.setEmail(email);
        newUser.setName(name);
        this.userRepository.save(newUser);
        return newUser;
    }

    @Transactional
    private void resetPassword(User user, String password) {
        user.setPassword(passwordEncoder.encode(password));
        user.setPasswordResetToken(null);
        user.setPasswordResetTokenExpirationDate(null);
        userRepository.save(user);
    }
}
