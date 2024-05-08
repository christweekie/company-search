package org.chrisfaulkner.jpa;

import org.chrisfaulkner.model.officer.CompanyOfficer;
import org.chrisfaulkner.model.officer.Officer;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class CompanyEntityFactory {

    public static CompanyEntity createCompanyEntity(CompanyOfficer companyOfficer, CompanyEntity existingCompanyEntity) {

        final var existingCompanyId = Optional.ofNullable(existingCompanyEntity).map(CompanyEntity::getId).orElse(null);

        final var companyEntity = CompanyEntity.builder()
                .companyNumber(companyOfficer.getCompanyNumber())
                .description(companyOfficer.getDescription())
                .companyType(companyOfficer.getCompanyType())
                .title(companyOfficer.getTitle())
                .companyStatus(companyOfficer.getCompanyStatus())
                .dateOfCreation(companyOfficer.getDateOfCreation())
                .id(existingCompanyId)
                .build();
        final var officers = createOfficers(companyOfficer.getOfficers(), existingCompanyEntity);
        companyEntity.setOfficers(officers);
        return companyEntity;
    }

    static Set<OfficerEntity> createOfficers(List<Officer> officers, CompanyEntity existingCompanyEntity) {
        return Optional.ofNullable(officers)
                .orElse(Collections.emptyList())
                .stream()
                .map(e -> createOfficerEntity(e, existingCompanyEntity))
                .collect(Collectors.toSet());
    }

    static OfficerEntity createOfficerEntity(Officer officer, CompanyEntity existingCompanyEntity) {

        final var officerEntity = OfficerEntity.builder()
                .officerRole(officer.getOfficerRole())
                .occupation(officer.getOccupation())
                .appointedOn(officer.getAppointedOn())
                .name(officer.getName())
                .build();

        if (existingCompanyEntity != null) {

            final Long id = existingCompanyEntity.getOfficers()
                    .stream()
                    .filter(officerEntity1 -> officerEntity1.equals(officerEntity))
                    .findFirst()
                    .map(OfficerEntity::getId)
                    .orElse(null);
            officerEntity.setId(id);
        }

        return officerEntity;
    }
}
