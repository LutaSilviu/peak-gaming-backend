package com.peak.gaming.reservation;

import jakarta.validation.constraints.NotNull;

public record UpdateReservationStatusRequest(@NotNull ReservationStatus stare) {
}
