package it.energymonitor.backend.api;

import java.time.Instant;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public final class BackendRequests {

	private BackendRequests() {
	}

	public record CreateSimulator(
			@NotBlank String ipAddress,
			@NotBlank String macAddress) {
	}

	public record CreateMeasurement(
			@NotNull Long simulatorId,
			@NotBlank String quantity,
			@NotNull Double value,
			@NotBlank String unit,
			Instant measuredAt) {
	}
}
