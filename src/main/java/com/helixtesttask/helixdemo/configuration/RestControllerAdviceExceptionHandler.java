package com.helixtesttask.helixdemo.configuration;

import lombok.Data;
import lombok.val;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@RestControllerAdvice
public class RestControllerAdviceExceptionHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler({HttpClientErrorException.class, IllegalStateException.class, IllegalArgumentException.class})
    public ResponseEntity<ErrorDto> handleClientException(Exception ex) {
        return new ResponseEntity<ErrorDto>(prepareResponse("Err occurred. Msg: " + ex.getMessage(), HttpStatus.BAD_REQUEST),
                new HttpHeaders(), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler({HttpServerErrorException.class})
    public ResponseEntity<ErrorDto> handleServerException(Exception ex) {
        return new ResponseEntity<ErrorDto>(prepareResponse("Err occurred. Msg: " + ex.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR),
                new HttpHeaders(), HttpStatus.INTERNAL_SERVER_ERROR);
    }

    private ErrorDto prepareResponse(String msg, HttpStatus status) {
        val err = new ErrorDto();
        err.setCode(status.value());
        err.setMsg(msg);
        err.setStatus(status.toString());
        return err;
    }

    @Data
    static class ErrorDto {
        String msg;
        int code;
        String status;
    }
}
