package com.example.backend_events_memories.controllers;

import com.example.backend_events_memories.domain.User;
import com.example.backend_events_memories.dto.*;
import com.example.backend_events_memories.services.LoginService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final LoginService loginService;


    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequestDTO body){
        try {
            String token = loginService.login(body);
            return ResponseEntity.ok(new LoginResponseDTO(token));
        } catch (ResponseStatusException e) {
            return ResponseEntity.status(e.getStatusCode()).body(e.getReason());
        }
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody RegisterRequestDTO body){
       try {
              User user = loginService.register(body);
              return ResponseEntity.ok(new RegisterResponseDTO(user.getEmail(), user.getName()));
         } catch (ResponseStatusException e) {
              return ResponseEntity.status(e.getStatusCode()).body(e.getReason());
       }
    }

    @PostMapping("/user/forgotPassword/{email}")
    public ResponseEntity<String> forgotPassword(@PathVariable String email){
        try {
            loginService.forgotPassword(email);
            return ResponseEntity.ok().build();
        } catch (ResponseStatusException e) {
            return ResponseEntity.status(e.getStatusCode()).body(e.getReason());
        }
    }

    @PostMapping("/user/resetPassword")
    public ResponseEntity<?> resetPassword(@RequestBody ResetPasswordRequestDTO body){
        try {
            loginService.resetUserPassword(body);
            return ResponseEntity.ok().build();
        }catch (ResponseStatusException e) {
            return ResponseEntity.status(e.getStatusCode()).body(e.getReason());
        }
    }
}
