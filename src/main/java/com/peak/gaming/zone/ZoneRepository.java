package com.peak.gaming.zone;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ZoneRepository extends JpaRepository<Zone, String> {

    List<Zone> findAllByOrderByDisplayOrderAsc();
}
