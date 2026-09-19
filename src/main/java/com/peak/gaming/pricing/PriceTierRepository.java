package com.peak.gaming.pricing;

import com.peak.gaming.station.StationType;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PriceTierRepository extends JpaRepository<PriceTier, Long> {

    List<PriceTier> findByStationTypeOrderByDurationHoursAsc(StationType stationType);

    Optional<PriceTier> findByStationTypeAndStudentRateAndDurationHours(
            StationType stationType, boolean studentRate, short durationHours);
}
