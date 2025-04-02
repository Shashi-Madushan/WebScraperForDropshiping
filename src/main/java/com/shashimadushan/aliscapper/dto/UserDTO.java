package com.shashimadushan.aliscapper.dto;

import com.shashimadushan.aliscapper.model.User;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;


@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class UserDTO {
    private String id;
    private String username;
    private String email;
    private String password;
    private User.Status status;
    private User.Role role;
    private Date createdAt;
}
