package com.walking.merchant.domain.entity.staff;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.OffsetDateTime;
import java.util.UUID;

@Entity
@Table(name = "branch_staff")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EntityListeners(AuditingEntityListener.class)
public class BranchStaff {
    @EmbeddedId
    private BranchStaffId id;

    @CreatedDate
    @Column(name = "added", nullable = false, updatable = false)
    private OffsetDateTime added;

    public BranchStaff(UUID branchId, UUID userId) {
        this.id = new BranchStaffId(branchId, userId);
    }
}
