package com.example.backend_events_memories.repositories;

import com.example.backend_events_memories.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, String> {
    Optional<User> findByEmail(String email);
    Optional<User> findByPasswordResetToken(String token);
}
