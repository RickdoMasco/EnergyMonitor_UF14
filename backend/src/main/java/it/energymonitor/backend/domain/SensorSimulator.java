package it.energymonitor.backend.domain;

public record SensorSimulator(Long id, String ipAddress, String macAddress, boolean functioning) {
}
