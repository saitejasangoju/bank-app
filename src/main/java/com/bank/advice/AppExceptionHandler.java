package com.bank.advice;

import java.util.NoSuchElementException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import com.bank.exception.AgeNotSatisfiedException;
import com.bank.exception.CustomerNotMatchAccount;
import com.bank.exception.ErrorResponse;
import com.bank.exception.NotActiveException;

@SuppressWarnings("serial")
@RestControllerAdvice
public class AppExceptionHandler extends RuntimeException{

	@ResponseStatus(code = HttpStatus.BAD_REQUEST)
	@ExceptionHandler(NoSuchElementException.class)
	public @ResponseBody ErrorResponse handleException(NoSuchElementException ex){
        return new ErrorResponse(HttpStatus.NOT_FOUND.value(), ex.getMessage());
    }
	
	@ResponseStatus(code = HttpStatus.BAD_REQUEST)
	@ExceptionHandler(AgeNotSatisfiedException.class)
	public @ResponseBody ErrorResponse handleException(AgeNotSatisfiedException ex){
        return new ErrorResponse(HttpStatus.NOT_FOUND.value(), ex.getMessage());
    }
	
	@ResponseStatus(code = HttpStatus.BAD_REQUEST)
	@ExceptionHandler(IllegalArgumentException.class)
	public @ResponseBody ErrorResponse handleException(IllegalArgumentException ex){
        return new ErrorResponse(HttpStatus.NOT_FOUND.value(), ex.getMessage());
    }
	
	@ResponseStatus(code = HttpStatus.BAD_REQUEST)
	@ExceptionHandler(CustomerNotMatchAccount.class)
	public @ResponseBody ErrorResponse handleException(CustomerNotMatchAccount ex){
        return new ErrorResponse(HttpStatus.NOT_FOUND.value(), ex.getMessage());
    }

	@ResponseStatus(code = HttpStatus.BAD_REQUEST)
	@ExceptionHandler(NotActiveException.class)
	public @ResponseBody ErrorResponse handleException(NotActiveException ex){
        return new ErrorResponse(HttpStatus.NOT_FOUND.value(), ex.getMessage());
    
	}
	
	@ResponseStatus(code = HttpStatus.NOT_ACCEPTABLE)
	@ExceptionHandler(MethodArgumentNotValidException.class)
	public @ResponseBody ErrorResponse handleException(MethodArgumentNotValidException ex) {
		return new ErrorResponse(HttpStatus.NOT_ACCEPTABLE.value(), ex.getMessage());
	}
	
}
