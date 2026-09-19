package com.peak.gaming.pricing;

import com.peak.gaming.station.StationType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.math.BigDecimal;

@Entity
@Table(name = "price_tiers")
public class PriceTier {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(name = "station_type", nullable = false)
    private StationType stationType;

    @Column(name = "student_rate", nullable = false)
    private boolean studentRate;

    @Column(name = "duration_hours", nullable = false)
    private short durationHours;

    @Column(name = "price_lei", nullable = false)
    private BigDecimal priceLei;

    public PriceTier() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public StationType getStationType() {
        return stationType;
    }

    public void setStationType(StationType stationType) {
        this.stationType = stationType;
    }

    public boolean isStudentRate() {
        return studentRate;
    }

    public void setStudentRate(boolean studentRate) {
        this.studentRate = studentRate;
    }

    public short getDurationHours() {
        return durationHours;
    }

    public void setDurationHours(short durationHours) {
        this.durationHours = durationHours;
    }

    public BigDecimal getPriceLei() {
        return priceLei;
    }

    public void setPriceLei(BigDecimal priceLei) {
        this.priceLei = priceLei;
    }
}
