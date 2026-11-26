package it.energymonitor.backend.domain;

import java.time.Instant;

public record SensorSimulator(
		Long id,
		String ipAddress,
		String macAddress,
		boolean functioning,
		Instant lastContactAt,
		IngestionOutcome lastOutcome,
		String lastOutcomeReason) {
}
