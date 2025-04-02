package com.shashimadushan.aliscapper.service;

import com.shashimadushan.aliscapper.dto.UserDTO;
import com.shashimadushan.aliscapper.model.User;
import com.shashimadushan.aliscapper.repo.UserRepository;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.List;

@Service
public class UserDataService {
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private UserRepository userRepository;
    private ModelMapper modelMapper = new ModelMapper();

    public boolean deleteUser(String username) {
        User user = userRepository.findById(username)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
        user.setStatus(User.Status.INACTIVE);
        User deleteduser= userRepository.save(user);
        if (deleteduser.getStatus() == User.Status.INACTIVE) {
            return true;
        }else {

            return false;
        }

    }
    public List<UserDTO> getAllUsers() {
        List<User> users = userRepository.findAll();
        return modelMapper.map(users, new TypeToken<List<UserDTO>>() {}.getType());
    }



    public UserDTO getUserByUsername(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with username: " + username));
        return modelMapper.map(user, UserDTO.class);
    }

    public boolean changePassword(String username, String currentPassword, String newpassword) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        if (!passwordEncoder.matches(currentPassword, user.getPassword())) {
            return false;
        }
        user.setPassword(passwordEncoder.encode(newpassword));
        userRepository.save(user);
        return true;
    }



    public UserDTO updateUserInfo(UserDTO userDTO) {
        User user = userRepository.findById(userDTO.getId())
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        // Update fields but preserve sensitive ones
        if (userDTO.getUsername() != null && !userDTO.getUsername().isEmpty()) {
            // Check if new username is already taken by another user
            if (!user.getUsername().equals(userDTO.getUsername()) &&
                    userRepository.existsByUsername(userDTO.getUsername())) {
                throw new IllegalArgumentException("Username already exists");
            }
            user.setUsername(userDTO.getUsername());
        }

        if (userDTO.getEmail() != null && !userDTO.getEmail().isEmpty()) {
            // Check if new email is already taken by another user
            if (!user.getEmail().equals(userDTO.getEmail()) &&
                    userRepository.existsByEmail(userDTO.getEmail())) {
                throw new IllegalArgumentException("Email already exists");
            }
            user.setEmail(userDTO.getEmail());
        }


        User savedUser = userRepository.save(user);
        return modelMapper.map(savedUser, UserDTO.class);
    }
    public long getTotalActiveUsers() {
        return userRepository.findAll().stream()
                .filter(user -> user.getStatus() == User.Status.ACTIVE)
                .count();
    }

    public long getNewUsersThisWeek() {
        LocalDateTime oneWeekAgo = LocalDateTime.now().minus(7, ChronoUnit.DAYS);
        Date oneWeekAgoDate = Date.from(oneWeekAgo.atZone(ZoneId.systemDefault()).toInstant());

        return userRepository.findAll().stream()
                .filter(user -> user.getCreatedAt() != null && user.getCreatedAt().after(oneWeekAgoDate))
                .count();
    }

}
