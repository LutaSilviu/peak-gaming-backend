package com.peak.gaming.reservation;

import com.peak.gaming.station.Station;
import com.peak.gaming.station.StationType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.Table;
import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.util.LinkedHashSet;
import java.util.Set;

@Entity
@Table(name = "reservations")
public class Reservation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "confirmation_code", nullable = false, unique = true)
    private String confirmationCode;

    @Enumerated(EnumType.STRING)
    @Column(name = "station_type", nullable = false)
    private StationType stationType;

    @Column(name = "people_count", nullable = false)
    private short peopleCount;

    @Column(name = "student_rate", nullable = false)
    private boolean studentRate;

    @Column(name = "reservation_date", nullable = false)
    private LocalDate reservationDate;

    @Column(name = "start_hour", nullable = false)
    private short startHour;

    @Column(name = "duration_hours", nullable = false)
    private short durationHours;

    @Column(name = "customer_name", nullable = false)
    private String customerName;

    @Column(name = "customer_phone", nullable = false)
    private String customerPhone;

    @Column(nullable = false)
    private String source = "website";

    @Column(name = "total_price_lei", nullable = false)
    private BigDecimal totalPriceLei;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ReservationStatus status = ReservationStatus.noua;

    @Column(name = "created_at", nullable = false)
    private Instant createdAt = Instant.now();

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "reservation_stations",
            joinColumns = @JoinColumn(name = "reservation_id"),
            inverseJoinColumns = @JoinColumn(name = "station_id")
    )
    private Set<Station> stations = new LinkedHashSet<>();

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getConfirmationCode() {
        return confirmationCode;
    }

    public void setConfirmationCode(String confirmationCode) {
        this.confirmationCode = confirmationCode;
    }

    public StationType getStationType() {
        return stationType;
    }

    public void setStationType(StationType stationType) {
        this.stationType = stationType;
    }

    public short getPeopleCount() {
        return peopleCount;
    }

    public void setPeopleCount(short peopleCount) {
        this.peopleCount = peopleCount;
    }

    public boolean isStudentRate() {
        return studentRate;
    }

    public void setStudentRate(boolean studentRate) {
        this.studentRate = studentRate;
    }

    public LocalDate getReservationDate() {
        return reservationDate;
    }

    public void setReservationDate(LocalDate reservationDate) {
        this.reservationDate = reservationDate;
    }

    public short getStartHour() {
        return startHour;
    }

    public void setStartHour(short startHour) {
        this.startHour = startHour;
    }

    public short getDurationHours() {
        return durationHours;
    }

    public void setDurationHours(short durationHours) {
        this.durationHours = durationHours;
    }

    public String getCustomerName() {
        return customerName;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    public String getCustomerPhone() {
        return customerPhone;
    }

    public void setCustomerPhone(String customerPhone) {
        this.customerPhone = customerPhone;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public BigDecimal getTotalPriceLei() {
        return totalPriceLei;
    }

    public void setTotalPriceLei(BigDecimal totalPriceLei) {
        this.totalPriceLei = totalPriceLei;
    }

    public ReservationStatus getStatus() {
        return status;
    }

    public void setStatus(ReservationStatus status) {
        this.status = status;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }

    public Set<Station> getStations() {
        return stations;
    }

    public void setStations(Set<Station> stations) {
        this.stations = stations;
    }
}
