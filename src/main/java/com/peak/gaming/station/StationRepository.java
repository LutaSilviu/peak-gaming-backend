package com.peak.gaming.station;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StationRepository extends JpaRepository<Station, Long> {

    List<Station> findByStationTypeOrderByDisplayOrder(StationType stationType);

    List<Station> findAllByOrderByStationTypeAscDisplayOrderAsc();
}
