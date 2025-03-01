package com.shashimadushan.aliscapper.service;

import com.shashimadushan.aliscapper.dto.RegisterRequest;
import com.shashimadushan.aliscapper.model.User;
import com.shashimadushan.aliscapper.repo.UserRepository;
import com.shashimadushan.aliscapper.security.JwtUtil;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {
    @Autowired
    private UserRepository userRepository;
    @Autowired private PasswordEncoder passwordEncoder;
    @Autowired private JwtUtil jwtUtil;
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

    public String login(String username, String password) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        if (!passwordEncoder.matches(password, user.getPassword())) {
            throw new BadCredentialsException("Invalid credentials");
        }

        return jwtUtil.generateToken(username);
    }
}