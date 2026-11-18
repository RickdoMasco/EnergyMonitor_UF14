package it.energymonitor.backend.api;

import java.time.Instant;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

public final class BackendRequests {

	public static final String IP_ADDRESS_PATTERN =
			"^(25[0-5]|2[0-4]\\d|1\\d{2}|[1-9]?\\d)(\\.(25[0-5]|2[0-4]\\d|1\\d{2}|[1-9]?\\d)){3}$";
	public static final String MAC_ADDRESS_PATTERN =
			"^([0-9A-Fa-f]{2}([:-])){5}[0-9A-Fa-f]{2}$";

	private BackendRequests() {
	}

	public record CreateSimulator(
			@NotBlank(message = "ipAddress è obbligatorio")
			@Pattern(
					regexp = IP_ADDRESS_PATTERN,
					message = "ipAddress non valido: usare il formato IPv4 (es. 192.168.1.10)")
			String ipAddress,
			@NotBlank(message = "macAddress è obbligatorio")
			@Pattern(
					regexp = MAC_ADDRESS_PATTERN,
					message = "macAddress non valido: usare il formato MAC (es. AA:BB:CC:DD:EE:FF)")
			String macAddress) {
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
