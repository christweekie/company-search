package org.chrisfaulkner.web;

import org.chrisfaulkner.model.officer.OfficerResponse;
import org.chrisfaulkner.model.web.CompanyResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.service.annotation.GetExchange;

import java.util.Map;

@Service
public interface CompanyOfficerClient {

	@GetExchange("/Search")
	ResponseEntity<CompanyResponse> getCompanyResponse(@RequestParam(name = "Query") Map<String, String> searchTerms);

	@GetExchange("/Officers")
	ResponseEntity<OfficerResponse> getOfficers(@RequestParam(name = "CompanyNumber") Map<String, String> searchTerms);

}
