package it.energymonitor.backend.api;

import java.time.Instant;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public final class BackendRequests {

	private BackendRequests() {
	}

	public record CreateSimulator(String ipAddress, String macAddress) {
		public CreateSimulator {
			if (ipAddress != null) {
				ipAddress = ipAddress.trim();
			}
			if (macAddress != null) {
				macAddress = macAddress.trim();
			}
		}
	}

	public record CreateMeasurement(
			@NotNull Long simulatorId,
			@NotBlank String quantity,
			@NotNull Double value,
			@NotBlank String unit,
			Instant measuredAt) {
	}
}
