package it.energymonitor.backend.api;

import it.energymonitor.backend.service.ResourceNotFoundException;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

@RestControllerAdvice
public class ApiExceptionHandler {

	@ExceptionHandler(IllegalArgumentException.class)
	public ResponseEntity<ApiError> handleInvalidRequest(IllegalArgumentException exception) {
		return ResponseEntity.badRequest().body(new ApiError(exception.getMessage()));
	}

	@ExceptionHandler({
			HttpMessageNotReadableException.class,
			MissingServletRequestParameterException.class,
			MethodArgumentTypeMismatchException.class
	})
	public ResponseEntity<ApiError> handleUnreadableRequest() {
		return ResponseEntity.badRequest().body(new ApiError("richiesta non valida o incompleta"));
	}

	@ExceptionHandler(HttpMediaTypeNotSupportedException.class)
	public ResponseEntity<ApiError> handleUnsupportedMediaType() {
		return ResponseEntity.status(HttpStatus.UNSUPPORTED_MEDIA_TYPE)
				.body(new ApiError("Content-Type deve essere application/json"));
	}

	@ExceptionHandler(ResourceNotFoundException.class)
	public ResponseEntity<ApiError> handleNotFound(ResourceNotFoundException exception) {
		return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ApiError(exception.getMessage()));
	}

	public record ApiError(String errore) {
	}
}
