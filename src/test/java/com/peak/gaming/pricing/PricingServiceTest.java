package com.peak.gaming.pricing;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import com.peak.gaming.station.StationType;
import java.math.BigDecimal;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class PricingServiceTest {

    @Mock
    private PriceTierRepository priceTierRepository;

    private PricingService pricingService;

    @BeforeEach
    void setUp() {
        pricingService = new PricingService(priceTierRepository);
    }

    private PriceTier tier(StationType type, boolean student, int hours, String price) {
        PriceTier t = new PriceTier();
        t.setStationType(type);
        t.setStudentRate(student);
        t.setDurationHours((short) hours);
        t.setPriceLei(new BigDecimal(price));
        return t;
    }

    @Nested
    class IsDayOffer {

        @ParameterizedTest
        @CsvSource({
                "12, 4, true",   // 12:00-16:00, exactly the window
                "12, 1, true",   // starts at window open
                "15, 1, true",   // ends exactly at window close
                "11, 2, false",  // starts before the window
                "15, 2, false",  // ends after the window
                "16, 1, false",  // starts exactly at window close
        })
        void matchesFrontendIsDayOfferRule(int hour, int duration, boolean expected) {
            assertThat(pricingService.isDayOffer(hour, duration)).isEqualTo(expected);
        }

        @Test
        void nullHourIsNeverADayOffer() {
            assertThat(pricingService.isDayOffer(null, 2)).isFalse();
        }
    }

    @Nested
    class UnitsFor {

        @Test
        void roundsUpWhenPeopleDoNotEvenlyDividePeoplePerUnit() {
            // ps5: 2 people per unit, 3 people => ceil(3/2) = 2 stations
            assertThat(pricingService.unitsFor(StationType.ps5, 3, (short) 2)).isEqualTo(2);
        }

        @Test
        void onePersonPerUnitMapsOneToOne() {
            assertThat(pricingService.unitsFor(StationType.pc, 5, (short) 1)).isEqualTo(5);
        }
    }

    @Nested
    class PricePerUnit {

        @Test
        void returnsEmptyWhenNoTierMatchesTheDuration() {
            when(priceTierRepository.findByStationTypeAndStudentRateAndDurationHours(
                    StationType.pc, false, (short) 7)).thenReturn(Optional.empty());

            Optional<BigDecimal> result = pricingService.pricePerUnit(StationType.pc, (short) 7, 10, false);

            assertThat(result).isEmpty();
        }

        @Test
        void returnsFullPriceOutsideTheDayOfferWindow() {
            when(priceTierRepository.findByStationTypeAndStudentRateAndDurationHours(
                    StationType.pc, false, (short) 3)).thenReturn(Optional.of(tier(StationType.pc, false, 3, "30")));

            Optional<BigDecimal> result = pricingService.pricePerUnit(StationType.pc, (short) 3, 20, false);

            assertThat(result).contains(new BigDecimal("30"));
        }

        @Test
        void capsPcPriceAt12LeiPerHourDuringTheDayOffer() {
            // tier price (30 for 3h = 10/h) is already below the 12/h cap, so it should win as-is
            when(priceTierRepository.findByStationTypeAndStudentRateAndDurationHours(
                    StationType.pc, false, (short) 3)).thenReturn(Optional.of(tier(StationType.pc, false, 3, "30")));

            Optional<BigDecimal> result = pricingService.pricePerUnit(StationType.pc, (short) 3, 13, false);

            assertThat(result).contains(new BigDecimal("30"));
        }

        @Test
        void capsPcPriceWhenTierPriceExceedsTheDayOfferCap() {
            // a hypothetical tier priced above 12/h must be capped down during the day offer
            when(priceTierRepository.findByStationTypeAndStudentRateAndDurationHours(
                    StationType.pc, false, (short) 2)).thenReturn(Optional.of(tier(StationType.pc, false, 2, "50")));

            Optional<BigDecimal> result = pricingService.pricePerUnit(StationType.pc, (short) 2, 12, false);

            assertThat(result).contains(new BigDecimal("24")); // 12 lei/h cap * 2h
        }

        @Test
        void capsPs5PriceAt20LeiPerHourDuringTheDayOffer() {
            when(priceTierRepository.findByStationTypeAndStudentRateAndDurationHours(
                    StationType.ps5, false, (short) 1)).thenReturn(Optional.of(tier(StationType.ps5, false, 1, "30")));

            Optional<BigDecimal> result = pricingService.pricePerUnit(StationType.ps5, (short) 1, 12, false);

            assertThat(result).contains(new BigDecimal("20")); // min(30, 20*1)
        }

        @Test
        void usesTheStudentTierWhenStudentRateIsTrue() {
            when(priceTierRepository.findByStationTypeAndStudentRateAndDurationHours(
                    StationType.ps5, true, (short) 1)).thenReturn(Optional.of(tier(StationType.ps5, true, 1, "25")));

            Optional<BigDecimal> result = pricingService.pricePerUnit(StationType.ps5, (short) 1, 20, true);

            assertThat(result).contains(new BigDecimal("25"));
        }
    }

    @Nested
    class TotalPrice {

        @Test
        void multipliesUnitPriceByUnitCount() {
            when(priceTierRepository.findByStationTypeAndStudentRateAndDurationHours(
                    StationType.pc, false, (short) 2)).thenReturn(Optional.of(tier(StationType.pc, false, 2, "22")));

            // 3 people at 1 person/unit => 3 units
            Optional<BigDecimal> result = pricingService.totalPrice(StationType.pc, (short) 2, 20, 3, false, (short) 1);

            assertThat(result).contains(new BigDecimal("66"));
        }

        @Test
        void returnsEmptyForNullType() {
            Optional<BigDecimal> result = pricingService.totalPrice(null, (short) 2, 20, 1, false, (short) 1);
            assertThat(result).isEmpty();
        }
    }
}
