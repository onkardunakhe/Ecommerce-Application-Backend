package com.Crud.Crud.Entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
public class MagicLoginToken {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String email;

    @Column(unique = true, nullable = false, length = 120)
    private String token;

    private boolean used = false;

    private LocalDateTime expiresAt;

    private LocalDateTime createdAt = LocalDateTime.now();
}
