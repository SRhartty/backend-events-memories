package com.example.backend_events_memories.controllers;


import com.example.backend_events_memories.domain.user.Event;
import com.example.backend_events_memories.domain.user.Image;
import com.example.backend_events_memories.domain.user.User;
import com.example.backend_events_memories.infra.security.SecurityFilter;
import com.example.backend_events_memories.infra.security.TokenService;
import com.example.backend_events_memories.repositories.EventRepository;
import com.example.backend_events_memories.repositories.ImageRepository;
import com.example.backend_events_memories.repositories.UserRepository;
import com.example.backend_events_memories.services.ImageService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.*;

@RestController
@RequestMapping("/images")
@RequiredArgsConstructor
public class ImagesController {

    private final ImageService imageService;
    private final TokenService tokenService;
    private final SecurityFilter securityFilter;
    private final UserRepository userRepository;
    private final ImageRepository imageRepository;
    private final EventRepository eventRepository;

    @PostMapping("/upload/eventimages/{eventId}")
    public ResponseEntity<String> uploadEventImages(@RequestParam("files") MultipartFile[] files,  HttpServletRequest request, @PathVariable String eventId) {

        String requestUserId = this.tokenService.getUserIdByToken(securityFilter.recoverToken(request));
        Optional<User> requestUser = this.userRepository.findById(requestUserId);
        Optional<Event> event = this.eventRepository.findById(eventId);

        if (requestUser.isEmpty() || event.isEmpty()) {
            return ResponseEntity.status(403).build();
        }

        Event currentEvent = event.get();
        User currentUser = requestUser.get();


        if(!currentEvent.getUsersGuests().contains(currentUser) && !currentEvent.getUserAdmin().equals(currentUser)) {
            return ResponseEntity.status(403).build();
        }

        StringBuilder message = new StringBuilder();
        for (MultipartFile file : files) {
            try {
                Image image = imageService.saveImage(file, currentUser, currentEvent);
                message.append("Image uploaded successfully: ").append(image.getName()).append("\n");
            } catch (IOException e) {
                return ResponseEntity.status(500).body("Error uploading image: " + file.getOriginalFilename());
            }
        }
        return ResponseEntity.ok(message.toString());
    }

    @GetMapping("/list/eventimages/{eventId}")
    public ResponseEntity<List<Map<String, Object>>> listEventImages(@PathVariable String eventId, HttpServletRequest request) {

        String requestUserId = this.tokenService.getUserIdByToken(securityFilter.recoverToken(request));
        Optional<User> requestUser = this.userRepository.findById(requestUserId);

        if (requestUser.isEmpty()) {
            return ResponseEntity.status(403).build();
        }

        Optional<Event> event = this.eventRepository.findById(eventId);

        if (event.isEmpty()) {
            return ResponseEntity.status(403).body(null);
        }

        List<Image> eventImages = this.imageRepository.findByEvent(event.get());
        List<Map<String, Object>> imagesResponse = new ArrayList<>();

        for (Image image : eventImages) {
            Optional<Image> imageOptional = imageRepository.findById(image.getId());
            if (imageOptional.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
            }
            Image currentImage = imageOptional.get();
            Map<String, Object> imageMap = new HashMap<>();
            imageMap.put("name", currentImage.getName());
            imageMap.put("type", currentImage.getType());
            imageMap.put("data", Base64.getEncoder().encodeToString(currentImage.getData()));
            imagesResponse.add(imageMap);
        }
        return ResponseEntity.ok(imagesResponse);
    }


}
