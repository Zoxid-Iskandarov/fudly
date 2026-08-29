package com.walking.merchant.repository;

import com.walking.merchant.domain.entity.staff.BranchStaff;
import com.walking.merchant.domain.entity.staff.BranchStaffId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BranchStaffRepository extends JpaRepository<BranchStaff, BranchStaffId> {
}
