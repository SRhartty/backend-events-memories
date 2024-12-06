package com.example.backend_events_memories.repositories;

import com.example.backend_events_memories.domain.Event;
import com.example.backend_events_memories.domain.Image;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ImageRepository extends JpaRepository<Image, String> {
    List<Image> findByEvent(Event event);
}
