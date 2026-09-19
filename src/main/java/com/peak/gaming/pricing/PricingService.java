package com.peak.gaming.pricing;

import com.peak.gaming.station.StationType;
import java.math.BigDecimal;
import java.util.Optional;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Server-side mirror of the frontend's booking-pricing.ts. The two must stay
 * in lockstep: whichever tariff table changes, the other has to change too.
 * The client-submitted total is never trusted — this service always
 * recomputes it from the price_tiers table.
 */
@Service
@Transactional(readOnly = true)
public class PricingService {

    private static final int DAY_OFFER_START_HOUR = 12;
    private static final int DAY_OFFER_END_HOUR = 16;
    private static final BigDecimal PC_DAY_OFFER_RATE = BigDecimal.valueOf(12);
    private static final BigDecimal PS5_DAY_OFFER_RATE = BigDecimal.valueOf(20);

    private final PriceTierRepository priceTierRepository;

    public PricingService(PriceTierRepository priceTierRepository) {
        this.priceTierRepository = priceTierRepository;
    }

    /** True whenever the whole booked interval falls inside the 12:00-16:00 day-offer window. */
    public boolean isDayOffer(Integer startHour, int durationHours) {
        return startHour != null && startHour >= DAY_OFFER_START_HOUR
                && startHour + durationHours <= DAY_OFFER_END_HOUR;
    }

    public int unitsFor(StationType stationType, int peopleCount, short peoplePerUnit) {
        return (int) Math.ceil(peopleCount / (double) peoplePerUnit);
    }

    /** The per-station price for one booking, or empty if no tier matches that duration. */
    public Optional<BigDecimal> pricePerUnit(
            StationType stationType, short durationHours, Integer startHour, boolean studentRate) {
        return priceTierRepository
                .findByStationTypeAndStudentRateAndDurationHours(stationType, studentRate, durationHours)
                .map(PriceTier::getPriceLei)
                .map(price -> capForDayOffer(stationType, price, durationHours, startHour));
    }

    private BigDecimal capForDayOffer(StationType stationType, BigDecimal price, short durationHours, Integer startHour) {
        if (!isDayOffer(startHour, durationHours)) {
            return price;
        }
        BigDecimal hourlyRate = stationType == StationType.pc ? PC_DAY_OFFER_RATE : PS5_DAY_OFFER_RATE;
        BigDecimal cap = hourlyRate.multiply(BigDecimal.valueOf(durationHours));
        return price.min(cap);
    }

    /** Total price for the whole booking (price per station * number of stations), or empty if invalid. */
    public Optional<BigDecimal> totalPrice(
            StationType stationType, short durationHours, Integer startHour, int peopleCount,
            boolean studentRate, short peoplePerUnit) {
        if (stationType == null) {
            return Optional.empty();
        }
        return pricePerUnit(stationType, durationHours, startHour, studentRate)
                .map(unitPrice -> unitPrice.multiply(BigDecimal.valueOf(unitsFor(stationType, peopleCount, peoplePerUnit))));
    }
}
