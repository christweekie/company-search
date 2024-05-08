package org.chrisfaulkner.model.officer;

import lombok.AccessLevel;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE, force = true)
@Data
public class PartialDate {

    private final Integer month;
    private final Integer year;
    private final Integer day;
}
