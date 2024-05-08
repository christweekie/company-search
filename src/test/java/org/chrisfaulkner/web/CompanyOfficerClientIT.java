package org.chrisfaulkner.web;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.tomakehurst.wiremock.WireMockServer;
import org.chrisfaulkner.model.Company;
import org.chrisfaulkner.model.officer.OfficerDetail;
import org.chrisfaulkner.model.officer.OfficerResponse;
import org.chrisfaulkner.model.web.CompanyResponse;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.http.HttpHeaders.CONTENT_TYPE;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@ActiveProfiles("test")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_CLASS)
class CompanyOfficerClientIT {


    private static final String PATH = "/TruProxyAPI/rest/Companies/v1/Search";

    private static final String OFFICER_PATH = "/TruProxyAPI/rest/Companies/v1/Officers";

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private CompanyOfficerClient webClient;

    @BeforeAll
    static void beforeAll() {
        final var wireMockServer = new WireMockServer(8080);
        wireMockServer.start();
        configureFor("localhost", wireMockServer.port());
    }

    @Test
    public void givenCorrectRequest_whenRequestCompany_thenReturnResponseStatusOk() throws IOException {

        // Arrange
        final var companyJson = Files.readString(Path.of("src/test/resources/__files/expectations/bbc-company.json"));
        final var expectedCompany = objectMapper.readValue(companyJson, Company.class);
        final var searchTerms = Map.of("Query", "06500244");
        stubFor(get(urlEqualTo(PATH + "?Query=06500244"))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "application/json")
                        .withBodyFile("bbc-company-response.json")));

        // Act
        ResponseEntity<CompanyResponse> responseEntity = webClient.getCompanyResponse(searchTerms);

        // Assertions
        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
        final var companyResponse = responseEntity.getBody();
        assertThat(companyResponse.getPageNumber()).isEqualTo(1);
        Assertions.assertThat(companyResponse.getItems()).hasSize(1);
        assertThat(companyResponse.getKind()).isEqualTo("search#companies");
        assertThat(companyResponse.getTotalResults()).isEqualTo(20);
        final var company = responseEntity.getBody().getItems().getFirst();
        Assertions.assertThat(company).isEqualTo(expectedCompany);
    }

    @Test
    public void givenOfficerRequest_whenRequestOfficers_thenReturnResponseStatusOk() throws IOException {

        // Arrange
        final var officerJson = Files.readString(Path.of("src/test/resources/__files/expectations/resigned-officer.json"));
        final var expectedOfficer = objectMapper.readValue(officerJson, OfficerDetail.class);

        final var searchTerms = Map.of("CompanyNumber", "06500244");
        stubFor(get(urlEqualTo(OFFICER_PATH + "?CompanyNumber=06500244"))
                .willReturn(aResponse()
                        .withStatus(HttpStatus.OK.value())
                        .withHeader(CONTENT_TYPE, APPLICATION_JSON_VALUE)
                        .withBodyFile("officer-response.json")));

        // Act
        ResponseEntity<OfficerResponse> responseEntity = webClient.getOfficers(searchTerms);

        // Assertions
        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
        final var officerResponse = responseEntity.getBody();
        assertThat(officerResponse.getEtag()).isEqualTo("6dd2261e61776d79c2c50685145fac364e75e24e");
        Assertions.assertThat(officerResponse.getItems()).hasSize(1);
        assertThat(officerResponse.getKind()).isEqualTo("officer-list");
        Assertions.assertThat(officerResponse.getLinks().getSelf()).isEqualTo("/company/10241297/officers");
        assertThat(officerResponse.getItemsPerPage()).isEqualTo(35);
        final var officer = responseEntity.getBody().getItems().getFirst();
        assertThat(officer).isEqualTo(expectedOfficer);
    }

}
