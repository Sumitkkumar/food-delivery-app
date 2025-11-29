package com.example.frontend.auth;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;
import java.util.Set;

@Document(collection = "users")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class User {
    @Id
    private String id;
    private String username; // unique
    private String email;    // optional
    private String passwordHash;
    private Set<String> roles; // e.g. ["ROLE_USER"], ["ROLE_MERCHANT"], ["ROLE_ADMIN"]
    private boolean enabled = true;
    private Instant createdAt;
}
