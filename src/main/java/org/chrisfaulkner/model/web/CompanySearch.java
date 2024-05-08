package org.chrisfaulkner.model.web;

import org.chrisfaulkner.model.validation.CompanySearchParams;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.apache.commons.lang3.StringUtils;

@CompanySearchParams
@Data
@AllArgsConstructor
public class CompanySearch {

    public static final String COMPANY_SEARCH = "Query";
    public static final String OFFICER_SEARCH = "CompanyNumber";

    private final String companyName;

    @Size(max = 12, message = "Company Number has a maximum length of 11")
    private final String companyNumber;

    public String getSearchTerm() {
        if (StringUtils.isBlank(companyNumber) && StringUtils.isBlank(companyName)) {
            throw new IllegalArgumentException("Invalid RequestBody for Company Search, either companyNumber or companyName must be set");
        }
        return StringUtils.isNotBlank(companyNumber)
                ? companyNumber
                : companyName;
    }
}
