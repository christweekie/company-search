package org.chrisfaulkner.model.officer;

import lombok.AccessLevel;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE, force = true)
@Data
public class OfficerLink {

	private AppointmentsLink officer;
}
