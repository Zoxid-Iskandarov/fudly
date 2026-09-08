package com.walking.merchant.domain.repository;

import com.walking.merchant.domain.entity.staff.BranchStaff;
import com.walking.merchant.domain.entity.staff.BranchStaffId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BranchStaffRepository extends JpaRepository<BranchStaff, BranchStaffId> {
}
