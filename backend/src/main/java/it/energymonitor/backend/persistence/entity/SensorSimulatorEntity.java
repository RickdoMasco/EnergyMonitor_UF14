package it.energymonitor.backend.persistence.entity;

import it.energymonitor.backend.domain.IngestionOutcome;
import it.energymonitor.backend.domain.ValidationLimits;
import java.time.Instant;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;

@Entity
@Table(name = "sensor_simulators")
@Getter
public class SensorSimulatorEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	private String ipAddress;

	private String macAddress;

	private boolean functioning;

	// Colonne nullable: ddl-auto=update deve poterle aggiungere a tabelle che contengono già righe
	@Column(name = "last_contact_at")
	private Instant lastContactAt;

	@Enumerated(EnumType.STRING)
	@Column(name = "last_outcome")
	private IngestionOutcome lastOutcome;

	@Column(name = "last_outcome_reason", length = ValidationLimits.OUTCOME_REASON_MAX_LENGTH)
	private String lastOutcomeReason;

	protected SensorSimulatorEntity() {
	}

	public SensorSimulatorEntity(String ipAddress, String macAddress) {
		this.ipAddress = ipAddress;
		this.macAddress = macAddress;
		this.functioning = true;
	}

	public void recordContact(IngestionOutcome outcome, String reason, Instant contactAt) {
		this.lastOutcome = outcome;
		this.lastOutcomeReason = truncate(reason);
		this.lastContactAt = contactAt;
	}

	private String truncate(String reason) {
		if (reason == null || reason.length() <= ValidationLimits.OUTCOME_REASON_MAX_LENGTH) {
			return reason;
		}
		return reason.substring(0, ValidationLimits.OUTCOME_REASON_MAX_LENGTH);
	}
}
