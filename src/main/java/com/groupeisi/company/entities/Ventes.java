package com.groupeisi.company.entities;

import jakarta.persistence.*;
import lombok.*;

import java.util.Date;

@Entity
@Table(name = "ventes")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Ventes {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(nullable = false)
    private Date dateP;

    @Column(nullable = false)
    private double quantity;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_ref", nullable = false)
    @ToString.Exclude
    private Produits product;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    @ToString.Exclude
    private UserAccount user;
}
