package it.energymonitor.backend.api;

import it.energymonitor.backend.domain.ValidationLimits;
import java.time.Instant;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

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
			@NotNull(message = "simulatorId è obbligatorio")
			Long simulatorId,

			@NotBlank(message = "quantity è obbligatorio")
			@Size(max = ValidationLimits.QUANTITY_MAX_LENGTH,
					message = "quantity supera la lunghezza massima di {max} caratteri")
			String quantity,

			@NotNull(message = "value è obbligatorio")
			Double value,

			@NotBlank(message = "unit è obbligatorio")
			@Size(max = ValidationLimits.UNIT_MAX_LENGTH,
					message = "unit supera la lunghezza massima di {max} caratteri")
			String unit,

			Instant measuredAt) {
	}
}
