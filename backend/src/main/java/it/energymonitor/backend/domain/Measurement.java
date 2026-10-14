package it.energymonitor.backend.domain;

import java.time.Instant;

public record Measurement(
		Long id,
		Long simulatorId,
		String quantity,
		double value,
		String unit,
		Instant measuredAt) {
}
