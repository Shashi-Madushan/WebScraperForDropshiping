package com.shashimadushan.aliscapper.model;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "users")
@AllArgsConstructor
@NoArgsConstructor
@Getter @Setter
public class User {
    @Id
    private String id;
    @Indexed(unique = true)
    private String username;
    private String email;
    private String password;
    private Status status=Status.ACTIVE;
    private Role role = Role.USER;

    public enum Role {
        USER, ADMIN
    }
    public enum Status {
        ACTIVE, INACTIVE
    }
}