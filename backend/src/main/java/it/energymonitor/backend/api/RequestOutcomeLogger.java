package it.energymonitor.backend.api;

import it.energymonitor.backend.domain.IngestionOutcome;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Component;

/**
 * Scrive l'esito delle richieste indicando la sorgente. Non registra mai corpo, header o query
 * string, che potrebbero contenere token o credenziali.
 */
@Slf4j
@Component
public class RequestOutcomeLogger {

	public void accepted(HttpServletRequest request, HttpStatusCode status, Long simulatorId) {
		log.info("esito={} status={} metodo={} percorso={} simulatorId={} ip={}",
				IngestionOutcome.ACCEPTED,
				status.value(),
				request.getMethod(),
				request.getRequestURI(),
				simulatorId,
				request.getRemoteAddr());
	}

	public void rejected(HttpServletRequest request, HttpStatusCode status, Long simulatorId, String reason) {
		log.warn("esito={} status={} metodo={} percorso={} simulatorId={} ip={} motivo=\"{}\"",
				IngestionOutcome.REJECTED,
				status.value(),
				request.getMethod(),
				request.getRequestURI(),
				simulatorId,
				request.getRemoteAddr(),
				reason);
	}
}
