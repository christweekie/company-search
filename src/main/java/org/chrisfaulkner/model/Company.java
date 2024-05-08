package org.chrisfaulkner.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateSerializer;
import org.chrisfaulkner.model.officer.CompanyOfficer;
import org.chrisfaulkner.model.officer.Officer;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.With;
import org.apache.commons.lang3.StringUtils;

import java.time.LocalDate;
import java.util.List;

@NoArgsConstructor(access = AccessLevel.PACKAGE, force = true)
@AllArgsConstructor
@Data
@Builder
@With
public class Company {

	// Given access to more info about the source data, I would choose
	// to consider modelling this as an enum
	public static final String ACTIVE_STATUS = "ACTIVE";

	@JsonProperty("company_status")
	private final String companyStatus;

	@JsonProperty("address_snippet")
	private final String addressSnippet;

	@JsonProperty("date_of_creation")
	@JsonSerialize(using = LocalDateSerializer.class)
	private final LocalDate dateOfCreation;

	@JsonProperty("matches")
	private final Matches matches;

	private final String description;

	private final Link links;

	@JsonProperty("company_number")
	private final String companyNumber;

	private final String title;

	@JsonProperty("company_type")
	private final String companyType;

	private final Address address;

	private final String kind;

	@JsonProperty("description_identifier")
	private final List<String> descriptionIdentifier;

	private final List<Officer> officers;

	public CompanyOfficer buildCompanyOfficer(List<Officer> officers) {
		return CompanyOfficer.builder()
				.companyNumber(this.companyNumber)
				.companyStatus(this.companyStatus)
				.companyType(this.companyType)
				.dateOfCreation(this.dateOfCreation)
				.title(this.title)
				.address(this.address)
				.description(this.description)
				.officers(officers)
				.build();
	}

	public boolean isActive() {
		return StringUtils.isBlank(companyStatus) || ACTIVE_STATUS.equalsIgnoreCase(companyStatus);
	}

}
