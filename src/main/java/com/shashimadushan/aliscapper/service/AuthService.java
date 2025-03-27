package com.shashimadushan.aliscapper.service;

import com.shashimadushan.aliscapper.dto.RegisterRequest;
import com.shashimadushan.aliscapper.model.User;
import com.shashimadushan.aliscapper.repo.UserRepository;
import com.shashimadushan.aliscapper.security.JwtUtil;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Service
public class AuthService {
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private JwtUtil jwtUtil;
    private ModelMapper modelMapper = new ModelMapper();

    public String register(RegisterRequest reqUser) {
        if (userRepository.existsByUsername(reqUser.getUsername())) {
            throw new IllegalArgumentException("Username is already taken");
        }
        if (userRepository.existsByEmail(reqUser.getEmail())) {
            throw new IllegalArgumentException("Email is already registered");
        }

        reqUser.setPassword(passwordEncoder.encode(reqUser.getPassword()));
        User user = modelMapper.map(reqUser, User.class);
        userRepository.save(user);
        return "User registered successfully";
    }

    public Map<String, String> login(String username, String password) {
        Map<String, String> response = new HashMap<>();
        Optional<User> optionalUser = userRepository.findByUsername(username);

        if (!optionalUser.isPresent()) {
            response.put("error", "User not found");
            return response;
        }

        User user = optionalUser.get();

        if (user.getStatus() == User.Status.INACTIVE) {
            response.put("error", "User account is inactive");
            return response;
        }

        if (!passwordEncoder.matches(password, user.getPassword())) {
            response.put("error", "Invalid credentials");
            return response;
        }

        String token = jwtUtil.generateToken(username, user.getRole());
        response.put("token", token);
        response.put("userRole", user.getRole().name());
        return response;
    }

    public boolean isAdmin(String token) {
        String role = jwtUtil.extractRole(token.substring(7));
        return User.Role.ADMIN.name().equals(role);
    }
}