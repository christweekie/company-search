package org.chrisfaulkner.model.officer;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.chrisfaulkner.model.Link;
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
public class OfficerResponse {

	@JsonProperty("etag")
	private final String etag;

	@JsonProperty("links")
	private final Link links;

	@JsonProperty("kind")
	private final String kind;

	@JsonProperty("items_per_page")
	private final int itemsPerPage;

	@JsonProperty("items")
	private final List<OfficerDetail> items;
}
