package com.peak.gaming.reservation;

import com.peak.gaming.config.RequireAdminKey;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.time.LocalDate;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/rezervari")
@Tag(name = "Reservations", description = "Booking creation, availability, and admin management")
public class ReservationController {

    private final ReservationService reservationService;

    public ReservationController(ReservationService reservationService) {
        this.reservationService = reservationService;
    }

    @GetMapping
    @Operation(summary = "Get a day's reservations", description =
            "Returns every reservation for the given date, used by the wizard to compute free/tight/full slots "
                    + "and by the admin panel to list the day. Matches SLOTS_ENDPOINT's expected {rezervari: [...]} shape.")
    public DayReservationsResponse listByDate(
            @Parameter(description = "Date, ISO-8601", example = "2026-09-20")
            @RequestParam("data") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        return reservationService.listByDate(date);
    }

    @PostMapping
    @Operation(summary = "Create a reservation", description =
            "Recomputes the price server-side and assigns free stations; never trusts a client-submitted total. "
                    + "Returns 409 if not enough stations are free for the requested slot.")
    public ResponseEntity<CreateReservationResponse> create(@Valid @RequestBody CreateReservationRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(reservationService.create(request));
    }

    @GetMapping("/stats")
    @RequireAdminKey
    @SecurityRequirement(name = "adminKey")
    @Operation(summary = "Get a day's stats", description = "Admin only. Mirrors the frontend's computeDayStats().")
    public DayStatsDto statsForDate(
            @Parameter(description = "Date, ISO-8601", example = "2026-09-20")
            @RequestParam("data") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        return reservationService.statsForDate(date);
    }

    @PatchMapping("/{id}/stare")
    @RequireAdminKey
    @SecurityRequirement(name = "adminKey")
    @Operation(summary = "Confirm or cancel a reservation", description = "Admin only. Sets a reservation's status.")
    public ReservationDto updateStatus(@PathVariable Long id, @Valid @RequestBody UpdateReservationStatusRequest request) {
        return reservationService.updateStatus(id, request.stare());
    }
}
