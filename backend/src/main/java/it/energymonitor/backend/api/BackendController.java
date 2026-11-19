package it.energymonitor.backend.api;

import it.energymonitor.backend.api.BackendRequests.CreateMeasurement;
import it.energymonitor.backend.api.BackendRequests.CreateSimulator;
import it.energymonitor.backend.api.BackendRequests.CreateUser;
import it.energymonitor.backend.domain.Measurement;
import it.energymonitor.backend.domain.Role;
import it.energymonitor.backend.domain.SensorSimulator;
import it.energymonitor.backend.domain.User;


import it.energymonitor.backend.service.BackendService;
import java.util.List;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class BackendController {

	private final BackendService service;

	@PostMapping("/simulators")
	@ResponseStatus(HttpStatus.CREATED)
	public SensorSimulator createSimulator(@Valid @RequestBody CreateSimulator request) {
		return service.createSimulator(request);
	}

	@GetMapping("/simulators")
	public List<SensorSimulator> getSimulators() {
		return service.getSimulators();
	}

	@PostMapping("/measurements")
	@ResponseStatus(HttpStatus.CREATED)
	public Measurement createMeasurement(@Valid @RequestBody CreateMeasurement request) {
		return service.createMeasurement(request);
	}

	@PostMapping("/user")
	@ResponseStatus(HttpStatus.CREATED)
	public User createUser (@Valid @RequestBody CreateUser request){		
		//L'oggetto request viene passato al service, il quale dopo aver fatto le sue operazioni interne, 
		//restituisce il DTO, il quale viene restituito all'utente:
		return service.createUser(request);
	} 

	@GetMapping("/roles")
	 public List<Role> getRoles() {
	 	return service.getRoles(); //restituisce il vettore con i DTO da inviare al frontend
	} 
}
