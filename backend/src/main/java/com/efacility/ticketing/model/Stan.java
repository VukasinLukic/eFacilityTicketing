package com.efacility.ticketing.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(
        name = "apartment",
        uniqueConstraints = @UniqueConstraint(
                name = "uk_apartment_building_number",
                columnNames = {"building_id", "number"}
        )
)
@Getter
@Setter
@NoArgsConstructor
public class Stan implements DomainEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String number;

    @Column(nullable = false)
    private int floor;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "building_id", nullable = false)
    private Zgrada building;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tenant_id")
    private Korisnik tenant;
}
