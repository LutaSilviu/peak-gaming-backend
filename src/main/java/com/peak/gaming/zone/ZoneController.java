package com.peak.gaming.zone;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/zones")
@Tag(name = "Zones", description = "The room's floor-plan zones (pc, ps5, volan, bar), used by the 3D room plan section")
public class ZoneController {

    private final ZoneService zoneService;

    public ZoneController(ZoneService zoneService) {
        this.zoneService = zoneService;
    }

    @GetMapping
    @Operation(summary = "List every zone", description = "Returns the four room zones, ordered as they appear in the floor plan.")
    public List<ZoneDto> listZones() {
        return zoneService.listZones();
    }
}
