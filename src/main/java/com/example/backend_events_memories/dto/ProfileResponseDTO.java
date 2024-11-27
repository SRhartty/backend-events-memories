package com.example.backend_events_memories.dto;

public record ProfileResponseDTO(String id, String name, String email, String profilePictureName, byte[] profilePictureData) {
}
