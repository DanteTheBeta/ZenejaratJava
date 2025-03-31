package com.zenejarat.backend.exception;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.util.HashMap;
import java.util.Map;

@ControllerAdvice // Ezzel jelzem, hogy ez egy globális kivételkezelő, ami minden vezérlőre érvényes.
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {

    // Felülírom a metódust, ami a @Valid annotációval jelölt mezők hibáit kezeli.
    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(
            MethodArgumentNotValidException ex, // Ez tartalmazza a validációs hibákat
            HttpHeaders headers,
            HttpStatusCode status,
            WebRequest request) {

        Map<String, String> errors = new HashMap<>(); // Ide gyűjtöm ki a mezőnkénti hibaüzeneteket.

        // Végigmegyek minden hibán, és a mező nevét a hibával párosítva beleteszem a map-be.
        ex.getBindingResult().getFieldErrors().forEach(error ->
                errors.put(error.getField(), error.getDefaultMessage()));

        // Visszaküldöm a hibákat JSON formátumban, a megfelelő státuszkóddal és fejléc beállításokkal.
        return new ResponseEntity<>(errors, headers, status);
    }
}
