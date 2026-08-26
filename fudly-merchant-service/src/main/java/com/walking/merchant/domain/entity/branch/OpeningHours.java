package com.walking.merchant.domain.entity.branch;

import java.time.DayOfWeek;
import java.time.LocalTime;

public record OpeningHours(
        DayOfWeek dayOfWeek,
        LocalTime opensAt,
        LocalTime closesAt
) {
}
