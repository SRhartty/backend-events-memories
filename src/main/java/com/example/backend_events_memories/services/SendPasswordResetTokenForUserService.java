package com.example.backend_events_memories.services;

import com.example.backend_events_memories.domain.user.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class SendPasswordResetTokenForUserService {

    private final EmailService emailService;

    @Transactional
    public void sendPasswordResetTokenForUser(User user) {
        String token = UUID.randomUUID().toString();
        user.setPasswordResetToken(token);
        user.setPasswordResetTokenExpirationDate(tokenExpirationDate());
        sendEmail(user, token);
    }

    private void sendEmail(User user, String token) {
        emailService.sendForgotPasswordLinkEmail(user.getEmail(),
                "Password Reset", "To reset your password, click the link: http://localhost:3000/resetPassword/" + token);
    }

    private Date tokenExpirationDate() {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime expirationDate = now.plusHours(24);
        return java.sql.Timestamp.valueOf(expirationDate);
    }
}
