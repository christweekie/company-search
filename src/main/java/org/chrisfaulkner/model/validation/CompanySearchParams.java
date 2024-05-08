package org.chrisfaulkner.model.validation;

import jakarta.validation.Constraint;

@Constraint(validatedBy = CompanySearchValidator.class)
public @interface CompanySearchParams {
}
