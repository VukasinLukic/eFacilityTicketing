package com.efacility.ticketing.exception;

import com.efacility.ticketing.connection.HttpResponse;
import com.efacility.ticketing.connection.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.stream.Collectors;

@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<Response> handleResourceNotFound(ResourceNotFoundException ex) {
        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(HttpResponse.getResponse(ex.getMessage(), HttpStatus.NOT_FOUND));
    }

    @ExceptionHandler(EmailAlreadyExistsException.class)
    public ResponseEntity<Response> handleEmailAlreadyExists(EmailAlreadyExistsException ex) {
        return ResponseEntity
                .status(HttpStatus.CONFLICT)
                .body(HttpResponse.getResponse(ex.getMessage(), HttpStatus.CONFLICT));
    }

    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<Response> handleBadCredentials(BadCredentialsException ex) {
        return ResponseEntity
                .status(HttpStatus.UNAUTHORIZED)
                .body(HttpResponse.getResponse("Pogrešan e-mail ili lozinka!", HttpStatus.UNAUTHORIZED));
    }

    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<Response> handleAuthentication(AuthenticationException ex) {
        return ResponseEntity
                .status(HttpStatus.UNAUTHORIZED)
                .body(HttpResponse.getResponse("Autentifikacija nije uspela: " + ex.getMessage(), HttpStatus.UNAUTHORIZED));
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<Response> handleAccessDenied(AccessDeniedException ex) {
        return ResponseEntity
                .status(HttpStatus.FORBIDDEN)
                .body(HttpResponse.getResponse("Pristup je odbijen!", HttpStatus.FORBIDDEN));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Response> handleValidation(MethodArgumentNotValidException ex) {
        String message = ex.getBindingResult().getFieldErrors()
                .stream()
                .map(e -> e.getField() + ": " + e.getDefaultMessage())
                .collect(Collectors.joining(", "));
        return ResponseEntity
                .badRequest()
                .body(HttpResponse.getResponse(message, HttpStatus.BAD_REQUEST));
    }

    @ExceptionHandler(InvalidStatusTransitionException.class)
    public ResponseEntity<Response> handleInvalidTransition(InvalidStatusTransitionException ex) {
        return ResponseEntity
                .badRequest()
                .body(HttpResponse.getResponse(ex.getMessage(), HttpStatus.BAD_REQUEST));
    }

    @ExceptionHandler(TiketAccessDeniedException.class)
    public ResponseEntity<Response> handleTiketAccessDenied(TiketAccessDeniedException ex) {
        return ResponseEntity
                .status(HttpStatus.FORBIDDEN)
                .body(HttpResponse.getResponse(ex.getMessage(), HttpStatus.FORBIDDEN));
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Response> handleIllegalArgument(IllegalArgumentException ex) {
        return ResponseEntity
                .badRequest()
                .body(HttpResponse.getResponse(ex.getMessage(), HttpStatus.BAD_REQUEST));
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<Response> handleDataIntegrity(DataIntegrityViolationException ex) {
        String detalji = ex.getMostSpecificCause().getMessage();
        String poruka = "Podaci nisu ispravni: narušeno je ograničenje baze podataka.";

        if (detalji != null) {
            String d = detalji.toLowerCase();
            if (d.contains("uk_apartment_building_number")) {
                poruka = "U toj zgradi već postoji stan sa istim brojem.";
            } else if (d.contains("email")) {
                poruka = "Nalog sa ovom e-mail adresom već postoji.";
            }
        }

        log.warn("Narusen integritet baze: {}", detalji);
        return ResponseEntity
                .status(HttpStatus.CONFLICT)
                .body(HttpResponse.getResponse(poruka, HttpStatus.CONFLICT));
    }

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<Response> handleGeneral(RuntimeException ex) {
        log.error("Neocekivana greska", ex);
        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(HttpResponse.getResponse(
                        "Došlo je do neočekivane greške. Pokušajte ponovo.",
                        HttpStatus.INTERNAL_SERVER_ERROR));
    }
}
