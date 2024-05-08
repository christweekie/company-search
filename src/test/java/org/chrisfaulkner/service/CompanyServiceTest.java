package org.chrisfaulkner.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.chrisfaulkner.jpa.CompanyEntity;
import org.chrisfaulkner.jpa.CompanyRepository;
import org.chrisfaulkner.web.CompanyOfficerClient;
import org.chrisfaulkner.model.Company;
import org.chrisfaulkner.model.officer.Officer;
import org.chrisfaulkner.model.officer.OfficerDetail;
import org.chrisfaulkner.model.officer.OfficerResponse;
import org.chrisfaulkner.model.web.CompanyResponse;
import org.chrisfaulkner.model.web.CompanySearch;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CompanyServiceTest {

    private static final long VERIFY_ASYNC_WAIT = 1000;
    private static final ObjectMapper objectMapper = new ObjectMapper();
    private static final String ACTIVE_TITLE = "Active CompanyEntity Ltd";
    private static final String INACTIVE_TITLE = "Closed CompanyEntity Ltd";
    private static final String ACTIVE_NUM = "100";
    private static final String INACTIVE_NUM = "999";
    private static OfficerDetail resignedOfficer;
    private static OfficerDetail officer;

    private final Company activeCompany = Company.builder()
            .title(ACTIVE_TITLE)
            .companyStatus("active")
            .companyNumber(ACTIVE_NUM)
            .officers(Collections.emptyList())
            .build();
    private final Company inActiveCompany = Company.builder()
            .title(INACTIVE_TITLE)
            .companyStatus("closed")
            .companyNumber(INACTIVE_NUM)
            .officers(Collections.emptyList())
            .build();

    @Mock
    private CompanyOfficerClient companyOfficerClient;

    @Mock
    private CompanyRepository companyRepository;

    @InjectMocks
    private CompanyService service;

    @BeforeAll
    static void beforeAll() throws IOException {
        objectMapper.registerModule(new JavaTimeModule());
        final var resigned = Files.readString(Path.of("src/test/resources/__files/expectations/resigned-officer.json"));
        final var active = Files.readString(Path.of("src/test/resources/__files/expectations/officer.json"));
        resignedOfficer = objectMapper.readValue(resigned, OfficerDetail.class);
        officer = objectMapper.readValue(active, OfficerDetail.class);
    }

    @Test
    void givenCompanySearch_whenSearchReturnsNothing_thenReturnEmptyList() {

        when(companyOfficerClient.getCompanyResponse(Map.of(CompanySearch.COMPANY_SEARCH, ACTIVE_NUM)))
                .thenReturn(ResponseEntity.of(Optional.empty()));

        final var response = service.getCompanies(new CompanySearch(null, ACTIVE_NUM), true);

        assertThat(response.getTotalResults()).isEqualTo(0);
        assertThat(response.getItems()).isEmpty();
        verifyNoInteractions(companyRepository);
    }

    @Test
    void givenCompanySearch_whenSearchFilteredAndHasOfficers_thenReturnActiveOnlyExcludeAllOfficers() {

        final var companyResponse = CompanyResponse.builder()
                .items(List.of(activeCompany, inActiveCompany)).build();
        // NOTE - we are filtering in the service tier
        final var officersResponse = OfficerResponse.builder()
                        .items(List.of(officer, resignedOfficer)).build();

        when(companyOfficerClient.getCompanyResponse(Map.of(CompanySearch.COMPANY_SEARCH, ACTIVE_NUM)))
                .thenReturn(ResponseEntity.of(Optional.of(companyResponse)));
        when(companyOfficerClient.getOfficers(Map.of(CompanySearch.OFFICER_SEARCH, ACTIVE_NUM)))
                .thenReturn(ResponseEntity.of(Optional.of(officersResponse)));

        final var search = new CompanySearch(null, ACTIVE_NUM);

        // Act
        final var response = service.getCompanies(search, true);

        assertThat(response.getTotalResults()).isEqualTo(1);
        final var companies = response.getItems();
        assertThat(companies).hasSize(1);
        final var actualCompany = companies.getFirst();
        assertThat(actualCompany.getOfficers()).hasSize(1);
        final Officer actualOfficer = actualCompany.getOfficers().getFirst();
        assertThat(actualOfficer.getClass()).isNotSameAs(OfficerDetail.class);
        assertThat(actualOfficer.getName()).isEqualTo(officer.getName());
        assertThat(actualOfficer.getAppointedOn()).isEqualTo(officer.getAppointedOn());
        assertThat(actualOfficer.getOfficerRole()).isEqualTo(officer.getOfficerRole());
        // NOTE - it is async so we may need a very small possible timeout on the verify
        verify(companyRepository, timeout(VERIFY_ASYNC_WAIT)).findByCompanyNumber(ACTIVE_NUM);
        verify(companyRepository, timeout(VERIFY_ASYNC_WAIT)).save(any(CompanyEntity.class));
        verifyNoMoreInteractions(companyRepository);
    }

    @Test
    void givenCompanyNonFilteredMultipleOfficers_whenSearch_thenReturnAll() {
        final var searchName = "ACME";

        final var companyResponse = CompanyResponse.builder()
                .items(List.of(activeCompany, inActiveCompany)).build();

        when(companyOfficerClient.getCompanyResponse(Map.of(CompanySearch.COMPANY_SEARCH, searchName)))
                .thenReturn(ResponseEntity.of(Optional.of(companyResponse)));
        when(companyOfficerClient.getOfficers(Map.of(CompanySearch.OFFICER_SEARCH, ACTIVE_NUM)))
                .thenReturn(ResponseEntity.of(Optional.empty()));
        when(companyOfficerClient.getOfficers(Map.of(CompanySearch.OFFICER_SEARCH, INACTIVE_NUM)))
                .thenReturn(ResponseEntity.of(Optional.empty()));

        final var search = new CompanySearch(searchName, null);

        final var response = service.getCompanies(search, false);

        assertThat(response.getTotalResults()).isEqualTo(2);
        final var companies = response.getItems();
        assertThat(companies).hasSize(2);
        final var actualActiveCompany = companies.get(0);
        final var actualInactiveCompany = companies.get(1);
        assertThat(actualActiveCompany).usingRecursiveComparison()
                .ignoringFields("officers")
                .isEqualTo(activeCompany.buildCompanyOfficer(Collections.emptyList()));
        assertThat(actualInactiveCompany).usingRecursiveComparison()
                .ignoringFields("officers")
                .isEqualTo(inActiveCompany.buildCompanyOfficer(Collections.emptyList()));
        // NOTE - this is a search for active and inactive so we save both
        verify(companyRepository, timeout(VERIFY_ASYNC_WAIT)).findByCompanyNumber(ACTIVE_NUM);
        verify(companyRepository, timeout(VERIFY_ASYNC_WAIT)).findByCompanyNumber(INACTIVE_NUM);
        verify(companyRepository, times(2)).save(any(CompanyEntity.class));
        verifyNoMoreInteractions(companyRepository);
    }

    @Test
    void givenCompanySearchByName_whenSearch_thenReturn() {

        final var companyResponse = CompanyResponse.builder().items(List.of(activeCompany)).build();
        final var officersResponse = OfficerResponse.builder().items(List.of(resignedOfficer)).build();

        when(companyOfficerClient.getCompanyResponse(Map.of(CompanySearch.COMPANY_SEARCH, "name")))
                .thenReturn(ResponseEntity.of(Optional.of(companyResponse)));
        when(companyOfficerClient.getOfficers(Map.of(CompanySearch.OFFICER_SEARCH, ACTIVE_NUM)))
                .thenReturn(ResponseEntity.of(Optional.of(officersResponse)));

        final var response = service.getCompanies(new CompanySearch("name", null), true);

        assertThat(response.getTotalResults()).isEqualTo(1);
        final var companies = response.getItems();
        assertThat(companies).hasSize(1);
        assertThat(companies.getFirst()).isEqualTo(activeCompany.buildCompanyOfficer(Collections.emptyList()));
        verify(companyRepository, timeout(VERIFY_ASYNC_WAIT)).findByCompanyNumber(ACTIVE_NUM);
        verify(companyRepository, timeout(VERIFY_ASYNC_WAIT)).save(any(CompanyEntity.class));
        verifyNoMoreInteractions(companyRepository);
    }

    @Test
    void givenOfficerSearch_whenSearchReturnsNothing_thenReturnEmptyList() {

        when(companyOfficerClient.getOfficers(Map.of(CompanySearch.OFFICER_SEARCH, "99")))
                .thenReturn(ResponseEntity.of(Optional.empty()));

        final var response = service.getCompanyOfficers("99");

        assertThat(response).isEmpty();
        verifyNoInteractions(companyRepository);
    }

    @Test
    void givenOfficerSearch_whenSearchWorks_thenReturnList() {

        final var officer = OfficerDetail.builder().build();
        final var officerResponse = OfficerResponse.builder().items(List.of(officer)).build();

        when(companyOfficerClient.getOfficers(Map.of(CompanySearch.OFFICER_SEARCH, "99")))
                .thenReturn(ResponseEntity.of(Optional.of(officerResponse)));

        final var response = service.getCompanyOfficers("99");

        assertThat(response).hasSize(1);
        assertThat(response.getFirst()).isSameAs(officer);
    }

}
