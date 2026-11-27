package it.energymonitor.backend.service;

public class UnknownDeviceException extends RuntimeException {

	public UnknownDeviceException(Long simulatorId) {
		super("simulatore non trovato: " + simulatorId);
	}
}
