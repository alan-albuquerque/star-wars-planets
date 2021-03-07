package com.alantech.starwarsplanets.controller.v1;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;

import com.alantech.starwarsplanets.exception.ResourceAlreadyExistsException;
import com.alantech.starwarsplanets.exception.ResourceNotFoundException;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class ApiExceptionHandler {
	@ResponseStatus(code = HttpStatus.BAD_REQUEST)
	@ExceptionHandler(MethodArgumentNotValidException.class)
	public ResponseEntity<ErrorResponse> handle(MethodArgumentNotValidException exception) {

		ErrorResponse errors = new ErrorResponse();

		ErrorItem rootErrorItem = new ErrorItem();
		rootErrorItem.setCode(ApiErrors.VALIDATION_ERROR);
		errors.setError(rootErrorItem);

		List<ErrorItem> errorItems = exception.getBindingResult().getFieldErrors().stream().map(e -> {
			ErrorItem errorItem = new ErrorItem();
			errorItem.setField(e.getField());
			errorItem.setMessage(e.getDefaultMessage());
			return errorItem;
		}).collect(Collectors.toList());

		errors.setErrors(errorItems);

		return new ResponseEntity<>(errors, HttpStatus.BAD_REQUEST);
	}

	@SuppressWarnings("rawtypes")
	@ExceptionHandler(ConstraintViolationException.class)
	public ResponseEntity<ErrorResponse> handle(ConstraintViolationException e) {
		ErrorResponse errors = new ErrorResponse();

		ErrorItem rootErrorItem = new ErrorItem();
		rootErrorItem.setCode(ApiErrors.VALIDATION_ERROR);
		errors.setError(rootErrorItem);

		for (ConstraintViolation violation : e.getConstraintViolations()) {
			ErrorItem error = new ErrorItem();
			error.setCode(violation.getMessageTemplate());
			error.setMessage(violation.getMessage());
			errors.addError(error);
		}

		return new ResponseEntity<>(errors, HttpStatus.BAD_REQUEST);
	}

	@ExceptionHandler(ResourceNotFoundException.class)
	public ResponseEntity<ErrorItem> handle(ResourceNotFoundException e) {
		ErrorItem error = new ErrorItem();
		error.setMessage(e.getMessage());
		error.setCode(ApiErrors.NOT_FOUND);
		return new ResponseEntity<>(error, HttpStatus.NOT_FOUND);
	}

	@ExceptionHandler(ResourceAlreadyExistsException.class)
	public ResponseEntity<ErrorItem> handle(ResourceAlreadyExistsException e) {
		ErrorItem error = new ErrorItem();
		error.setMessage(e.getMessage());
		error.setCode(ApiErrors.ALREADY_EXISTS);
		return new ResponseEntity<>(error, HttpStatus.CONFLICT);
	}

	@ExceptionHandler(Exception.class)
	public ResponseEntity<ErrorItem> handle() {
		ErrorItem error = new ErrorItem();
		error.setMessage("An internal server error occurred.");
		error.setCode(ApiErrors.INTERNAL_SERVER_ERROR);
		return new ResponseEntity<>(error, HttpStatus.INTERNAL_SERVER_ERROR);
	}

	@Getter
	@Setter
	@NoArgsConstructor
	@JsonInclude(JsonInclude.Include.NON_NULL)
	private static class ErrorItem {

		private String code;
		private String field;
		private String message;

	}

	@Getter
	@Setter
	private static class ErrorResponse {
		private ErrorItem error = new ErrorItem();

		private List<ErrorItem> errors = new ArrayList<>();

		public void addError(ErrorItem error) {
			this.errors.add(error);
		}

	}
}
