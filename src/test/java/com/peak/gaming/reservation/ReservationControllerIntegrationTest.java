package com.peak.gaming.reservation;

import static org.assertj.core.api.Assertions.assertThat;

import com.peak.gaming.exception.ApiErrorResponse;
import com.peak.gaming.support.PostgresIntegrationTest;
import java.time.LocalDate;
import java.util.Map;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.resttestclient.TestRestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

class ReservationControllerIntegrationTest extends PostgresIntegrationTest {

    @Autowired
    private TestRestTemplate restTemplate;

    private static final LocalDate TEST_DATE = LocalDate.of(2030, 6, 15);
    private static final String ADMIN_KEY_HEADER = "X-Admin-Key";
    private static final String ADMIN_KEY_VALUE = "test-admin-key";

    private HttpEntity<Map<String, Object>> jsonBody(Map<String, Object> body) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        return new HttpEntity<>(body, headers);
    }

    private HttpEntity<Map<String, Object>> jsonBodyAsAdmin(Map<String, Object> body) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set(ADMIN_KEY_HEADER, ADMIN_KEY_VALUE);
        return new HttpEntity<>(body, headers);
    }

    private Map<String, Object> bookingPayload(String stationType, int people, int hour, int duration, String phone) {
        return Map.of(
                "tip", stationType,
                "persoane", people,
                "student", false,
                "data", TEST_DATE.toString(),
                "ora", hour,
                "durata", duration,
                "nume", "Integration Test",
                "telefon", phone,
                "sursa", "test"
        );
    }

    private CreateReservationResponse createBooking(String stationType, int people, int hour, int duration, String phone) {
        ResponseEntity<CreateReservationResponse> response = restTemplate.postForEntity(
                "/api/rezervari", jsonBody(bookingPayload(stationType, people, hour, duration, phone)),
                CreateReservationResponse.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        return response.getBody();
    }

    @Test
    void createsReservationAndReturnsAssignedStations() {
        ResponseEntity<CreateReservationResponse> response = restTemplate.postForEntity(
                "/api/rezervari", jsonBody(bookingPayload("pc", 1, 13, 1, "0745111111")),
                CreateReservationResponse.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(response.getBody().cod()).matches("^PK-[A-Z0-9]{5}$");
        assertThat(response.getBody().total()).isNotNull();
        assertThat(response.getBody().statii()).isNotEmpty();
    }

    @Test
    void listsReservationsCreatedForADate() {
        createBooking("ps5", 2, 13, 1, "0745222222");

        ResponseEntity<DayReservationsResponse> response = restTemplate.getForEntity(
                "/api/rezervari?data=" + TEST_DATE, DayReservationsResponse.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody().rezervari()).isNotEmpty();
    }

    @Test
    void rejectsBookingWithInvalidPhoneNumber() {
        ResponseEntity<ApiErrorResponse> response = restTemplate.postForEntity(
                "/api/rezervari", jsonBody(bookingPayload("pc", 1, 12, 1, "not-a-phone")), ApiErrorResponse.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody().eroare()).isNotBlank();
    }

    @Test
    void rejectsOverbookingBeyondStationCapacity() {
        // volan has exactly 1 physical station
        createBooking("volan", 1, 16, 1, "0745333333");

        ResponseEntity<ApiErrorResponse> second = restTemplate.postForEntity(
                "/api/rezervari", jsonBody(bookingPayload("volan", 1, 16, 1, "0745333344")), ApiErrorResponse.class);

        assertThat(second.getStatusCode()).isEqualTo(HttpStatus.CONFLICT);
        assertThat(second.getBody().eroare()).isNotBlank();
    }

    @Test
    void allowsBackToBackBookingsThatDoNotOverlap() {
        createBooking("volan", 1, 18, 1, "0745444444");

        ResponseEntity<CreateReservationResponse> nextSlot = restTemplate.postForEntity(
                "/api/rezervari", jsonBody(bookingPayload("volan", 1, 19, 1, "0745444455")),
                CreateReservationResponse.class);

        assertThat(nextSlot.getStatusCode()).isEqualTo(HttpStatus.CREATED);
    }

    @Test
    void adminStatsRequireTheAdminKeyHeader() {
        ResponseEntity<ApiErrorResponse> withoutKey = restTemplate.getForEntity(
                "/api/rezervari/stats?data=" + TEST_DATE, ApiErrorResponse.class);
        assertThat(withoutKey.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);

        HttpHeaders headers = new HttpHeaders();
        headers.set(ADMIN_KEY_HEADER, ADMIN_KEY_VALUE);
        ResponseEntity<DayStatsDto> withKey = restTemplate.exchange(
                "/api/rezervari/stats?data=" + TEST_DATE, HttpMethod.GET, new HttpEntity<>(headers), DayStatsDto.class);
        assertThat(withKey.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(withKey.getBody()).isNotNull();
    }

    @Test
    void adminCanConfirmAReservationButAnonymousCannot() {
        CreateReservationResponse created = createBooking("pc", 1, 20, 1, "0745555555");

        // We don't get the numeric id back from the create response (only cod/total/statii,
        // matching the frontend contract), so look it up via the day listing.
        ResponseEntity<DayReservationsResponse> day = restTemplate.getForEntity(
                "/api/rezervari?data=" + TEST_DATE, DayReservationsResponse.class);
        Long id = day.getBody().rezervari().stream()
                .filter(r -> created.cod().equals(r.cod()))
                .findFirst().orElseThrow().id();

        ResponseEntity<ReservationDto> patched = restTemplate.exchange(
                "/api/rezervari/" + id + "/stare", HttpMethod.PATCH,
                jsonBodyAsAdmin(Map.of("stare", "confirmata")), ReservationDto.class);

        assertThat(patched.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(patched.getBody().stare()).isEqualTo(ReservationStatus.confirmata);

        ResponseEntity<ApiErrorResponse> rejected = restTemplate.exchange(
                "/api/rezervari/" + id + "/stare", HttpMethod.PATCH,
                jsonBody(Map.of("stare", "anulata")), ApiErrorResponse.class);
        assertThat(rejected.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
    }
}
