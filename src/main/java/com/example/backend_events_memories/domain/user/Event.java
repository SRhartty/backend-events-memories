package com.example.backend_events_memories.domain.user;

import jakarta.persistence.*;
import lombok.*;


import java.util.List;

@Entity
@Table(name = "events")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Event {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    private String name;

    private String description;

    private String date;

    private String location;

    @ManyToOne
    private User userAdmin;

    @ManyToMany
    private List<User> usersGuests;
}
