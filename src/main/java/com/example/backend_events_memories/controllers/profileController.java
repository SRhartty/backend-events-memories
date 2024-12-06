package com.example.backend_events_memories.controllers;


import com.example.backend_events_memories.domain.User;
import com.example.backend_events_memories.dto.ProfileResponseDTO;
import com.example.backend_events_memories.infra.security.SecurityFilter;
import com.example.backend_events_memories.infra.security.TokenService;
import com.example.backend_events_memories.repositories.UserRepository;
import com.example.backend_events_memories.services.ProfileService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;
import java.util.Optional;

@RestController
@RequestMapping("/profile")
@RequiredArgsConstructor
public class profileController {

    private final TokenService tokenService;
    private final SecurityFilter securityFilter;
    private final UserRepository userRepository;
    private final ProfileService profileService;

    @GetMapping
    public ResponseEntity<?> getProfile(HttpServletRequest request) {
        try {
            User user = this.profileService.getProfile(request);
            return ResponseEntity.ok(new ProfileResponseDTO(user.getId(), user.getName(), user.getEmail(),
                    user.getProfilePictureName(), user.getProfilePictureData()));
        }catch (ResponseStatusException e){
            return ResponseEntity.status(e.getStatusCode()).body(e.getReason());
        }
    }

    @PutMapping
    public ResponseEntity<ProfileResponseDTO> updateProfile(@RequestBody ProfileResponseDTO body, HttpServletRequest request) {
        String userId = this.tokenService.getUserIdByToken(securityFilter.recoverToken(request));
        Optional<User> user = this.userRepository.findById(userId);

        if (user.isEmpty()) {
            System.out.println("User not found");
            return ResponseEntity.status(403).body(null);
        }

        try {
            User updatedUser = user.get();
            updatedUser.setName(body.name());
            updatedUser.setEmail(body.email());

            this.userRepository.save(updatedUser);
            return ResponseEntity.ok(new ProfileResponseDTO(updatedUser.getId(),
                    updatedUser.getName(), updatedUser.getEmail(), updatedUser.getProfilePictureName(),
                    updatedUser.getProfilePictureData()));
        }catch (Exception e){
            return ResponseEntity.status(500).body(null);
        }
    }

    @PostMapping("/upload/profileimage")
    public ResponseEntity<String> uploadProfileImage(@RequestParam("file") MultipartFile file, HttpServletRequest request) {
        String userId = this.tokenService.getUserIdByToken(securityFilter.recoverToken(request));
        Optional<User> user = this.userRepository.findById(userId);

        if (user.isEmpty()) {
            System.out.println("User not found");
            return ResponseEntity.status(403).build();
        }

        User currentUser = user.get();
        System.out.println(currentUser.getName());

        try {
            currentUser.setProfilePictureName(file.getOriginalFilename());
            currentUser.setProfilePictureData(file.getBytes());
            this.userRepository.save(currentUser);
            return ResponseEntity.ok("Image uploaded successfully");
        } catch (IOException e) {
            return ResponseEntity.status(500).body("Error saving image");
        }
    }
}
