package com.peak.gaming.station;

import static org.assertj.core.api.Assertions.assertThat;

import com.peak.gaming.support.PostgresIntegrationTest;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.resttestclient.TestRestTemplate;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

class StationControllerIntegrationTest extends PostgresIntegrationTest {

    @Autowired
    private TestRestTemplate restTemplate;

    @Test
    void listsAllFifteenSeededStations() {
        ResponseEntity<List<StationDto>> response = restTemplate.exchange(
                "/api/stations", HttpMethod.GET, null, new ParameterizedTypeReference<List<StationDto>>() { });

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).hasSize(15);
    }

    @Test
    void listsTheThreeStationTypes() {
        ResponseEntity<List<StationTypeDto>> response = restTemplate.exchange(
                "/api/stations/types", HttpMethod.GET, null, new ParameterizedTypeReference<List<StationTypeDto>>() { });

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).hasSize(3);
    }
}
