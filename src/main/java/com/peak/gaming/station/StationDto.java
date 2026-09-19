package com.peak.gaming.station;

public record StationDto(Long id, String code, StationType stationType, short displayOrder) {

    static StationDto from(Station station) {
        return new StationDto(station.getId(), station.getCode(), station.getStationType(), station.getDisplayOrder());
    }
}
