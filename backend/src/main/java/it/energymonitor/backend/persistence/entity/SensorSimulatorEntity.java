package it.energymonitor.backend.persistence.entity;

import jakarta.persistence.Entity;
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

	protected SensorSimulatorEntity() {
	}

	public SensorSimulatorEntity(String ipAddress, String macAddress) {
		this.ipAddress = ipAddress;
		this.macAddress = macAddress;
		this.functioning = true;
	}
}
