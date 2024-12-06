package com.example.backend_events_memories.services;



import com.example.backend_events_memories.domain.Event;
import com.example.backend_events_memories.domain.Image;
import com.example.backend_events_memories.domain.User;
import com.example.backend_events_memories.infra.security.SecurityFilter;
import com.example.backend_events_memories.infra.security.TokenService;
import com.example.backend_events_memories.repositories.EventRepository;
import com.example.backend_events_memories.repositories.ImageRepository;
import com.example.backend_events_memories.repositories.UserRepository;
import jakarta.servlet.http.HttpServletRequest;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;
import java.util.*;


@Service
@RequiredArgsConstructor
public class ImageService {

    private final ImageRepository imageRepository;
    private final TokenService tokenService;
    private final EventRepository eventRepository;
    private final SecurityFilter securityFilter;
    private final UserRepository userRepository;

    @Transactional
    public void saveImage(MultipartFile file, User user, Event event) {
        try {
            Image image = new Image();
            image.setName(file.getOriginalFilename());
            image.setType(file.getContentType());
            image.setUser(user);
            image.setEvent(event);
            image.setData(file.getBytes());
            imageRepository.save(image);
        }catch (IOException e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Error saving image");
        }
    }

    public void uploadImages(MultipartFile[] files, HttpServletRequest request, String eventId) throws IOException {
        String userId = tokenService.getUserIdByToken(securityFilter.recoverToken(request));
        Optional<User> user = userRepository.findById(userId);
        Optional<Event> event = eventRepository.findById(eventId);

        if (user.isEmpty() || event.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "User or event not found");
        }

        User userFound = user.get();
        Event eventFound = event.get();

        for (MultipartFile file : files) {
            saveImage(file, userFound, eventFound);
        }
    }

    public Image getImage(String id, HttpServletRequest request) {

        String userId = tokenService.getUserIdByToken(securityFilter.recoverToken(request));
        Optional<User> user = userRepository.findById(userId);

        if (user.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "User not found");
        }
        Optional<Image> image = imageRepository.findById(id);
        if (image.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Image not found");
        }
        return image.get();
    }

    public List<Map<String, String>> getImagesByEvent(String eventId , HttpServletRequest request) {
        String userId = tokenService.getUserIdByToken(securityFilter.recoverToken(request));
        Optional<User> user = userRepository.findById(userId);

        if (user.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "User not found");
        }

        Optional<Event> event = eventRepository.findById(eventId);
        if (event.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Event not found");
        }
        Event eventFound = event.get();
        List<Image> images = imageRepository.findByEvent(eventFound);
        List<Map<String, String>> imagesList = new ArrayList<>();
        for (Image image : images) {
            Map<String, String> imageMap = new HashMap<>();
            imageMap.put("id", image.getId());
            imagesList.add(imageMap);
        }
        return imagesList;
    }



}
