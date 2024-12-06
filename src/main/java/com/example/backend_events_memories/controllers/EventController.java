package com.example.backend_events_memories.controllers;


import com.example.backend_events_memories.domain.Event;
import com.example.backend_events_memories.dto.EventRequestDTO;
import com.example.backend_events_memories.dto.EventResponseDTO;
import com.example.backend_events_memories.services.EventService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/event")
@RequiredArgsConstructor
public class EventController {

    private final EventService eventService;

    @PostMapping("/create")
    public ResponseEntity<?> createEvent(@RequestBody EventRequestDTO body, HttpServletRequest request){
        try {
            Event newEvent = eventService.createEvent(body, request);
            return ResponseEntity.ok(new EventResponseDTO(newEvent.getId()));
        }catch (ResponseStatusException e){
            return ResponseEntity.status(e.getStatusCode()).body(e.getReason());
        }
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<?> updateEvent(@PathVariable String id, @RequestBody EventRequestDTO body, HttpServletRequest request){
        try {
            Event newEvent = eventService.updateEvent(id, body, request);
            return ResponseEntity.ok(new EventResponseDTO(newEvent.getId()));
        }catch (ResponseStatusException e){
            return ResponseEntity.status(e.getStatusCode()).body(e.getReason());
        }
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<?> deleteEvent(@PathVariable String id, HttpServletRequest request){
        try {
            eventService.deleteEvent(id, request);
            return ResponseEntity.ok().build();
        }catch (ResponseStatusException e){
            return ResponseEntity.status(e.getStatusCode()).body(e.getReason());
        }
    }

    @GetMapping("/list")
    public ResponseEntity<?> listEvents(HttpServletRequest request){
        try {
            return ResponseEntity.ok(eventService.listEvents(request));
        }catch (ResponseStatusException e){
            return ResponseEntity.status(e.getStatusCode()).body(e.getReason());
        }
    }

    @PostMapping("/invite/{id}")
    public ResponseEntity<?> inviteUser(@PathVariable String id, HttpServletRequest request){
        try {
            return ResponseEntity.ok(eventService.inviteUser(id, request));
        }catch (ResponseStatusException e){
            return ResponseEntity.status(e.getStatusCode()).body(e.getReason());
        }
    }

}
