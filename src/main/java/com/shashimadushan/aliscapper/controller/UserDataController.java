package com.shashimadushan.aliscapper.controller;

import com.shashimadushan.aliscapper.dto.ChangePasswordRequest;
import com.shashimadushan.aliscapper.dto.UserDTO;
import com.shashimadushan.aliscapper.dto.UserDataResponse;

import com.shashimadushan.aliscapper.security.JwtUtil;
import com.shashimadushan.aliscapper.service.UserDataService;
import com.shashimadushan.aliscapper.service.UserService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;


import java.util.List;


@RestController
@RequestMapping("/api/user")
public class UserDataController {

    private final UserService userService;
    private final JwtUtil jwtUtil;
    private ModelMapper modelMapper = new ModelMapper();
    @Autowired
    private UserDataService userDataService;

    @Autowired
    public UserDataController(UserService userService,JwtUtil jwtUtil) {
        this.userService = userService;
        this.jwtUtil = jwtUtil;
    }


    @GetMapping("/me")
    public ResponseEntity<UserDataResponse> getCurrentUser(@RequestHeader("Authorization") String token) {
        String username = jwtUtil.extractUsername(token.substring(7));
         UserDTO user = userDataService.getUserByUsername(username);
         UserDataResponse userDataResponse = modelMapper.map(user, UserDataResponse.class) ;

        return ResponseEntity.ok(userDataResponse);
    }


    @PostMapping("/deleteAccount")
    public boolean deleteAcoount(@RequestHeader("Authorization") String token){
        String username = jwtUtil.extractUsername(token.substring(7));
        if(userDataService.deleteUser(username)){
            return true;
        }
        return false;
    }


    @PostMapping("/change-password")
    public ResponseEntity<String> changePassword(@RequestBody ChangePasswordRequest request,
                                                 Authentication authentication,@RequestHeader("Authorization") String token) {
        if (authentication == null || !authentication.isAuthenticated()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("User is not authenticated");
        }

        String username = jwtUtil.extractUsername(token.substring(7));

        boolean isAdmin = authentication.getAuthorities()
                .stream()
                .anyMatch(a -> a.getAuthority().equals("ADMIN"));

        if (!isAdmin) {
            if (userDataService.changePassword(username, request.getCurrentPassword(), request.getNewPassword())){
                return ResponseEntity.ok("Password changed successfully");
            }else {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Password change failed");
            }

        } else {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Admins cannot change passwords this way");
        }


    }

}