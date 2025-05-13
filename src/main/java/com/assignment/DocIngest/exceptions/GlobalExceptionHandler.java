package com.assignment.DocIngest.exceptions;



import com.assignment.DocIngest.dto.ErrorResp;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@ControllerAdvice
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResp> handleGlobalException(Exception exception,
                                                                            WebRequest webRequest) {
        ErrorResp errorRespDto = new ErrorResp(
                webRequest.getDescription(false),
                exception.getMessage(),
                HttpStatus.INTERNAL_SERVER_ERROR,
                LocalDateTime.now());
        return new ResponseEntity<>(errorRespDto, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(UserAlreadyExistException.class)
    public ResponseEntity<ErrorResp> handleUserAlreadyExistException(UserAlreadyExistException userAlreadyExistException,
                                                                         WebRequest webRequest) {
        ErrorResp errorRespDto = new ErrorResp(
                webRequest.getDescription(false),
                userAlreadyExistException.getMessage(),
                HttpStatus.BAD_REQUEST,
                LocalDateTime.now());
        return new ResponseEntity<>(errorRespDto, HttpStatus.BAD_REQUEST);
    }
    @ExceptionHandler(UsernameNotFoundException.class)
    public ResponseEntity<ErrorResp> handleUserNotFoundException(UsernameNotFoundException usernameNotFoundException,
                                                                     WebRequest webRequest) {
        ErrorResp errorRespDto = new ErrorResp(
                webRequest.getDescription(false),
                usernameNotFoundException.getMessage(),
                HttpStatus.NOT_FOUND,
                LocalDateTime.now());
        return new ResponseEntity<>(errorRespDto, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ErrorResp> handleResourceNotFoundException(ResourceNotFoundException resourceNotFoundException,
                                                                            WebRequest webRequest) {
        ErrorResp errorRespDto = new ErrorResp(
                webRequest.getDescription(false),
                resourceNotFoundException.getMessage(),
                HttpStatus.NOT_FOUND,
                LocalDateTime.now());
        return new ResponseEntity<>(errorRespDto, HttpStatus.NOT_FOUND);
    }

    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException ex, HttpHeaders headers, HttpStatusCode status, WebRequest request) {
        Map<String, String> errors = new HashMap<>(); //<key, value>
        List<ObjectError> errorList = ex.getBindingResult().getAllErrors();

        errorList.forEach(error -> {
            String fieldName = ((FieldError) error).getField();
            String message = error.getDefaultMessage();
            errors.put(fieldName, message);
        });
        return new ResponseEntity<>(errors, HttpStatus.BAD_REQUEST);
    }
}
