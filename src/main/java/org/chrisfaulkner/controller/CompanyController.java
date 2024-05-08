package org.chrisfaulkner.controller;

import org.chrisfaulkner.model.officer.CompanyOfficers;
import org.chrisfaulkner.model.web.CompanySearch;
import org.chrisfaulkner.service.CompanyService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@AllArgsConstructor
@RestController
public class CompanyController {

    @Autowired
    private CompanyService companyService;

    @PostMapping(path = "/companyofficers", produces = MediaType.APPLICATION_JSON_VALUE)
    public CompanyOfficers getCompanyOfficers(@Valid @RequestBody CompanySearch companySearch,
                                              @RequestParam(defaultValue = "true") boolean activeOnly) {
        log.debug("Initiate search for companies to match {}", companySearch.toString());
        return companyService.getCompanies(companySearch, activeOnly);
    }

}
