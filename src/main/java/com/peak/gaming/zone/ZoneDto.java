package com.peak.gaming.zone;

public record ZoneDto(
        String code,
        String label,
        String tag,
        String title,
        String description,
        String capacityLabel,
        String hoursLabel
) {

    static ZoneDto from(Zone zone) {
        return new ZoneDto(
                zone.getCode(),
                zone.getLabel(),
                zone.getTag(),
                zone.getTitle(),
                zone.getDescription(),
                zone.getCapacityLabel(),
                zone.getHoursLabel()
        );
    }
}
