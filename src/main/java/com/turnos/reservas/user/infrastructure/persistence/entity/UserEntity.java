package com.turnos.reservas.user.infrastructure.persistence.entity;

import java.time.Instant;
import java.util.HashSet;
import java.util.Set;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "app_user")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 50)
    private String login;

    @Column(name = "password_hash", nullable = false)
    private String passwordHash;

    @Column(name = "first_name", length = 50)
    private String firstName;

    @Column(name = "last_name", length = 50)
    private String lastName;

    @Column(nullable = false, unique = true, length = 254)
    private String email;

    @Column(name = "image_url", length = 256)
    private String imageUrl;

    @Column(nullable = false)
    private boolean activated;

    @Column(name = "lang_key", length = 10)
    private String langKey;

    @Column(name = "created_date", nullable = false)
    private Instant createdDate;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "user_authority", joinColumns = @JoinColumn(name = "user_id"))
    @Column(name = "authority_name", length = 50)
    @Builder.Default
    private Set<String> authorities = new HashSet<>();
}
