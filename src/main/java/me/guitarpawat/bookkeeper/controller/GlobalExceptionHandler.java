package me.guitarpawat.bookkeeper.controller;

import lombok.extern.slf4j.Slf4j;
import me.guitarpawat.bookkeeper.model.ErrorModel;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.ServletRequestBindingException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.server.ResponseStatusException;

import javax.validation.ConstraintViolationException;

@ControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    @ExceptionHandler(ResponseStatusException.class)
    public ResponseEntity<ErrorModel> handleResponseStatus(ResponseStatusException rex) {
        ErrorModel errorRes = new ErrorModel(rex.getRawStatusCode(), rex.getReason());
        log.error("handleResponseStatus", rex);
        return ResponseEntity.status(rex.getStatus()).body(errorRes);
    }

    @ExceptionHandler({
            ServletRequestBindingException.class,
            ConstraintViolationException.class,
            HttpMessageNotReadableException.class,
    })
    public ResponseEntity<ErrorModel> handleBadRequest(Exception bex) {
        ErrorModel errorRes = new ErrorModel(HttpStatus.BAD_REQUEST.value(), bex.getMessage());
        log.error("handleBadRequest", bex);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorRes);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorModel> handleException(Exception ex) {
        ErrorModel errorRes = new ErrorModel(HttpStatus.INTERNAL_SERVER_ERROR.value(), "Something went wrong");
        log.error("handleException", ex);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorRes);
    }

}
