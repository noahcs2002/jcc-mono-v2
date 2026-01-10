package com.software.teamfive.jcc_product_inventory_management.config;

import com.software.teamfive.jcc_product_inventory_management.utility.exception.company.CompanyNotFoundException;
import com.software.teamfive.jcc_product_inventory_management.utility.exception.join.UserNotInCompanyException;
import com.software.teamfive.jcc_product_inventory_management.utility.exception.permission.InsufficientPermissionsException;
import com.software.teamfive.jcc_product_inventory_management.utility.exception.product.ProductAlreadyExistsException;
import com.software.teamfive.jcc_product_inventory_management.utility.exception.user.UserAlreadyExistsException;
import com.software.teamfive.jcc_product_inventory_management.utility.exception.user.UserNotFoundException;

import io.jsonwebtoken.ExpiredJwtException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ProductAlreadyExistsException.class)
    public ResponseEntity<?> handleDuplicateProduct(ProductAlreadyExistsException ex) {
        return ResponseEntity
                .status(HttpStatus.CONFLICT)
                .body(ex.getMessage());
    }

    @ExceptionHandler(UserAlreadyExistsException.class)
    public ResponseEntity<?> handleDuplicateUser(UserAlreadyExistsException ex) {
        return ResponseEntity.status(HttpStatus.CONFLICT).body(ex.getMessage());
    }

    @ExceptionHandler(UserNotInCompanyException.class)
    public ResponseEntity<?> handleUserNotInCompany(UserNotInCompanyException ex) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(ex.getMessage());
    }

    @ExceptionHandler(InsufficientPermissionsException.class)
    public ResponseEntity<?> handleInsufficientPermissions(InsufficientPermissionsException ex) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(ex.getMessage());
    }

    @ExceptionHandler(CompanyNotFoundException.class)
    public ResponseEntity<?> handleCompanyNotFound(CompanyNotFoundException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ex.getMessage());
    }

    @ExceptionHandler(ExpiredJwtException.class)
    public ResponseEntity<?> handleExpiredToken(ExpiredJwtException ex) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(
                String.format("Token expired!%n%s", ex.getMessage())
        );
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<?> handleBadRequestBody(MethodArgumentNotValidException ex) {

        Map<String, String> errors = new HashMap<>();

        ex.getBindingResult().getFieldErrors().forEach(error ->
                errors.put(error.getField(), error.getDefaultMessage())
        );

        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(errors);
    }

    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<?> handleUserNotFound(UserNotFoundException ex) {
        return ResponseEntity
                .status(HttpStatus.UNAUTHORIZED)
                .body(ex.getMessage());
    }

    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ResponseEntity<?> handleBadMethod(HttpRequestMethodNotSupportedException ex) {
        return ResponseEntity
                .status(HttpStatus.METHOD_NOT_ALLOWED)
                .body(ex.getMessage());
    }


    @ExceptionHandler(Exception.class)
    public ResponseEntity<?> handleGeneric(Exception ex) {
        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("Unexpected error: " + ex.getMessage());
    }
}