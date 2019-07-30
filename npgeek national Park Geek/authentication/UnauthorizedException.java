package com.techelevator.npgeek.authentication;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(code=HttpStatus.FORBIDDEN)
public class UnauthorizedException extends Exception{
	
	/**
	 * UnauthorizedException
	 */
private static final long serialVersionUID = 1L;
	
}
