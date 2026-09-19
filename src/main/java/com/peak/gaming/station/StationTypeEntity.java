package com.peak.gaming.station;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "station_types")
public class StationTypeEntity {

    @Id
    @Enumerated(EnumType.STRING)
    private StationType code;

    @Column(name = "short_name", nullable = false)
    private String shortName;

    @Column(name = "full_name", nullable = false)
    private String fullName;

    @Column(name = "people_per_unit", nullable = false)
    private short peoplePerUnit;

    @Column(name = "max_people", nullable = false)
    private short maxPeople;

    public StationTypeEntity() {
    }

    public StationType getCode() {
        return code;
    }

    public void setCode(StationType code) {
        this.code = code;
    }

    public String getShortName() {
        return shortName;
    }

    public void setShortName(String shortName) {
        this.shortName = shortName;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public short getPeoplePerUnit() {
        return peoplePerUnit;
    }

    public void setPeoplePerUnit(short peoplePerUnit) {
        this.peoplePerUnit = peoplePerUnit;
    }

    public short getMaxPeople() {
        return maxPeople;
    }

    public void setMaxPeople(short maxPeople) {
        this.maxPeople = maxPeople;
    }
}
