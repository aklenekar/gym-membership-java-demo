package com.apexgym.profile.persistence;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "pricing")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Pricing {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(nullable = false)
    private Integer price;

    @Column(nullable = false)
    private Integer annualPrice;

    @Column(nullable = false)
    private Boolean mostFeatured;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "pricing_features", joinColumns = @JoinColumn(name = "pricing_id"))
    private List<PricingFeatures> features;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
    }

}

