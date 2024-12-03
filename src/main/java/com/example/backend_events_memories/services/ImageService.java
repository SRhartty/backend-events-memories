package com.example.backend_events_memories.services;



import com.example.backend_events_memories.domain.user.Event;
import com.example.backend_events_memories.domain.user.Image;
import com.example.backend_events_memories.domain.user.User;
import com.example.backend_events_memories.repositories.ImageRepository;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;


@Service
@RequiredArgsConstructor
public class ImageService {

    private final ImageRepository imageRepository;

    @Transactional
    public Image saveImage(MultipartFile file, User user, Event event) throws IOException {
        try {
            Image image = new Image();
            image.setName(file.getOriginalFilename());
            image.setType(file.getContentType());
            image.setUser(user);
            image.setEvent(event);
            image.setData(file.getBytes());
            return imageRepository.save(image);
        }catch (IOException e){
            throw new IOException("Error saving image");
        }
    }

}
