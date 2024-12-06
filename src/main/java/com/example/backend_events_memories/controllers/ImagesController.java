package com.example.backend_events_memories.controllers;


import com.example.backend_events_memories.domain.Event;
import com.example.backend_events_memories.domain.Image;
import com.example.backend_events_memories.domain.User;
import com.example.backend_events_memories.infra.security.SecurityFilter;
import com.example.backend_events_memories.infra.security.TokenService;
import com.example.backend_events_memories.repositories.EventRepository;
import com.example.backend_events_memories.repositories.ImageRepository;
import com.example.backend_events_memories.repositories.UserRepository;
import com.example.backend_events_memories.services.ImageService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;
import java.util.*;

@RestController
@RequestMapping("/images")
@RequiredArgsConstructor
public class ImagesController {

    private final ImageService imageService;


    @PostMapping("/upload/eventimages/{eventId}")
    public ResponseEntity<String> uploadEventImages(@RequestParam("files") MultipartFile[] files,  HttpServletRequest request, @PathVariable String eventId) {
        try {
            imageService.uploadImages(files, request, eventId);
            return ResponseEntity.ok().build();
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error saving image");
        }
    }

    @GetMapping("/upload/eventimage/{imageId}")
    public ResponseEntity<?> getImage(@PathVariable String imageId, HttpServletRequest request) {
        try {
            return ResponseEntity.ok(imageService.getImage(imageId, request));
        } catch (ResponseStatusException e) {
            return ResponseEntity.status(e.getStatusCode()).body(e.getReason());
        }
    }

    @GetMapping("/list/eventimages/{eventId}")
    public ResponseEntity<?> listEventImages(@PathVariable String eventId, HttpServletRequest request) {
        try {
            return ResponseEntity.ok(imageService.getImagesByEvent(eventId, request));
        } catch (ResponseStatusException e) {
            return ResponseEntity.status(e.getStatusCode()).body(e.getReason());
        }
    }
}
