package org.chrisfaulkner.model.officer;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateSerializer;
import org.chrisfaulkner.model.Address;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor(access = AccessLevel.PRIVATE, force = true)
@AllArgsConstructor
@Builder
@Data
public class OfficerDetail extends Officer {

	private final String name;

	@JsonProperty("officer_role")
	private final String officerRole;

	@JsonProperty("appointed_on")
	@JsonSerialize(using = LocalDateSerializer.class)
	private final LocalDate appointedOn;

	private final Address address;

	@JsonProperty("resigned_on")
	@JsonSerialize(using = LocalDateSerializer.class)
	private final LocalDate resignedOn;

	@JsonProperty("links")
	private final OfficerLink links;

	@JsonProperty("date_of_birth")
	private final PartialDate dateOfBirth;

	private final String occupation;

	@JsonProperty("country_of_residence")
	private final String countryOfResidence;

	private final String nationality;

}
