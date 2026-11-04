package it.energymonitor.backend.service;

import lombok.Getter;

@Getter
public class InvalidMeasurementException extends RuntimeException {

	private final Long simulatorId;

	public InvalidMeasurementException(Long simulatorId, String message) {
		super(message);
		this.simulatorId = simulatorId;
	}
}
