package com.datn.laptopshop.config;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.HashMap;
import java.util.Map;

public class ResponseHandler {
    public static ResponseEntity<Object> responseBuilder( String titleMess,
            String message, HttpStatus httpStatus, Object responseObject, int number
    ){
        Map response = new HashMap<>();
        response.put("responseCode", number);
        response.put(titleMess, message);
        response.put("data", responseObject);

        return new ResponseEntity<>(response, httpStatus);
    }
}
