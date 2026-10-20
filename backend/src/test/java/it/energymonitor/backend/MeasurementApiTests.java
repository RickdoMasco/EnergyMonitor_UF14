package it.energymonitor.backend;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.jayway.jsonpath.JsonPath;
import it.energymonitor.backend.api.ApiExceptionHandler;
import it.energymonitor.backend.api.BackendController;
import it.energymonitor.backend.persistence.repository.MeasurementRepository;
import it.energymonitor.backend.persistence.repository.QuantityRepository;
import it.energymonitor.backend.persistence.repository.SensorSimulatorRepository;
import it.energymonitor.backend.service.BackendService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

@SpringBootTest
class MeasurementApiTests {

	@DynamicPropertySource
	static void configureDatasource(DynamicPropertyRegistry registry) {
		registry.add("spring.datasource.url",
				() -> "jdbc:h2:mem:energymonitor_test;DB_CLOSE_DELAY=-1");
		registry.add("spring.datasource.username", () -> "sa");
		registry.add("spring.datasource.password", () -> "");
	}

	@Autowired
	private BackendService service;

	@Autowired
	private MeasurementRepository measurementRepository;

	@Autowired
	private QuantityRepository quantityRepository;

	@Autowired
	private SensorSimulatorRepository simulatorRepository;

	private MockMvc mvc;

	@BeforeEach
	void setUp() {
		clearDatabase();
		mvc = MockMvcBuilders.standaloneSetup(new BackendController(service))
				.setControllerAdvice(new ApiExceptionHandler())
				.build();
	}

	@AfterEach
	void cleanDatabase() {
		clearDatabase();
	}

	@Test
	void registersMultipleSimulatorsAndAcceptsMeasurementsFromEach() throws Exception {
		long firstSimulatorId = createSimulator("{\"ipAddress\":\"192.168.1.10\"}");
		long secondSimulatorId = createSimulator("{\"macAddress\":\"AA:BB:CC:DD:EE:FF\"}");

		mvc.perform(post("/api/measurements")
						.contentType(MediaType.APPLICATION_JSON)
						.content(measurementJson(firstSimulatorId, "temperature", 21.5, "C")))
				.andExpect(status().isCreated())
				.andExpect(jsonPath("$.simulatorId").value(firstSimulatorId))
				.andExpect(jsonPath("$.quantity").value("temperature"));
		mvc.perform(post("/api/measurements")
						.contentType(MediaType.APPLICATION_JSON)
						.content(measurementJson(secondSimulatorId, "temperature", 22.0, "C")))
				.andExpect(status().isCreated())
				.andExpect(jsonPath("$.simulatorId").value(secondSimulatorId));

		mvc.perform(get("/api/simulators"))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.length()").value(2));
		org.junit.jupiter.api.Assertions.assertEquals(2, measurementRepository.count());
		org.junit.jupiter.api.Assertions.assertEquals(1, quantityRepository.count());
	}

	@Test
	void rejectsInvalidMeasurementsAndUnknownSimulators() throws Exception {
		mvc.perform(post("/api/simulators")
						.contentType(MediaType.APPLICATION_JSON)
						.content("{invalid json"))
				.andExpect(status().isBadRequest())
				.andExpect(jsonPath("$.errore").exists());

		mvc.perform(post("/api/simulators")
						.contentType(MediaType.TEXT_PLAIN)
						.content("not json"))
				.andExpect(status().isUnsupportedMediaType())
				.andExpect(jsonPath("$.errore").exists());

		mvc.perform(post("/api/measurements")
						.contentType(MediaType.APPLICATION_JSON)
						.content("{\"simulatorId\":9223372036854775807,"
								+ "\"quantity\":\"temperature\",\"value\":20,\"unit\":\"C\"}"))
				.andExpect(status().isNotFound())
				.andExpect(jsonPath("$.errore").exists());

		long simulatorId = createSimulator("{}");
		mvc.perform(post("/api/measurements")
						.contentType(MediaType.APPLICATION_JSON)
						.content(measurementJson(simulatorId, " ", 20, "C")))
				.andExpect(status().isBadRequest())
				.andExpect(jsonPath("$.errore").exists());

		mvc.perform(post("/api/measurements")
						.contentType(MediaType.APPLICATION_JSON)
						.content("{invalid json"))
				.andExpect(status().isBadRequest())
				.andExpect(jsonPath("$.errore").exists());
	}

	private long createSimulator(String json) throws Exception {
		MvcResult response = mvc.perform(post("/api/simulators")
						.contentType(MediaType.APPLICATION_JSON)
						.content(json))
				.andExpect(status().isCreated())
				.andReturn();
		return ((Number) JsonPath.read(response.getResponse().getContentAsString(), "$.id")).longValue();
	}

	private String measurementJson(long simulatorId, String quantity, double value, String unit) {
		return "{\"simulatorId\":" + simulatorId
				+ ",\"quantity\":\"" + quantity
				+ "\",\"value\":" + value
				+ ",\"unit\":\"" + unit + "\"}";
	}

	private void clearDatabase() {
		measurementRepository.deleteAll();
		quantityRepository.deleteAll();
		simulatorRepository.deleteAll();
	}
}
