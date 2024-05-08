package org.chrisfaulkner.jpa;

import org.chrisfaulkner.model.officer.CompanyOfficer;
import org.chrisfaulkner.model.officer.Officer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.Month;
import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

class CompanyEntityFactoryTest {
    private static final String COMPANY_NUMBER = "100";
    private static final LocalDate COMPANY_CREATED = LocalDate.of(1969, Month.APRIL, 1);

    private static final LocalDate APPT_DATE_1 = LocalDate.of(2020, Month.FEBRUARY, 2);
    private final CompanyOfficer company = CompanyOfficer
            .builder()
            .companyNumber(COMPANY_NUMBER)
            .companyType("LTD")
            .companyStatus("active")
            .title("BBC LIMITED")
            .description("Description")
            .dateOfCreation(COMPANY_CREATED)
            .build();

    private final Officer officer1 = new Officer("Mr SMITH", "Officer Role1",
            APPT_DATE_1, "Occupation1", null);
    private final Officer officer2 = new Officer("Mrs SMITH", "Officer Role2",
            LocalDate.of(2005, Month.FEBRUARY, 2), "Occupation2", null);


    @BeforeEach
    void beforeEach() {
        company.setOfficers(List.of(officer1, officer2));
    }

    @Test
    void givenCompanyOfficer_whenCreate_thenMakeNew() {

        final var companyEntity = CompanyEntityFactory.createCompanyEntity(company, null);

        assertThat(companyEntity.getCompanyNumber()).isEqualTo(COMPANY_NUMBER);
        assertThat(companyEntity.getCompanyType()).isEqualTo("LTD");
        assertThat(companyEntity.getId()).isNull();
        assertThat(companyEntity.getCompanyStatus()).isEqualTo("active");
        assertThat(companyEntity.getTitle()).isEqualTo("BBC LIMITED");
        assertThat(companyEntity.getDescription()).isEqualTo("Description");
        assertThat(companyEntity.getDateOfCreation()).isEqualTo(COMPANY_CREATED);

        assertThat(companyEntity.getOfficers()).hasSize(2);
    }

    @Test
    void givenOfficer_whenCreate_thenMakeNew() {

        final var officerEntity = CompanyEntityFactory.createOfficerEntity(officer1, null);

        assertThat(officerEntity.getId()).isNull();
        assertThat(officerEntity.getName()).isEqualTo("Mr SMITH");
        assertThat(officerEntity.getOfficerRole()).isEqualTo("Officer Role1");
        assertThat(officerEntity.getOccupation()).isEqualTo("Occupation1");
        assertThat(officerEntity.getAppointedOn()).isEqualTo(APPT_DATE_1);
    }

    @Test
    void givenOfficer_whenCreateWithExistingCompany_thenMakeNew() {

        final var existingOfficerEntity = OfficerEntity.builder().id(101L)
                .name("Mr SMITH")
                .officerRole("role")
                .appointedOn(APPT_DATE_1)
                .occupation("Jobs")
                .build();
        final var existingCompanyEntity = CompanyEntity.builder()
                .id(100L)
                .companyNumber(COMPANY_NUMBER)
                .officers(Set.of(existingOfficerEntity))
                .build();

        final var officerEntity = CompanyEntityFactory.createOfficerEntity(officer1, existingCompanyEntity);

        assertThat(officerEntity.getId()).isEqualTo(101L);
        assertThat(officerEntity.getName()).isEqualTo("Mr SMITH");
        assertThat(officerEntity.getOfficerRole()).isEqualTo("Officer Role1");
        assertThat(officerEntity.getOccupation()).isEqualTo("Occupation1");
        assertThat(officerEntity.getAppointedOn()).isEqualTo(APPT_DATE_1);
    }
}
