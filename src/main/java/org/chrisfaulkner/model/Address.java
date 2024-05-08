package org.chrisfaulkner.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE, force = true)
@AllArgsConstructor
@Data
public class Address {

	private final String premises;

	@JsonProperty("postal_code")
	private final String postalCode;

	private final String country;

	private final String locality;

	@JsonProperty("address_line_1")
	private final String addressLine1;

}
