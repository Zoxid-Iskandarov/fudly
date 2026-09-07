package com.walking.listing.repository.specification;

import com.walking.listing.domain.entity.listing.Listing;
import com.walking.listing.domain.entity.listing.ListingStatus;
import lombok.experimental.UtilityClass;
import org.springframework.data.jpa.domain.Specification;

import java.util.UUID;

@UtilityClass
public class ListingSpecification {

    public Specification<Listing> hasStatus(ListingStatus status) {
        return (root, query, cb) -> {
            if (status == null) return null;
            return cb.equal(root.get("status"), status);
        };
    }

    public Specification<Listing> hasBranchId(UUID branchId) {
        return (root, query, cb) -> {
            if (branchId == null) return null;
            return cb.equal(root.get("branchId"), branchId);
        };
    }
}
