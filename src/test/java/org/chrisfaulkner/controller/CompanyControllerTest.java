package org.chrisfaulkner.controller;

import org.chrisfaulkner.model.officer.CompanyOfficers;
import org.chrisfaulkner.model.web.CompanySearch;
import org.chrisfaulkner.service.CompanyService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CompanyControllerTest {

    @Mock
    private CompanyService service;

    @InjectMocks
    private CompanyController controller;

    @Test
    void testGetCompanyOfficers() {
        CompanySearch companySearch = new CompanySearch("", "11122233");
        final var companyOfficers = new CompanyOfficers(0, Collections.emptyList());
        when(service.getCompanies(companySearch, true)).thenReturn(companyOfficers);

        CompanyOfficers actualCompanyOfficers = controller.getCompanyOfficers(companySearch, true);

        assertThat(actualCompanyOfficers).isSameAs(companyOfficers);
        verify(service).getCompanies(companySearch, true);
        verifyNoMoreInteractions(service);
    }


}
