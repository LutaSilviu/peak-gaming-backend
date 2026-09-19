package com.peak.gaming.zone;

import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class ZoneService {

    private final ZoneRepository zoneRepository;

    public ZoneService(ZoneRepository zoneRepository) {
        this.zoneRepository = zoneRepository;
    }

    public List<ZoneDto> listZones() {
        return zoneRepository.findAllByOrderByDisplayOrderAsc().stream()
                .map(ZoneDto::from)
                .toList();
    }
}
