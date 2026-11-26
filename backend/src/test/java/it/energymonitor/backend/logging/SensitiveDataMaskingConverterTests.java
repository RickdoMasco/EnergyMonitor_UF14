package it.energymonitor.backend.logging;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

class SensitiveDataMaskingConverterTests {

	private static final String MASK = SensitiveDataMaskingConverter.MASK;

	@Test
	void masksSensitiveKeyValuePairs() {
		assertEquals("password=" + MASK + " utente=mario",
				SensitiveDataMaskingConverter.mask("password=segreta utente=mario"));
		assertEquals("token: " + MASK,
				SensitiveDataMaskingConverter.mask("token: abc123"));
		assertEquals("\"apiKey\":\"" + MASK + "\"",
				SensitiveDataMaskingConverter.mask("\"apiKey\":\"chiave\""));
		assertEquals("Authorization: Bearer " + MASK,
				SensitiveDataMaskingConverter.mask("Authorization: Bearer eyJhbGciOi"));
	}

	@Test
	void leavesOrdinaryMessagesUnchanged() {
		String message = "esito=REJECTED status=400 simulatorId=1 motivo=\"quantity è obbligatorio\"";
		assertEquals(message, SensitiveDataMaskingConverter.mask(message));
	}
}
