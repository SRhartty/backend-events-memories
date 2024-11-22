package com.example.backend_events_memories.controllers;


import com.example.backend_events_memories.domain.user.Event;
import com.example.backend_events_memories.domain.user.User;
import com.example.backend_events_memories.dto.EventRequestDTO;
import com.example.backend_events_memories.dto.EventResponseDTO;
import com.example.backend_events_memories.infra.security.SecurityFilter;
import com.example.backend_events_memories.infra.security.TokenService;
import com.example.backend_events_memories.repositories.EventRepository;
import com.example.backend_events_memories.repositories.UserRepository;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/event")
@RequiredArgsConstructor
public class EventController {

    private final EventRepository eventRepository;
    private final TokenService tokenService;
    private final SecurityFilter securityFilter;
    private final UserRepository userRepository;

    @PostMapping("/create")
    public ResponseEntity<EventResponseDTO> createEvent(@RequestBody EventRequestDTO body, HttpServletRequest request){

        String userId = this.tokenService.getUserIdByToken(securityFilter.recoverToken(request));
        Optional<User> user = this.userRepository.findById(userId);

        if (user.isEmpty()) {
            return ResponseEntity.status(403).body(null);
        }

        User userAdmin = user.get();
        Optional<Event> saveEvent = this.eventRepository.findByName(body.name());

        if(saveEvent.isEmpty()) {
            Event newEvent = new Event();
            newEvent.setName(body.name());
            newEvent.setDescription(body.description());
            newEvent.setDate(body.date());
            newEvent.setLocation(body.location());
            newEvent.setUserAdmin(userAdmin);
            this.eventRepository.save(newEvent);
            return ResponseEntity.ok(new EventResponseDTO(newEvent.getName(),
                    newEvent.getDescription(), newEvent.getDate(),
                    newEvent.getLocation(), newEvent.getId()));
        }
        return ResponseEntity.status(403).body(null);
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<EventResponseDTO> updateEvent(@PathVariable String id, @RequestBody EventRequestDTO body, HttpServletRequest request){

        String userId = this.tokenService.getUserIdByToken(securityFilter.recoverToken(request));
        Optional<User> user = this.userRepository.findById(userId);

        if (user.isEmpty()) {
            return ResponseEntity.status(403).body(null);
        }

        User userAdmin = user.get();
        Optional<Event> saveEvent = this.eventRepository.findById(id);

        if(saveEvent.isPresent()) {
            Event newEvent = saveEvent.get();
            newEvent.setName(body.name());
            newEvent.setDescription(body.description());
            newEvent.setDate(body.date());
            newEvent.setLocation(body.location());
            newEvent.setUserAdmin(userAdmin);
            this.eventRepository.save(newEvent);
            return ResponseEntity.ok(new EventResponseDTO(newEvent.getName(),
                    newEvent.getDescription(), newEvent.getDate(),
                    newEvent.getLocation(), newEvent.getId()));
        }
        return ResponseEntity.status(403).body(null);

    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<Event> deleteEvent(@PathVariable String id, HttpServletRequest request){

        String filterToken = securityFilter.recoverToken(request);
        String userId = this.tokenService.getUserIdByToken(filterToken);
        Optional<User> userOptional = this.userRepository.findById(userId);

        if (userOptional.isEmpty()) {
            return ResponseEntity.status(403).body(null);
        }

        Optional<Event> event = this.eventRepository.findById(id);
        User user = userOptional.get();

        if(event.isPresent() && event.get().getUserAdmin().getId().equals(user.getId())) {
            this.eventRepository.delete(event.get());
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.status(403).body(null);
    }

    @GetMapping("/list")
    public ResponseEntity<List<EventResponseDTO>> listEvents(HttpServletRequest request){

        List<EventResponseDTO> arrayListDTO = new ArrayList<>();
        String filterToken = securityFilter.recoverToken(request);
        String userId = this.tokenService.getUserIdByToken(filterToken);
        Optional<User> userOptional = this.userRepository.findById(userId);

        if (userOptional.isEmpty()) {
            return ResponseEntity.status(403).body(null);
        }

        User user = userOptional.get();
        List<Event> events = this.eventRepository.findByUserAdmin(user);

        if (events.isEmpty()) {
            return ResponseEntity.status(403).body(null);
        }
        for (Event event : events) {
            arrayListDTO.add(new EventResponseDTO(event.getName(), event.getDescription(), event.getDate(), event.getLocation(), event.getId()));
        }
        return ResponseEntity.ok(arrayListDTO);
    }

    @PostMapping("/invite/{id}")
    public ResponseEntity<EventResponseDTO> inviteUser(@PathVariable String id, HttpServletRequest request){

        String userId = this.tokenService.getUserIdByToken(securityFilter.recoverToken(request));
        Optional<User> user = this.userRepository.findById(userId);

        if (user.isEmpty()) {
            return ResponseEntity.status(403).body(null);
        }

        User currentUser = user.get();

        Optional<Event> saveEvent = this.eventRepository.findById(id);

        if(saveEvent.isPresent() && !saveEvent.get().getUsersGuests().contains(currentUser)) {
            Event newEvent = saveEvent.get();
            newEvent.getUsersGuests().add(currentUser);
            this.eventRepository.save(newEvent);
            return ResponseEntity.ok(new EventResponseDTO(newEvent.getName(),
                    newEvent.getDescription(), newEvent.getDate(),
                    newEvent.getLocation(), newEvent.getId()));
        }
        return ResponseEntity.status(403).body(null);
    }

}
