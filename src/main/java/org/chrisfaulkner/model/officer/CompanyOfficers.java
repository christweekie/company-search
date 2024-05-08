package org.chrisfaulkner.model.officer;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@NoArgsConstructor(access = AccessLevel.PRIVATE, force = true)
@AllArgsConstructor
@Data
public class CompanyOfficers {
    @JsonProperty("total_results")
    private int totalResults;
    @JsonProperty("items")
    private List<CompanyOfficer> items;

}
