package com.walking.listing.domain.entity;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.UUID;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EntityListeners(AuditingEntityListener.class)
public class Listing {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "branch_id", nullable = false)
    private UUID branchId;

    @Column(nullable = false, length = 150)
    private String title;

    private String description;

    @Column(name = "original_price", nullable = false, precision = 10, scale = 2)
    private BigDecimal originalPrice;

    @Column(name = "discounted_price", nullable = false, precision = 10, scale = 2)
    private BigDecimal discountedPrice;

    private Integer quantity;

    @Column(name = "expiration_time", nullable = false)
    private OffsetDateTime expirationTime;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 50)
    private ListingStatus status;

    @Version
    @Column(nullable = false)
    private Integer version;

    @CreatedDate
    @Column(nullable = false, updatable = false)
    private OffsetDateTime created;

    @LastModifiedDate
    @Column(nullable = false)
    private OffsetDateTime updated;
}
