package com.peak.gaming.station;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/stations")
@Tag(name = "Stations", description = "The room's fixed station catalog (PC1-PC9, PS1-PS5, VOL)")
public class StationController {

    private final StationService stationService;

    public StationController(StationService stationService) {
        this.stationService = stationService;
    }

    @GetMapping
    @Operation(summary = "List every physical station", description = "Returns every station row, ordered by type then display order.")
    public List<StationDto> listStations() {
        return stationService.listStations();
    }

    @GetMapping("/types")
    @Operation(summary = "List station types", description = "Returns the three station types (pc, ps5, volan) with their capacity rules.")
    public List<StationTypeDto> listStationTypes() {
        return stationService.listStationTypes();
    }
}
