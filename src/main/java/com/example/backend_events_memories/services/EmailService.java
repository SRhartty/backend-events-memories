package com.example.backend_events_memories.services;

import com.example.backend_events_memories.domain.User;
import com.example.backend_events_memories.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class EmailService {


    private final JavaMailSender emailSender;
    private final UserRepository userRepository;

    @Async
    private void sendForgotPasswordLinkEmail(String to, String text) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(to);
        message.setSubject("Password Reset");
        message.setText(text);

        emailSender.send(message);
    }

    @Transactional
    private String createPasswordResetTokenForUser(User user) {
        String token = UUID.randomUUID().toString();
        user.setPasswordResetToken(token);
        user.setPasswordResetTokenExpirationDate(tokenExpirationDate());
        userRepository.save(user);
        return token;
    }

    public void sendEmail(User user) {
        sendForgotPasswordLinkEmail(user.getEmail(),
                "To reset your password," +
                        " click the link: http://localhost:3000/resetPassword/"
                        + createPasswordResetTokenForUser(user));
    }

    private Date tokenExpirationDate() {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime expirationDate = now.plusHours(24);
        return java.sql.Timestamp.valueOf(expirationDate);
    }
}
