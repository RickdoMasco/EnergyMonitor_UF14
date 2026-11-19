package it.energymonitor.backend.service;

import it.energymonitor.backend.api.BackendRequests.CreateMeasurement;
import it.energymonitor.backend.api.BackendRequests.CreateSimulator;
import it.energymonitor.backend.api.BackendRequests.CreateUser;
import it.energymonitor.backend.domain.Measurement;
import it.energymonitor.backend.domain.SensorSimulator;
import it.energymonitor.backend.domain.User;
import it.energymonitor.backend.persistence.entity.MeasurementEntity;
import it.energymonitor.backend.persistence.entity.QuantityEntity;
import it.energymonitor.backend.persistence.entity.SensorSimulatorEntity;
import it.energymonitor.backend.persistence.entity.UserEntity;
import it.energymonitor.backend.persistence.repository.MeasurementRepository;
import it.energymonitor.backend.persistence.repository.QuantityRepository;
import it.energymonitor.backend.persistence.repository.SensorSimulatorRepository;
import it.energymonitor.backend.persistence.repository.UserRepository;

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
	private final UserRepository userRepo;

    
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

	public User createUser (CreateUser request){
		//ricavare i dati dall'oggetto JSON arrivato ed inseirlo dentro variabili Java
		String nome = request.nome();
		String cognome = request.cognome();
		String email = request.email();
		String password = request.password();
		
		//con le variabili Java appena ricavate, creare un'Entity ed inviarla al repo
		UserEntity user = userRepo.save(new UserEntity(nome, cognome, email, password));
		
		//come conferma di creazione della nuova Entity, creare il DTO partendo dalla nuova Entity e restituirlo al controller
		return toUser(user);
	}
	
		
	public Measurement createMeasurement(CreateMeasurement request) {
		requireRequest(request);
		if (request.simulatorId() == null) {
			throw new IllegalArgumentException("simulatorId è obbligatorio");
		}

		SensorSimulatorEntity simulator = simulatorRepository.findById(request.simulatorId())
				.orElseThrow(() -> new ResourceNotFoundException(
						"simulatore non trovato: " + request.simulatorId()));

		String quantityName = requiredText(request.quantity(), "quantity");
		String unit = requiredText(request.unit(), "unit");

		if (request.value() == null || !Double.isFinite(request.value())) {
			throw new IllegalArgumentException("value deve essere un numero finito");
		}

		QuantityEntity quantity = quantityRepository.findByNameIgnoreCase(quantityName)
				.orElseGet(() -> quantityRepository.save(new QuantityEntity(quantityName)));

		MeasurementEntity measurement = measurementRepository.save(new MeasurementEntity(
				simulator,
				quantity,
				request.value(),
				unit,
				request.measuredAt() == null ? Instant.now() : request.measuredAt()));
		return toMeasurement(measurement);
		
	}

	private String requiredText(String value, String name) {
		if (value == null || value.isBlank()) {
			throw new IllegalArgumentException(name + " è obbligatorio");
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
				entity.isFunctioning());
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

	private User toUser(UserEntity entity){
		return new User(
			entity.getId(),
			entity.getNome(),
			entity.getCognome(),
			entity.getEmail(),
			entity.getPasswordHash()
			);
	}
}
