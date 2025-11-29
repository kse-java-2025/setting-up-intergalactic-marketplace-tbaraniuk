package com.example.intergalactic_marketplace.web;

import com.example.intergalactic_marketplace.featuretoggle.exception.FeatureToggleNotEnabledException;
import com.example.intergalactic_marketplace.service.exception.*;
import com.example.intergalactic_marketplace.web.exception.GreetingNotFoundException;
import com.example.intergalactic_marketplace.web.exception.ParamsViolationDetails;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.net.URI;
import java.time.Instant;
import java.util.List;

import static org.springframework.http.HttpStatus.*;

@ControllerAdvice
@Slf4j
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {
    public static final String VALIDATION_FAILED_MESSAGE = "Validation failed. Check 'errors' field for details.";

    @ExceptionHandler(PersistenceException.class)
    ProblemDetail handlePersistenceException(RuntimeException ex) {
        log.error("Persistence exception raised", ex);
        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(INTERNAL_SERVER_ERROR, ex.getMessage());
        problemDetail.setType(URI.create("persistence-exception"));
        problemDetail.setTitle("Persistence Exception");
        return problemDetail;
    }

    @ExceptionHandler(GreetingNotFoundException.class)
    ProblemDetail handleStoreConfigurationNotFoundException(GreetingNotFoundException ex) {
        log.warn("Greeting Not Found exception raised");

        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(NOT_FOUND, ex.getMessage());
        problemDetail.setType(URI.create("greeting-not-found"));
        problemDetail.setTitle("Greeting Not Found");
        problemDetail.setProperty("timestamp", Instant.now());
        return problemDetail;
    }


    @ExceptionHandler(ProductNotFoundException.class)
    ProblemDetail handleNotFoundException(ProductNotFoundException ex) {
        log.warn("Product Not Found exception raised");

        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(NOT_FOUND, ex.getMessage());
        problemDetail.setType(URI.create("product-not-found"));
        problemDetail.setTitle("Product Not Found");
        problemDetail.setProperty("timestamp", Instant.now());
        return problemDetail;
    }

    @ExceptionHandler(ProductAlreadyExistsException.class)
    ProblemDetail handleProductAlreadyExistsException(ProductAlreadyExistsException ex) {
        log.warn("Product Already Exists exception raised");

        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(CONFLICT, ex.getMessage());
        problemDetail.setType(URI.create("product-already-exists"));
        problemDetail.setTitle("Product Already Exists");
        problemDetail.setProperty("timestamp", Instant.now());
        return problemDetail;
    }

    @ExceptionHandler(ProductCategoryAlreadyExistsException.class)
    ProblemDetail handleProductCategoryAlreadyExistsException(ProductCategoryAlreadyExistsException ex) {
        log.warn("Product Category Already Exists exception raised");

        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(CONFLICT, ex.getMessage());
        problemDetail.setType(URI.create("product-category-already-exists"));
        problemDetail.setTitle("Product Category Already Exists");
        problemDetail.setProperty("timestamp", Instant.now());
        return problemDetail;
    }

    @ExceptionHandler(ProductCategoryNotFoundException.class)
    ProblemDetail handleProductCategoryNotFoundException(ProductCategoryAlreadyExistsException ex) {
        log.warn("Product Category Not Found exception raised");

        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(NOT_FOUND, ex.getMessage());
        problemDetail.setType(URI.create("product-category-not-found"));
        problemDetail.setTitle("Product Category Not Found");
        problemDetail.setProperty("timestamp", Instant.now());
        return problemDetail;
    }

    @ExceptionHandler(FeatureToggleNotEnabledException.class)
    ProblemDetail handleFeatureToggleNotEnabledException(FeatureToggleNotEnabledException ex) {
        log.warn("Feature Toggle Not Enabled exception raised");
        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(NOT_FOUND, ex.getMessage());
        problemDetail.setType(URI.create("feature-toggle-not-enabled"));
        problemDetail.setTitle("Feature Toggle Not Enabled");
        problemDetail.setProperty("timestamp", Instant.now());
        return problemDetail;
    }

    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException ex, HttpHeaders headers, HttpStatusCode statusCode, WebRequest request) {
        List<FieldError> fieldErrors = ex.getBindingResult().getFieldErrors();
        List<ParamsViolationDetails> validationResponse = fieldErrors.stream().map(err ->
                ParamsViolationDetails.builder().reason(err.getDefaultMessage()).fieldName(err.getField()).build()).toList();

        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(statusCode, VALIDATION_FAILED_MESSAGE);
        problemDetail.setTitle("Validation Failed");
        problemDetail.setType(URI.create("validation-error"));
        problemDetail.setProperty("timestamp", Instant.now());
        problemDetail.setProperty("errors", validationResponse);
        problemDetail.setInstance(URI.create(request.getContextPath()));

        log.warn("Validation error: {}", validationResponse);

        return ResponseEntity.status(statusCode).body(problemDetail);
    }
}
