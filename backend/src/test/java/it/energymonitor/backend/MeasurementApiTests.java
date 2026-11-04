package it.energymonitor.backend;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.jayway.jsonpath.JsonPath;
import it.energymonitor.backend.api.ApiExceptionHandler;
import it.energymonitor.backend.api.BackendController;
import it.energymonitor.backend.api.RequestOutcomeLogger;
import it.energymonitor.backend.domain.IngestionOutcome;
import it.energymonitor.backend.domain.ValidationLimits;
import it.energymonitor.backend.persistence.repository.MeasurementRepository;
import it.energymonitor.backend.persistence.repository.QuantityRepository;
import it.energymonitor.backend.persistence.repository.SensorSimulatorRepository;
import it.energymonitor.backend.service.BackendService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.system.CapturedOutput;
import org.springframework.boot.test.system.OutputCaptureExtension;
import org.springframework.http.MediaType;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

@SpringBootTest
@ExtendWith(OutputCaptureExtension.class)
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

	@Autowired
	private RequestOutcomeLogger outcomeLogger;

	private MockMvc mvc;

	@BeforeEach
	void setUp() {
		clearDatabase();
		mvc = MockMvcBuilders.standaloneSetup(new BackendController(service, outcomeLogger))
				.setControllerAdvice(new ApiExceptionHandler(service, outcomeLogger))
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
				.andExpect(status().isUnprocessableContent())
				.andExpect(jsonPath("$.errore").value("simulatore non trovato: 9223372036854775807"));

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

	@Test
	void rejectsTooLongFieldsWithBadRequestAndNamesTheFields() throws Exception {
		long simulatorId = createSimulator("{}");
		String tooLongQuantity = "q".repeat(ValidationLimits.QUANTITY_MAX_LENGTH + 1);
		String tooLongUnit = "u".repeat(ValidationLimits.UNIT_MAX_LENGTH + 1);

		mvc.perform(post("/api/measurements")
						.contentType(MediaType.APPLICATION_JSON)
						.content(measurementJson(simulatorId, tooLongQuantity, 20, tooLongUnit)))
				.andExpect(status().isBadRequest())
				.andExpect(jsonPath("$.errore").value(
						"quantity supera la lunghezza massima di " + ValidationLimits.QUANTITY_MAX_LENGTH
								+ " caratteri; unit supera la lunghezza massima di "
								+ ValidationLimits.UNIT_MAX_LENGTH + " caratteri"));

		mvc.perform(post("/api/measurements")
						.contentType(MediaType.APPLICATION_JSON)
						.content("{\"simulatorId\":" + simulatorId + ",\"quantity\":\"temperature\",\"unit\":\"C\"}"))
				.andExpect(status().isBadRequest())
				.andExpect(jsonPath("$.errore").value("value è obbligatorio"));

		assertEquals(0, measurementRepository.count());
	}

	@Test
	void tracksLastOutcomeOfEachSourceSeparately() throws Exception {
		long acceptedSourceId = createSimulator("{}");
		long rejectedSourceId = createSimulator("{}");

		mvc.perform(get("/api/simulators"))
				.andExpect(jsonPath("$[0].lastOutcome").doesNotExist())
				.andExpect(jsonPath("$[0].lastContactAt").doesNotExist());

		mvc.perform(post("/api/measurements")
						.contentType(MediaType.APPLICATION_JSON)
						.content(measurementJson(acceptedSourceId, "temperature", 21.5, "C")))
				.andExpect(status().isCreated());
		mvc.perform(post("/api/measurements")
						.contentType(MediaType.APPLICATION_JSON)
						.content(measurementJson(rejectedSourceId, " ", 21.5, "C")))
				.andExpect(status().isBadRequest());

		mvc.perform(get("/api/simulators"))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$[0].id").value(acceptedSourceId))
				.andExpect(jsonPath("$[0].lastOutcome").value(IngestionOutcome.ACCEPTED.name()))
				.andExpect(jsonPath("$[0].lastOutcomeReason").doesNotExist())
				.andExpect(jsonPath("$[0].lastContactAt").exists())
				.andExpect(jsonPath("$[1].id").value(rejectedSourceId))
				.andExpect(jsonPath("$[1].lastOutcome").value(IngestionOutcome.REJECTED.name()))
				.andExpect(jsonPath("$[1].lastOutcomeReason").value("quantity è obbligatorio"))
				.andExpect(jsonPath("$[1].lastContactAt").exists());
	}

	@Test
	void logsOutcomeAndSourceWithoutRequestBody(CapturedOutput output) throws Exception {
		long simulatorId = createSimulator("{}");
		String secretInBody = "password=supersegreta";

		mvc.perform(post("/api/measurements")
						.contentType(MediaType.APPLICATION_JSON)
						.content(measurementJson(simulatorId, "temperature", 21.5, "C")))
				.andExpect(status().isCreated());
		mvc.perform(post("/api/measurements")
						.contentType(MediaType.APPLICATION_JSON)
						.content(measurementJson(simulatorId, secretInBody, 21.5, "u".repeat(
								ValidationLimits.UNIT_MAX_LENGTH + 1))))
				.andExpect(status().isBadRequest());

		assertTrue(output.getOut().contains(
				"esito=ACCEPTED status=201 metodo=POST percorso=/api/measurements simulatorId=" + simulatorId));
		assertTrue(output.getOut().contains(
				"esito=REJECTED status=400 metodo=POST percorso=/api/measurements simulatorId=" + simulatorId));
		assertFalse(output.getOut().contains("supersegreta"));
	}

	@Test
	void masksSecretsInLoggedMessagesAndStackTraces(CapturedOutput output) {
		LoggerFactory.getLogger(MeasurementApiTests.class).error(
				"connessione fallita password=nelmessaggio",
				new IllegalStateException("token=nellostacktrace"));

		assertFalse(output.getOut().contains("nelmessaggio"));
		assertFalse(output.getOut().contains("nellostacktrace"));
		assertTrue(output.getOut().contains("password=***"));
		assertEquals(1, output.getOut().split("IllegalStateException", -1).length - 1);
		assertFalse(output.getOut().contains("%n"));
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
