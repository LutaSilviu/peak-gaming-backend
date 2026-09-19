package com.peak.gaming.zone;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "zones")
public class Zone {

    @Id
    private String code;

    @Column(nullable = false)
    private String label;

    @Column(nullable = false)
    private String tag;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false)
    private String description;

    @Column(name = "capacity_label", nullable = false)
    private String capacityLabel;

    @Column(name = "hours_label", nullable = false)
    private String hoursLabel;

    @Column(name = "display_order", nullable = false)
    private short displayOrder;

    public Zone() {
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getCapacityLabel() {
        return capacityLabel;
    }

    public void setCapacityLabel(String capacityLabel) {
        this.capacityLabel = capacityLabel;
    }

    public String getHoursLabel() {
        return hoursLabel;
    }

    public void setHoursLabel(String hoursLabel) {
        this.hoursLabel = hoursLabel;
    }

    public short getDisplayOrder() {
        return displayOrder;
    }

    public void setDisplayOrder(short displayOrder) {
        this.displayOrder = displayOrder;
    }
}
