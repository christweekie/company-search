package org.chrisfaulkner.jpa;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDate;
import java.time.Month;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

@ActiveProfiles("test")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
class CompanyRepositoryIT {

    private static final String COMPANY_NUMBER = "100";
    private final CompanyEntity company = CompanyEntity
            .builder()
            .companyNumber(COMPANY_NUMBER)
            .companyType("LTD")
            .companyStatus("active")
            .title("BBC LIMITED")
            .description("Description")
            .dateOfCreation(LocalDate.of(1969, Month.APRIL, 1))
            .build();
    private final Set<OfficerEntity> officerEntities = new HashSet<>(2);
    private final OfficerEntity officer1 = OfficerEntity
            .builder()
            .name("Mr SMITH")
            .officerRole("Officer Role")
            .appointedOn(LocalDate.of(2020, Month.JANUARY, 1))
            .occupation("Occupation")
            .build();
    private final OfficerEntity officer2 = OfficerEntity
            .builder()
            .name("Mrs SMITH")
            .officerRole("Officer Role2")
            .appointedOn(LocalDate.of(2005, Month.FEBRUARY, 2))
            .occupation("Occupation2")
            .build();

    @BeforeEach
    void beforeEach() {
        officerEntities.addAll(List.of(officer1, officer2));
        company.setOfficers(officerEntities);
    }

    @Autowired
    private CompanyRepository repository;

    @Test
    void givenCompanyWithOfficers_whenSaveNewCompany_thenReturnCompany() {

        // Act
        final var companySaved = repository.save(company);

        // Assert
        assertThat(companySaved.getId()).isNotNull();
        assertThat(companySaved.getDateOfCreation()).isNotNull();
        assertThat(companySaved.getLastUpdatedOn()).isNotNull();
        assertThat(companySaved.getOfficers().size()).isEqualTo(2);

        assertThat(companySaved).usingRecursiveComparison()
                .ignoringFields("id", "officers", "dateOfCreation")
                .isEqualTo(company);

        assertThat(company.getOfficers()).containsExactlyInAnyOrder(officer1, officer2);
    }

    @Test
    void givenExistingCompany_whenFindByNumber_thenReturnCompany() {

        // Arrange
        repository.save(company.withCompanyNumber("S102"));

        // Assert
        assertThat(repository.findByCompanyNumber("S102")).isNotNull();
    }

}
