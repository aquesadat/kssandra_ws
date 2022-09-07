package com.kssandra.ksd_ws.exception;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@ControllerAdvice
public class CustomRestExceptionHandler extends ResponseEntityExceptionHandler {

	private static final Logger LOG = LoggerFactory.getLogger(CustomRestExceptionHandler.class);

	@Override
	protected ResponseEntity<Object> handleHttpMessageNotReadable(HttpMessageNotReadableException ex,
			HttpHeaders headers, HttpStatus status, WebRequest request) {
		LOG.error("Bad request error: ", ex);
		return buildErrorResponse(HttpStatus.BAD_REQUEST, "Malformed request");
	}

	@ExceptionHandler(value = { KsdServiceException.class })
	protected ResponseEntity<Object> handleConflict(KsdServiceException ex, WebRequest request) {
		LOG.error("Error proccessing request: ", ex.getCause());
		String bodyOfResponse = "Error proccessing request. Please contact our support team.";
		return handleExceptionInternal(ex, bodyOfResponse, new HttpHeaders(), HttpStatus.CONFLICT, request);
	}

	@ExceptionHandler({ Exception.class })
	public ResponseEntity<Object> handleAll(Exception ex, WebRequest request) {
		LOG.error("Unexpected error", ex);
		return buildErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR, "Unexpected error occurred");
	}

	@Override
	protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException ex,
			HttpHeaders headers, HttpStatus status, WebRequest request) {
		LOG.error("Bad request error: {}", ex.getLocalizedMessage());
		String errMsg = "Bad request";
		if (ex.getBindingResult().getFieldError() != null) {
			errMsg = ex.getBindingResult().getFieldError().getField().concat(" - ")
					.concat(ex.getBindingResult().getFieldError().getDefaultMessage());
		}
		return buildErrorResponse(HttpStatus.BAD_REQUEST, errMsg);
	}

	private ResponseEntity<Object> buildErrorResponse(HttpStatus status, String message) {
		ApiError apiError = new ApiError(status, message);
		return new ResponseEntity<>(apiError, new HttpHeaders(), apiError.getStatus());
	}
}
