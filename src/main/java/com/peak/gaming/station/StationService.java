package com.peak.gaming.station;

import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class StationService {

    private final StationRepository stationRepository;
    private final StationTypeRepository stationTypeRepository;

    public StationService(StationRepository stationRepository, StationTypeRepository stationTypeRepository) {
        this.stationRepository = stationRepository;
        this.stationTypeRepository = stationTypeRepository;
    }

    public List<StationDto> listStations() {
        return stationRepository.findAllByOrderByStationTypeAscDisplayOrderAsc().stream()
                .map(StationDto::from)
                .toList();
    }

    public List<StationTypeDto> listStationTypes() {
        return stationTypeRepository.findAll().stream()
                .map(StationTypeDto::from)
                .toList();
    }
}
