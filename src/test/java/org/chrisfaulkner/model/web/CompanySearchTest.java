package org.chrisfaulkner.model.web;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

class CompanySearchTest {


    @Test
    void givenBothSet_whenGetSearchTerm_thenPreferNumber() {
        final var companySearch = new CompanySearch("name", "011");
        assertThat(companySearch.getSearchTerm()).isEqualTo("011");
    }

    @Test
    void givenNumberSet_whenGetSearchTerm_thenNumber() {
        final var companySearch = new CompanySearch("", "011");
        assertThat(companySearch.getSearchTerm()).isEqualTo("011");
    }

    @Test
    void givenNumberSetButBlank_whenGetSearchTerm_thenUseName() {
        final var companySearch = new CompanySearch("company name", " ");
        assertThat(companySearch.getSearchTerm()).isEqualTo("company name");
    }

    @Test
    void givenNeitherSet_whenGetSearchTerm_thenIllegalArgumentException() {
        final var companySearch = new CompanySearch("", " ");
        assertThrows(IllegalArgumentException.class, companySearch::getSearchTerm);
    }
}
