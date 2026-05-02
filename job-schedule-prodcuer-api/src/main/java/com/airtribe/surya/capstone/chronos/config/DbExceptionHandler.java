package com.airtribe.surya.capstone.chronos.config;

import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.net.ConnectException;
import java.sql.SQLException;

@ControllerAdvice
public class DbExceptionHandler {

    @ExceptionHandler({DataAccessException.class, SQLException.class, ConnectException.class})
    public ResponseEntity<String> handleDbException(Exception ex) {
        String msg = "Database unavailable: " + ex.getMessage();
        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body(msg);
    }
}
