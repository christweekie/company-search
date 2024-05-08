package org.chrisfaulkner.service;

import org.chrisfaulkner.jpa.CompanyEntityFactory;
import org.chrisfaulkner.jpa.CompanyRepository;
import org.chrisfaulkner.model.Company;
import org.chrisfaulkner.model.officer.CompanyOfficer;
import org.chrisfaulkner.model.officer.CompanyOfficers;
import org.chrisfaulkner.model.officer.Officer;
import org.chrisfaulkner.model.officer.OfficerDetail;
import org.chrisfaulkner.model.web.CompanyResponse;
import org.chrisfaulkner.model.web.CompanySearch;
import org.chrisfaulkner.web.CompanyOfficerClient;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

@Service
@RequiredArgsConstructor
@Slf4j
public class CompanyService {

    private final CompanyOfficerClient webClient;

    private final CompanyRepository companyRepository;

    /**
     * Gets a list of companies based on the CompanySearch instance passed in.
     * @param companySearch
     *   Parameter to be used in search
     * @param activeOnly
     *   If true, return only companies which have status of "active"
     * @return
     *   List of matching companies, or empty list. Guaranteed non-null response.
     */
    public CompanyOfficers getCompanies(final CompanySearch companySearch, final boolean activeOnly) {

        final var companies = getCompanies(companySearch).stream()
                .filter(c -> !activeOnly || c.isActive())
                .parallel()
                .map(company -> {
                    final var officers = getCompanyOfficers(company.getCompanyNumber())
                            .stream()
                            .map(o -> new Officer(o.getName(), o.getOfficerRole(), o.getAppointedOn(), o.getOccupation(), o.getAddress()))
                            .toList();

                    // Returns the company with limited fields
                    final var companyOfficer = company.buildCompanyOfficer(officers);
                    CompletableFuture.runAsync(() -> saveCompany(companyOfficer));
                    return companyOfficer;
                })
                .toList();

        return new CompanyOfficers(companies.size(), companies);
    }

    public List<OfficerDetail> getCompanyOfficers(final String companyNumber) {
        final var searchParams = Map.of(CompanySearch.OFFICER_SEARCH, companyNumber);
        final var officerResponse = webClient.getOfficers(searchParams).getBody();
        if (officerResponse == null) {
            return Collections.emptyList();
        }

        final var officers = officerResponse.getItems().stream()
                .filter(officerDetails -> officerDetails.getResignedOn() == null)
                .toList();

        log.debug("Matched {} company number to {} non-resigned officers", companyNumber, officers.size());
        return officers;
    }

    private List<Company> getCompanies(final CompanySearch companySearch) {

        log.debug("Preparing a company search for {}", companySearch.toString());
        final var searchParams = Map.of(CompanySearch.COMPANY_SEARCH, companySearch.getSearchTerm());
        final var companyResponse = webClient.getCompanyResponse(searchParams).getBody();
        final var companies = Optional.ofNullable(companyResponse)
                .map(CompanyResponse::getItems)
                .orElse(Collections.emptyList());
        log.debug("Matched {} to {} companies", companySearch.getSearchTerm(), companies.size());
        return companies;
    }

    private void saveCompany(final CompanyOfficer company) {
        log.info("About to save company {} (number : {}), with {} officers",
                company.getTitle(), company.getCompanyNumber(), company.getOfficers().size());
        final var existing = companyRepository.findByCompanyNumber(company.getCompanyNumber());
        final var companyEntity = CompanyEntityFactory.createCompanyEntity(company, existing);
        try {
            companyRepository.save(companyEntity);
        }
        catch (final Exception e) {
            log.error("Failed to save company {}", company.getTitle(), e);
            throw new RuntimeException(e);
        }
        log.info("Saved company {}", company.getTitle());
    }
}
