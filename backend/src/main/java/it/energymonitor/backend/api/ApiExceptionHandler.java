package it.energymonitor.backend.api;

import it.energymonitor.backend.api.BackendRequests.CreateMeasurement;
import it.energymonitor.backend.service.BackendService;
import it.energymonitor.backend.service.InvalidMeasurementException;
import it.energymonitor.backend.service.ResourceNotFoundException;
import it.energymonitor.backend.service.UnknownDeviceException;
import jakarta.servlet.http.HttpServletRequest;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataAccessException;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.ErrorResponse;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

@Slf4j
@RestControllerAdvice
@RequiredArgsConstructor
public class ApiExceptionHandler {

	private static final String INVALID_REQUEST_MESSAGE = "richiesta non valida o incompleta";
	private static final String UNSUPPORTED_MEDIA_TYPE_MESSAGE = "Content-Type deve essere application/json";
	private static final String UNEXPECTED_ERROR_MESSAGE = "errore interno del server";
	private static final String FIELD_ERRORS_SEPARATOR = "; ";

	private final BackendService service;
	private final RequestOutcomeLogger outcomeLogger;

	@ExceptionHandler(IllegalArgumentException.class)
	public ResponseEntity<ApiError> handleInvalidRequest(
			IllegalArgumentException exception, HttpServletRequest request) {
		return reject(request, HttpStatus.BAD_REQUEST, null, exception.getMessage());
	}

	@ExceptionHandler({
			HttpMessageNotReadableException.class,
			MissingServletRequestParameterException.class,
			MethodArgumentTypeMismatchException.class
	})
	public ResponseEntity<ApiError> handleUnreadableRequest(HttpServletRequest request) {
		return reject(request, HttpStatus.BAD_REQUEST, null, INVALID_REQUEST_MESSAGE);
	}

	@ExceptionHandler(MethodArgumentNotValidException.class)
	public ResponseEntity<ApiError> handleInvalidFields(
			MethodArgumentNotValidException exception, HttpServletRequest request) {
		String message = exception.getBindingResult().getFieldErrors().stream()
				.map(FieldError::getDefaultMessage)
				.sorted()
				.collect(Collectors.joining(FIELD_ERRORS_SEPARATOR));
		if (message.isEmpty()) {
			message = INVALID_REQUEST_MESSAGE;
		}

		Long simulatorId = null;
		if (exception.getBindingResult().getTarget() instanceof CreateMeasurement measurement) {
			simulatorId = measurement.simulatorId();
			recordRejectedMeasurement(simulatorId, message);
		}
		return reject(request, HttpStatus.BAD_REQUEST, simulatorId, message);
	}

	@ExceptionHandler(InvalidMeasurementException.class)
	public ResponseEntity<ApiError> handleInvalidMeasurement(
			InvalidMeasurementException exception, HttpServletRequest request) {
		recordRejectedMeasurement(exception.getSimulatorId(), exception.getMessage());
		return reject(request, HttpStatus.BAD_REQUEST, exception.getSimulatorId(), exception.getMessage());
	}

	@ExceptionHandler(UnknownDeviceException.class)
	public ResponseEntity<ApiError> handleUnknownDevice(
			UnknownDeviceException exception, HttpServletRequest request) {
		return reject(request, HttpStatus.UNPROCESSABLE_CONTENT, null, exception.getMessage());
	}

	@ExceptionHandler(HttpMediaTypeNotSupportedException.class)
	public ResponseEntity<ApiError> handleUnsupportedMediaType(HttpServletRequest request) {
		return reject(request, HttpStatus.UNSUPPORTED_MEDIA_TYPE, null, UNSUPPORTED_MEDIA_TYPE_MESSAGE);
	}

	@ExceptionHandler(ResourceNotFoundException.class)
	public ResponseEntity<ApiError> handleNotFound(
			ResourceNotFoundException exception, HttpServletRequest request) {
		return reject(request, HttpStatus.NOT_FOUND, null, exception.getMessage());
	}

	@ExceptionHandler(Exception.class)
	public ResponseEntity<ApiError> handleUnexpected(Exception exception, HttpServletRequest request) {
		// Le eccezioni del framework (es. rotta inesistente, metodo non consentito) mantengono il loro status
		if (exception instanceof ErrorResponse errorResponse) {
			String detail = errorResponse.getBody().getDetail();
			return reject(request, errorResponse.getStatusCode(), null,
					detail == null ? INVALID_REQUEST_MESSAGE : detail);
		}
		log.error("errore non gestito su {} {}", request.getMethod(), request.getRequestURI(), exception);
		return reject(request, HttpStatus.INTERNAL_SERVER_ERROR, null, UNEXPECTED_ERROR_MESSAGE);
	}

	private void recordRejectedMeasurement(Long simulatorId, String reason) {
		try {
			service.recordRejectedMeasurement(simulatorId, reason);
		} catch (DataAccessException exception) {
			log.warn("impossibile aggiornare lo stato della sorgente simulatorId={}", simulatorId, exception);
		}
	}

	private ResponseEntity<ApiError> reject(
			HttpServletRequest request, HttpStatusCode status, Long simulatorId, String message) {
		outcomeLogger.rejected(request, status, simulatorId, message);
		return ResponseEntity.status(status).body(new ApiError(message));
	}

	public record ApiError(String errore) {
	}
}
