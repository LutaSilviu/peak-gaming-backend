package com.peak.gaming.zone;

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

class ZoneControllerIntegrationTest extends PostgresIntegrationTest {

    @Autowired
    private TestRestTemplate restTemplate;

    @Test
    void listsTheFourSeededZonesInDisplayOrder() {
        ResponseEntity<List<ZoneDto>> response = restTemplate.exchange(
                "/api/zones", HttpMethod.GET, null, new ParameterizedTypeReference<List<ZoneDto>>() { });

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        List<ZoneDto> zones = response.getBody();
        assertThat(zones).hasSize(4);
        assertThat(zones.stream().map(ZoneDto::code)).containsExactly("pc", "ps5", "volan", "bar");
    }
}
