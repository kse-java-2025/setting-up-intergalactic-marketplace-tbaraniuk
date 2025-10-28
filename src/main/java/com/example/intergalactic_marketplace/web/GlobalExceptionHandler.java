package com.example.intergalactic_marketplace.web;

import com.example.intergalactic_marketplace.service.exception.ProductAlreadyExistsException;
import com.example.intergalactic_marketplace.service.exception.ProductNotFoundException;
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
import java.util.List;

import static org.springframework.http.HttpStatus.CONFLICT;
import static org.springframework.http.HttpStatus.NOT_FOUND;

@ControllerAdvice
@Slf4j
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {
    @ExceptionHandler(ProductNotFoundException.class)
    ProblemDetail handleNotFoundException(ProductNotFoundException ex) {
        log.info("Product Not Found exception raised");
        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(NOT_FOUND, ex.getMessage());
        problemDetail.setType(URI.create("product-not-found"));
        problemDetail.setTitle("Product Not Found");
        return problemDetail;
    }

    @ExceptionHandler(ProductAlreadyExistsException.class)
    ProblemDetail handleProductAlreadyExistsException(ProductAlreadyExistsException ex) {
        log.info("Product Already Exists exception raised");
        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(CONFLICT, ex.getMessage());
        problemDetail.setType(URI.create("product-already-exists"));
        problemDetail.setTitle("Product Already Exists");
        return problemDetail;
    }

    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException ex, HttpHeaders headers, HttpStatusCode statusCode, WebRequest request) {
        List<FieldError> fieldErrors = ex.getBindingResult().getFieldErrors();
        List<ParamsViolationDetails> validationResponse = fieldErrors.stream().map(err ->
                ParamsViolationDetails.builder().reason(err.getDefaultMessage()).fieldName(err.getField()).build()).toList();
        log.info("Validation error: {}", validationResponse);
        return ResponseEntity.badRequest().body(validationResponse);
    }
}
