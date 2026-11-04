package it.energymonitor.backend.logging;

import ch.qos.logback.classic.spi.IThrowableProxy;
import org.springframework.boot.logging.logback.ExtendedWhitespaceThrowableProxyConverter;

/**
 * Stack trace nel formato di default di Spring Boot, con lo stesso mascheramento dei messaggi:
 * i messaggi delle eccezioni possono contenere credenziali (es. stringhe di connessione).
 */
public class SensitiveThrowableMaskingConverter extends ExtendedWhitespaceThrowableProxyConverter {

	@Override
	protected String throwableProxyToString(IThrowableProxy throwableProxy) {
		return SensitiveDataMaskingConverter.mask(super.throwableProxyToString(throwableProxy));
	}
}
