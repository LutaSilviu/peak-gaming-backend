package com.peak.gaming.station;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "stations")
public class Station {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String code;

    @Enumerated(EnumType.STRING)
    @Column(name = "station_type", nullable = false)
    private StationType stationType;

    @Column(name = "display_order", nullable = false)
    private short displayOrder;

    public Station() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public StationType getStationType() {
        return stationType;
    }

    public void setStationType(StationType stationType) {
        this.stationType = stationType;
    }

    public short getDisplayOrder() {
        return displayOrder;
    }

    public void setDisplayOrder(short displayOrder) {
        this.displayOrder = displayOrder;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Station station)) return false;
        return id != null && id.equals(station.id);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}
