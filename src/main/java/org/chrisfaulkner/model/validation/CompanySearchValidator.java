package org.chrisfaulkner.model.validation;

import org.chrisfaulkner.model.web.CompanySearch;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.apache.commons.lang3.StringUtils;

public class CompanySearchValidator implements ConstraintValidator<CompanySearchParams, CompanySearch> {

    @Override
    public boolean isValid(CompanySearch companySearch, ConstraintValidatorContext constraintValidatorContext) {
        return StringUtils.isNotBlank(companySearch.getSearchTerm());
    }
}
