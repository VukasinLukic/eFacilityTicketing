package com.efacility.ticketing.connection;

import org.springframework.http.HttpStatus;

import java.util.Map;

public class HttpResponse {

    public static Response getResponseWithData(String message, Map<?, ?> data, HttpStatus status) {
        return new Response(message, data, status);
    }

    public static Response getResponse(String message, HttpStatus status) {
        return new Response(message, status);
    }
}
