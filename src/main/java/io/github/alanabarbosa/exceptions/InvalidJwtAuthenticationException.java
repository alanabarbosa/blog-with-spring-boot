package io.github.alanabarbosa.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.FORBIDDEN)
public class InvalidJwtAuthenticationException extends RuntimeException {

	private static final long serialVersionUID = 1L;	
	
	public InvalidJwtAuthenticationException(String ex) {
		super(ex);
	}
}
