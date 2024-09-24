package com.datn.shop_app.exception;

import com.datn.shop_app.response.ResponseObject;
import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;

@RestControllerAdvice //chỉ định lớp này xửu lý exception
public class GlobalExceptionHandler {
    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ResponseEntity<ResponseObject> handleGeneralException(Exception ex) {
        return ResponseEntity.internalServerError().body(ResponseObject.builder()
                .message(ex.getMessage())
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .build());
    }

    @ExceptionHandler(DataAccessException.class)
    public ResponseEntity<ResponseObject> handleDataAccessException(DataAccessException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ResponseObject.builder()
                .status(HttpStatus.NOT_FOUND)
                .message(ex.getMessage())
                .build());
    }
}
