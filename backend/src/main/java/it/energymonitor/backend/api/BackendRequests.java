package it.energymonitor.backend.api;

import java.time.Instant;

public final class BackendRequests {

	private BackendRequests() {
	}

	public record CreateSimulator(String ipAddress, String macAddress) {
	}

	public record CreateMeasurement(
			Long simulatorId,
			String quantity,
			Double value,
			String unit,
			Instant measuredAt) {
	}
}
