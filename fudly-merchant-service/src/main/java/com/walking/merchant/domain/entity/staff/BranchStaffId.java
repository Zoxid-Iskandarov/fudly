package com.walking.merchant.domain.entity.staff;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.*;

import java.io.Serializable;
import java.util.UUID;

@Embeddable
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class BranchStaffId implements Serializable {

    @Column(name = "branch_id", nullable = false)
    private UUID branchId;

    @Column(name = "user_id", nullable = false)
    private UUID userId;
}
