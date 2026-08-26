package com.walking.merchant.repository;

import com.walking.merchant.domain.dto.branch.BranchNearResponse;
import com.walking.merchant.domain.entity.branch.Branch;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface BranchRepository extends JpaRepository<Branch, UUID> {

    @Query(value = """
            SELECT b.id,
                   b.merchant_id AS "merchantId",
                   b.address,
                   b.latitude,
                   b.longitude,
                   b.status,
                   round(
                           (6371 * acos(
                                   least(1.0, greatest(-1.0,
                                       cos(radians(:latitude)) * cos(radians(b.latitude)) *
                                       cos(radians(b.longitude) - radians(:longitude)) +
                                       sin(radians(:latitude)) * sin(radians(b.latitude))
                                   ))
                                   ))::numeric, 2
                   )             AS "distanceKm"
            FROM branch b
            JOIN merchant m ON b.merchant_id = m.id
            WHERE (
                    6371 * acos(
                              least(1.0, greatest(-1.0,
                                  cos(radians(:latitude)) * cos(radians(b.latitude)) *
                                  cos(radians(b.longitude) - radians(:longitude)) +
                                  sin(radians(:latitude)) * sin(radians(b.latitude))
                              ))
                             )
                      ) <= :radius
              AND m.status = 'ACTIVE'
              AND b.status != 'CLOSED'
              AND (:onlyOpen = false OR b.status = 'OPEN')
            ORDER BY "distanceKm"
            """, nativeQuery = true)
    List<BranchNearResponse> findNearByBranches(
            @Param("latitude") Double latitude,
            @Param("longitude") Double longitude,
            @Param("radius") Integer radius,
            @Param("onlyOpen") Boolean onlyOpen);

    @Query("select b from Branch b join fetch b.merchant where b.id = :branchId")
    Optional<Branch> findByIdWithMerchant(@Param("branchId") UUID branchId);

    @Modifying
    @Query("update Branch b set b.status = 'CLOSED' where b.merchant.id = :merchantId and b.status != 'CLOSED'")
    void closeAllBranchesByMerchantId(@Param("merchantId") UUID merchantId);
}
