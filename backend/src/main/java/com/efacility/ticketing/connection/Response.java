package com.efacility.ticketing.connection;

import com.fasterxml.jackson.annotation.JsonInclude;
import org.springframework.http.HttpStatus;

import java.util.Map;

import static com.fasterxml.jackson.annotation.JsonInclude.Include.NON_NULL;

@JsonInclude(NON_NULL)
public class Response {

    private String message;
    private Map<?, ?> data;
    private HttpStatus status;

    public Response(String message, HttpStatus status) {
        this.message = message;
        this.status = status;
    }

    public Response(String message, Map<?, ?> data, HttpStatus status) {
        this.message = message;
        this.data = data;
        this.status = status;
    }

    public String getMessage() {
        return message;
    }

    public Object getData() {
        return data;
    }

    public HttpStatus getStatus() {
        return status;
    }
}
