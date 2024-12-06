package com.example.backend_events_memories.services;

import com.example.backend_events_memories.domain.User;
import com.example.backend_events_memories.dto.ProfileResponseDTO;
import com.example.backend_events_memories.infra.security.SecurityFilter;
import com.example.backend_events_memories.infra.security.TokenService;
import com.example.backend_events_memories.repositories.UserRepository;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ProfileService {

    private final TokenService tokenService;
    private final UserRepository userRepository;
    private final SecurityFilter securityFilter;

    public User getProfile(HttpServletRequest request) {
        String userId = tokenService.getUserIdByToken(securityFilter.recoverToken(request));
        Optional<User> user = userRepository.findById(userId);

        if (user.isEmpty()) {
            throw new  ResponseStatusException(HttpStatus.FORBIDDEN, "User not found");
        }
        return user.get();

    }
}
