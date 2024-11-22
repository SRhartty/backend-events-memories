package com.example.backend_events_memories.repositories;

import com.example.backend_events_memories.domain.user.Event;
import com.example.backend_events_memories.domain.user.Image;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ImageRepository extends JpaRepository<Image, String> {
    List<Image> findByEvent(Event event);
}
