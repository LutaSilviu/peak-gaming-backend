package com.peak.gaming.station;

public record StationTypeDto(
        StationType code,
        String shortName,
        String fullName,
        short peoplePerUnit,
        short maxPeople
) {

    static StationTypeDto from(StationTypeEntity entity) {
        return new StationTypeDto(
                entity.getCode(),
                entity.getShortName(),
                entity.getFullName(),
                entity.getPeoplePerUnit(),
                entity.getMaxPeople()
        );
    }
}
