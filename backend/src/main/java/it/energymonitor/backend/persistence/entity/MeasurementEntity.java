package it.energymonitor.backend.persistence.entity;

import java.time.Instant;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Entity
@Table(name = "measurements")
@RequiredArgsConstructor
@Getter
public class MeasurementEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "simulator_id", nullable = false)
	private SensorSimulatorEntity simulator;

	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "quantity_id", nullable = false)
	private QuantityEntity quantity;

	@Column(name = "measurement_value", nullable = false)
	private double value;

	@Column(nullable = false, length = 32)
	private String unit;

	@Column(name = "measured_at", nullable = false)
	private Instant measuredAt;

	public MeasurementEntity(
			SensorSimulatorEntity simulator,
			QuantityEntity quantity,
			double value,
			String unit,
			Instant measuredAt) {
		this.simulator = simulator;
		this.quantity = quantity;
		this.value = value;
		this.unit = unit;
		this.measuredAt = measuredAt;
	}
}
