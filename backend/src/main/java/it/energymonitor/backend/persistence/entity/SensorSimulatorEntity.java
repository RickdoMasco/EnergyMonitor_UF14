package it.energymonitor.backend.persistence.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.Getter;

@Entity
@Table(
		name = "sensor_simulators",
		uniqueConstraints = {
				@UniqueConstraint(name = "uk_sensor_simulators_ip_address", columnNames = "ip_address"),
				@UniqueConstraint(name = "uk_sensor_simulators_mac_address", columnNames = "mac_address")
		})
@Getter
public class SensorSimulatorEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(name = "ip_address", nullable = false, unique = true)
	private String ipAddress;

	@Column(name = "mac_address", nullable = false, unique = true)
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
