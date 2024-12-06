package com.example.backend_events_memories.domain;

import jakarta.annotation.Nullable;
import jakarta.persistence.*;
import lombok.*;
import java.util.Date;

@Entity
@Table(name = "users")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    private String name;

    private String email;

    private String password;

    @Nullable
    private String profilePictureName;

    @Nullable
    @Lob
    private byte[] profilePictureData;

    @Nullable
    private String passwordResetToken;

    @Nullable
    private Date passwordResetTokenExpirationDate;

}
