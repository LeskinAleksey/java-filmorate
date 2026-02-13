package ru.yandex.practicum.filmorate.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import ru.yandex.practicum.filmorate.model.ErrorResponse;

import java.util.HashMap;
import java.util.Map;

import static ru.yandex.practicum.filmorate.controller.FilmController.*;
import static ru.yandex.practicum.filmorate.controller.UserController.*;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ValidationException.class)
    public ResponseEntity<Map<String, String>> handleValidationException(ValidationException e) {
        Map<String, String> error = new HashMap<>();
        error.put("error", e.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidationExceptions(MethodArgumentNotValidException e) {
        ErrorResponse errors = new ErrorResponse();

        e.getBindingResult().getFieldErrors().forEach(error -> {
            String fieldName = error.getField();
            String errorMessage = error.getDefaultMessage();

            switch (fieldName) {
                case "email":
                    errors.setError(BLANK_EMAIL);
                    break;
                case "login":
                    errors.setError(BLANK_LOGIN);
                    break;
                case "birthday":
                    errors.setError(WRONG_BIRTHDAY);
                    break;
                case "name":
                    errors.setError(BLANK_NAME);
                    break;
                case "description":
                    errors.setError(LONG_DESCRIPTION);
                    break;
                case "releaseDate":
                    errors.setError(WRONG_RELEASE_DATE);
                    break;
                case "duration":
                    errors.setError(WRONG_DURATION);
                    break;
                default:
                    errors.setError(errorMessage);
            }
        });
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errors);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, String>> handleException(Exception e) {
        Map<String, String> error = new HashMap<>();
        error.put("error", e.getMessage());
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
    }
}
