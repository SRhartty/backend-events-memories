package com.example.backend_events_memories.repositories;


import com.example.backend_events_memories.domain.Event;
import com.example.backend_events_memories.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface EventRepository  extends JpaRepository<Event, String> {
    Optional<Event> findByName(String name);
    List<Event> findByUserAdmin(User userAdmin);
    Optional<Event> findByNameAndUserAdminId(String name, String userAdminId);
}
