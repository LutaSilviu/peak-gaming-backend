package com.peak.gaming.reservation;

import java.math.BigDecimal;

/** Mirrors the frontend's admin-stats.ts computeDayStats() output. */
public record DayStatsDto(
        int total,
        int peopleExpected,
        int stationHours,
        BigDecimal estimatedRevenue,
        int cancelled
) {
}
