package org.chrisfaulkner.model.web;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.chrisfaulkner.model.Company;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@NoArgsConstructor(access = AccessLevel.PRIVATE, force = true)
@AllArgsConstructor
@Builder
@Data
public class CompanyResponse {

	@JsonProperty("page_number")
	private final int pageNumber;

	private final String kind;

	@JsonProperty("total_results")
	private final int totalResults;

	private final List<Company> items;
}
