package com.asset.asset_management.model;


import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "assets")
public class Asset {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String assetName;
    private String assetType;
    private String serialNumber;
    private String department;
    private String description;


    private LocalDate purchaseDate;

    private Double purchaseCost;
    private Double currentValue;

    private Integer usefulLifeYears;
    private Double salvageValue;

    private LocalDate warrantyExpiry;

    private String status;
    private String location;
}