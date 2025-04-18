package com.shashimadushan.aliscapper.controller;

import com.shashimadushan.aliscapper.dto.AuthRequest;
import com.shashimadushan.aliscapper.dto.RegisterRequest;
import com.shashimadushan.aliscapper.service.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@CrossOrigin("*")
@RestController
@RequestMapping("/api/auth")
public class AuthController {
    @Autowired
    private AuthService authService;

    @PostMapping("/register")
    public ResponseEntity<String> register(@RequestBody RegisterRequest user) {
        return ResponseEntity.ok(authService.register(user));
    }

    @PostMapping("/login")
    public ResponseEntity<Map<String, String>> login(@RequestBody AuthRequest request) {
        Map<String, String> authResponse = authService.login(request.getUsername(), request.getPassword());
        return ResponseEntity.ok(authResponse);
    }

    @GetMapping("/check-admin")
    public ResponseEntity<Map<String, Boolean>> checkAdmin(@RequestHeader("Authorization") String token) {

        boolean isAdmin = authService.isAdmin(token);
        Map<String, Boolean> response = new HashMap<>();
        response.put("isAdmin", isAdmin);
        return ResponseEntity.ok(response);
    }
}