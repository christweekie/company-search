package org.chrisfaulkner.model.officer;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateSerializer;
import org.chrisfaulkner.model.Address;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@NoArgsConstructor(access = AccessLevel.PROTECTED, force = true)
@AllArgsConstructor
@Data
@JsonPropertyOrder({"name", "officer_role", "appointed_on", "address"})
public class Officer {

	private final String name;

	@JsonProperty("officer_role")
	private final String officerRole;

	@JsonProperty("appointed_on")
	@JsonSerialize(using = LocalDateSerializer.class)
	private final LocalDate appointedOn;

	@JsonIgnore
	private final String occupation;

	private final Address address;

}
