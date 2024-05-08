package org.chrisfaulkner.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.chrisfaulkner.model.Company;
import org.chrisfaulkner.model.officer.CompanyOfficers;
import org.chrisfaulkner.model.officer.Officer;
import org.chrisfaulkner.model.officer.OfficerDetail;
import org.chrisfaulkner.model.web.CompanySearch;
import org.chrisfaulkner.service.CompanyService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collections;
import java.util.List;

import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ActiveProfiles("test")
@SpringBootTest
@AutoConfigureMockMvc
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_CLASS)
class CompanyControllerIT {

    private Officer officer;
    private Company company;
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private CompanyService companyService;

    @BeforeEach
    void beforeAll() throws IOException {
        final var active = Files.readString(Path.of("src/test/resources/__files/expectations/officer.json"));
        final var companyJson = Files.readString(Path.of("src/test/resources/__files/expectations/bbc-company.json"));
        final var detail = objectMapper.readValue(active, OfficerDetail.class);
        officer = new Officer(detail.getName(), detail.getOfficerRole(), detail.getAppointedOn(), detail.getOccupation(), detail.getAddress());
        company = objectMapper.readValue(companyJson, Company.class);
    }

    @Test
    void getCompanyOfficersShouldReturnCompanyOfficers() throws Exception {
        CompanyOfficers officers = new CompanyOfficers(1, List.of(company.buildCompanyOfficer(List.of(officer))));
        CompanySearch companySearch = new CompanySearch("Acme Services", "100");

        when(companyService.getCompanies(companySearch, Boolean.TRUE)).thenReturn(officers);

        mockMvc.perform(post("/companyofficers?active=true")
                .content(objectMapper.writeValueAsString(companySearch))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.total_results").value(1))
                .andExpect(jsonPath("$.items[0].company_number", is(company.getCompanyNumber())))
                .andExpect(jsonPath("$.items[0].company_type", is(company.getCompanyType())))
                .andExpect(jsonPath("$.items[0].address.locality", is(company.getAddress().getLocality())))
                .andExpect(jsonPath("$.items[0].addressSnippet").doesNotExist())
                ;

        verify(companyService).getCompanies(companySearch, true);
        verifyNoMoreInteractions(companyService);
    }

    @Test
    void getCompanyOfficers_whenPassNoRequestParameter_thenDefaultsToActiveTrue() throws Exception {
        CompanyOfficers officers = new CompanyOfficers(0, Collections.emptyList());
        CompanySearch companySearch = new CompanySearch("Acme Services", "100");

        when(companyService.getCompanies(companySearch, Boolean.TRUE)).thenReturn(officers);

        mockMvc.perform(post("/companyofficers")
                        .content(objectMapper.writeValueAsString(companySearch))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.total_results").value(0));

        verify(companyService).getCompanies(companySearch, true);
        verifyNoMoreInteractions(companyService);
    }

    @Test
    void getCompanyOfficers_whenPassNoRequestBody_thenBadRequest() throws Exception {
        mockMvc.perform(post("/companyofficers")
                        .content("{ something that is not JSON }")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    void getCompanyOfficers_whenCompanyNumberTooLong_thenBadRequest() throws Exception {
        CompanySearch companySearch = new CompanySearch(null, "101868372163821360");

        mockMvc.perform(post("/companyofficers")
                        .content(objectMapper.writeValueAsString(companySearch))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    void getCompanyOfficersShouldHandleBadRequest() throws Exception {
        mockMvc.perform(post("/companyofficers")
                .content("")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }
}
