package com.groupeisi.company.dto;

import lombok.*;

import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AchatsDto {
    private Long id;
    private Date dateP;
    private double quantity;
    private String productRef;
    private Long userId;
}
