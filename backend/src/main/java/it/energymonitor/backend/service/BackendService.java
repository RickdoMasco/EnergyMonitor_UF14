package it.energymonitor.backend.service;

import it.energymonitor.backend.api.BackendRequests.CreateMeasurement;
import it.energymonitor.backend.api.BackendRequests.CreateSimulator;
import it.energymonitor.backend.domain.IngestionOutcome;
import it.energymonitor.backend.domain.Measurement;
import it.energymonitor.backend.domain.SensorSimulator;
import it.energymonitor.backend.persistence.entity.MeasurementEntity;
import it.energymonitor.backend.persistence.entity.QuantityEntity;
import it.energymonitor.backend.persistence.entity.SensorSimulatorEntity;
import it.energymonitor.backend.persistence.repository.MeasurementRepository;
import it.energymonitor.backend.persistence.repository.QuantityRepository;
import it.energymonitor.backend.persistence.repository.SensorSimulatorRepository;
import lombok.RequiredArgsConstructor;
import java.time.Instant;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class BackendService {

	private final SensorSimulatorRepository simulatorRepository;
	private final QuantityRepository quantityRepository;
	private final MeasurementRepository measurementRepository;

	public SensorSimulator createSimulator(CreateSimulator request) {
		requireRequest(request);
		String ipAddress = normalizeOptional(request.ipAddress());
		String macAddress = normalizeOptional(request.macAddress());
		SensorSimulatorEntity simulator = simulatorRepository.save(
				new SensorSimulatorEntity(ipAddress, macAddress));
		return toSimulator(simulator);
	}

	@Transactional(readOnly = true)
	public List<SensorSimulator> getSimulators() {
		return simulatorRepository.findAllByOrderByIdAsc().stream()
				.map(this::toSimulator)
				.toList();
	}

	public Measurement createMeasurement(CreateMeasurement request) {
		requireRequest(request);
		if (request.simulatorId() == null) {
			throw new IllegalArgumentException("simulatorId è obbligatorio");
		}

		Long simulatorId = request.simulatorId();
		SensorSimulatorEntity simulator = simulatorRepository.findById(simulatorId)
				.orElseThrow(() -> new UnknownDeviceException(simulatorId));

		String quantityName = requiredText(request.quantity(), "quantity", simulatorId);
		String unit = requiredText(request.unit(), "unit", simulatorId);

		if (request.value() == null || !Double.isFinite(request.value())) {
			throw new InvalidMeasurementException(simulatorId, "value deve essere un numero finito");
		}

		QuantityEntity quantity = quantityRepository.findByNameIgnoreCase(quantityName)
				.orElseGet(() -> quantityRepository.save(new QuantityEntity(quantityName)));

		Instant receivedAt = Instant.now();
		MeasurementEntity measurement = measurementRepository.save(new MeasurementEntity(
				simulator,
				quantity,
				request.value(),
				unit,
				request.measuredAt() == null ? receivedAt : request.measuredAt()));
		simulator.recordContact(IngestionOutcome.ACCEPTED, null, receivedAt);
		return toMeasurement(measurement);
	}

	/**
	 * Registra il rifiuto sulla sorgente, se censita. Gira in una transazione propria perché
	 * la transazione dell'acquisizione rifiutata è già stata annullata.
	 */
	public void recordRejectedMeasurement(Long simulatorId, String reason) {
		if (simulatorId == null) {
			return;
		}
		simulatorRepository.findById(simulatorId)
				.ifPresent(simulator -> simulator.recordContact(
						IngestionOutcome.REJECTED, reason, Instant.now()));
	}

	private String requiredText(String value, String name, Long simulatorId) {
		if (value == null || value.isBlank()) {
			throw new InvalidMeasurementException(simulatorId, name + " è obbligatorio");
		}
		return value.trim();
	}

	private String normalizeOptional(String value) {
		if (value == null) {
			return null;
		}
		String normalized = value.trim();
		return normalized.isEmpty() ? null : normalized;
	}

	private void requireRequest(Object request) {
		if (request == null) {
			throw new IllegalArgumentException("corpo della richiesta obbligatorio");
		}
	}

	private SensorSimulator toSimulator(SensorSimulatorEntity entity) {
		return new SensorSimulator(
				entity.getId(),
				entity.getIpAddress(),
				entity.getMacAddress(),
				entity.isFunctioning(),
				entity.getLastContactAt(),
				entity.getLastOutcome(),
				entity.getLastOutcomeReason());
	}

	private Measurement toMeasurement(MeasurementEntity entity) {
		return new Measurement(
				entity.getId(),
				entity.getSimulator().getId(),
				entity.getQuantity().getName(),
				entity.getValue(),
				entity.getUnit(),
				entity.getMeasuredAt());
	}
}
