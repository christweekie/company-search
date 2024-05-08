package org.chrisfaulkner.jpa;

import org.springframework.data.repository.CrudRepository;

public interface CompanyRepository extends CrudRepository<CompanyEntity, Long> {
    CompanyEntity findByCompanyNumber(String companyNumber);
}
