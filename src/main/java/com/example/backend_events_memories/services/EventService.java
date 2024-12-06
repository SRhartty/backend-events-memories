package com.example.backend_events_memories.services;

import com.example.backend_events_memories.domain.Event;
import com.example.backend_events_memories.domain.User;
import com.example.backend_events_memories.dto.EventRequestDTO;
import com.example.backend_events_memories.dto.EventResponseDTO;
import com.example.backend_events_memories.infra.security.SecurityFilter;
import com.example.backend_events_memories.infra.security.TokenService;
import com.example.backend_events_memories.repositories.EventRepository;
import com.example.backend_events_memories.repositories.UserRepository;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class EventService {

    private final EventRepository eventRepository;
    private final TokenService tokenService;
    private final SecurityFilter securityFilter;
    private final UserRepository userRepository;

    public Event createEvent(EventRequestDTO body, HttpServletRequest request) {
        String userId = tokenService.getUserIdByToken(securityFilter.recoverToken(request));
        Optional<User> user = userRepository.findById(userId);

        if (user.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "User not found");
        }

        User userAdmin = user.get();
        Optional<Event> Event = eventRepository.findByNameAndUserAdminId(body.name(), userAdmin.getId());

        if(Event.isEmpty()) {
            Event newEvent = new Event();
            newEvent.setName(body.name());
            newEvent.setDescription(body.description());
            newEvent.setDate(body.date());
            newEvent.setLocation(body.location());
            newEvent.setUserAdmin(userAdmin);
            eventRepository.save(newEvent);
            return newEvent;
        }
        throw new ResponseStatusException(HttpStatus.CONFLICT, "Event already exists");
    }

    public Event updateEvent(String id, EventRequestDTO body, HttpServletRequest request) {
        String userId = tokenService.getUserIdByToken(securityFilter.recoverToken(request));
        Optional<User> user = userRepository.findById(userId);
        Optional<Event> saveEvent = eventRepository.findById(id);

        if (user.isEmpty() || saveEvent.isEmpty()) {
           throw new ResponseStatusException(HttpStatus.FORBIDDEN, "User not found");
        }

        User userAdmin = user.get();
        Event currentEvent = saveEvent.get();

        if(currentEvent.getUserAdmin().getId().equals(userAdmin.getId())) {
            currentEvent.setName(body.name());
            currentEvent.setDescription(body.description());
            currentEvent.setDate(body.date());
            currentEvent.setLocation(body.location());
            eventRepository.save(currentEvent);
            return currentEvent;
        }
        throw new ResponseStatusException(HttpStatus.FORBIDDEN, "User not allowed to update this event");
    }

    public void deleteEvent(String id, HttpServletRequest request) {
        String userId = tokenService.getUserIdByToken(securityFilter.recoverToken(request));
        Optional<User> user = userRepository.findById(userId);
        Optional<Event> event = eventRepository.findById(id);

        if (user.isEmpty() || event.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "User not found");
        }

        User userAdmin = user.get();
        Event currentEvent = event.get();

        if(currentEvent.getUserAdmin().getId().equals(userAdmin.getId())) {
            eventRepository.delete(currentEvent);
        }
        throw new ResponseStatusException(HttpStatus.FORBIDDEN, "User not allowed to delete this event");
    }

    public List<EventResponseDTO> listEvents(HttpServletRequest request) {
        List<EventResponseDTO> arrayListDTO = new ArrayList<>();
        String filterToken = securityFilter.recoverToken(request);
        String userId = tokenService.getUserIdByToken(filterToken);
        Optional<User> userOptional = userRepository.findById(userId);

        if (userOptional.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "User not found");
        }

        User user = userOptional.get();
        List<Event> events = eventRepository.findByUserAdmin(user);

        if (events.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "No events found");
        }
        for (Event event : events) {
            arrayListDTO.add(new EventResponseDTO(event.getId()));
        }
        return arrayListDTO;
    }

    public EventResponseDTO inviteUser(String id, HttpServletRequest request) {
        String userId = tokenService.getUserIdByToken(securityFilter.recoverToken(request));
        Optional<User> user = userRepository.findById(userId);

        if (user.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "User not found");
        }

        User currentUser = user.get();
        Optional<Event> saveEvent = eventRepository.findById(id);

        if(saveEvent.isPresent() && !saveEvent.get().getUsersGuests().contains(currentUser)) {
            Event newEvent = saveEvent.get();
            newEvent.getUsersGuests().add(currentUser);
            eventRepository.save(newEvent);
            return new EventResponseDTO(newEvent.getId());
        }
        throw new ResponseStatusException(HttpStatus.FORBIDDEN, "User not allowed to invite this user");
    }
}
