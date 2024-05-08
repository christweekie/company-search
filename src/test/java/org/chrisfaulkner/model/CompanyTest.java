package org.chrisfaulkner.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.assertj.core.api.Assertions.assertThat;

class CompanyTest {

    private Company company;

    @BeforeEach void setUp() {
        company = Company.builder().build();
    }

    @Test
    void givenNullOrBlank_whenIsActive_thenTrue() {
        company = Company.builder().companyStatus("  ").build();
        assertThat(company.isActive()).isTrue();
        company = Company.builder().build();
        assertThat(company.isActive()).isTrue();
    }

    @ParameterizedTest
    @ValueSource(strings = {"active", "ACTIVE"})
    void givenActiveStatus_whenIsActive_thenTrue(final String status) {
        company = Company.builder().companyStatus(status).build();
        assertThat(company.isActive()).isTrue();
    }

    @ParameterizedTest
    @ValueSource(strings = {"inactive", "ACIVE", "some other string"})
    void givenOtherPossibleStatus_whenIsActive_thenFalse(final String status) {
        company = Company.builder().companyStatus(status).build();
        assertThat(company.isActive()).isFalse();
    }

}
