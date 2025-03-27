package com.shashimadushan.aliscapper.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class UserDataResponse {
    private String id;
    private String username;
    private String email;
    private String fullName;
//    private LocalDateTime createdAt;
//    private LocalDateTime updatedAt;


}