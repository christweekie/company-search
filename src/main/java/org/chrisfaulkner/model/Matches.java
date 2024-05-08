package org.chrisfaulkner.model;

import lombok.AccessLevel;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@NoArgsConstructor(access = AccessLevel.PRIVATE, force = true)
@Data
public class Matches {

	private final List<Integer> title;
}
