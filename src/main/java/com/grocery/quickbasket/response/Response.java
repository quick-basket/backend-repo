package com.grocery.quickbasket.response;

import lombok.Data;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

@Data
public class Response<T> {
    private int statusCode;
    private boolean success = false;
    private String message;
    private T data;

    public Response(int statusCode, String message, T data){
        this.statusCode = statusCode;
        this.message = message;
        this.data = data;

        if (statusCode == HttpStatus.OK.value()){
            this.success = true;
        }
    }

    public static <T> ResponseEntity<Response<T>> failedResponse(int statusCode, String message, T data){
        Response<T> response = new Response<>(statusCode, message, data);
        response.setSuccess(false);
        return ResponseEntity.status(statusCode).body(response);
    }

    public static <T> ResponseEntity<Response<T>> failedResponse(String message, T data){
        Response<T> response = new Response<>(HttpStatus.BAD_REQUEST.value(), message, data);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST.value()).body(response);
    }

    public static <T> ResponseEntity<Response<T>> successResponse(int statusCode, String message, T data){
        Response<T> response = new Response<>(statusCode, message, data);
        return ResponseEntity.status(statusCode).body(response);
    }

    public static <T> ResponseEntity<Response<T>> successResponse(String message, T data){
        Response<T> response = new Response<>(HttpStatus.OK.value(), message, data);
        return ResponseEntity.status(HttpStatus.OK.value()).body(response);
    }

}
