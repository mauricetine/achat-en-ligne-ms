package com.groupeisi.company.entities;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "produits")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Produits {

    @Id
    @Column(nullable = false, unique = true)
    private String ref;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private double stock;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    @ToString.Exclude
    private UserAccount user;
}
