package com.groupeisi.company.entities;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "user_accounts")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserAccount {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String email;

    @Column(nullable = false)
    private String password;

    // Rôle simple : USER ou ADMIN
    @Column(nullable = false)
    private String role = "USER";
}
