package it.energymonitor.backend.logging;

import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.pattern.CompositeConverter;
import java.util.regex.Pattern;

/**
 * Maschera nei log i valori associati a chiavi sensibili (password, token, chiavi API,
 * header Authorization), ad esempio {@code password=abc} diventa {@code password=***}.
 */
public class SensitiveDataMaskingConverter extends CompositeConverter<ILoggingEvent> {

	static final String MASK = "***";

	private static final Pattern SENSITIVE_VALUE = Pattern.compile(
			"(?i)\\b(password|passwd|pwd|secret|token|access_token|api[-_]?key|authorization)"
					+ "(\"?\\s*[:=]\\s*\"?)"
					+ "(bearer\\s+|basic\\s+)?"
					+ "[^\\s\",;&]+");

	@Override
	protected String transform(ILoggingEvent event, String in) {
		return mask(in);
	}

	static String mask(String in) {
		if (in == null || in.isEmpty()) {
			return in;
		}
		return SENSITIVE_VALUE.matcher(in).replaceAll("$1$2$3" + MASK);
	}
}
