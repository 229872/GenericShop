package pl.lodz.p.edu.presentation.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.server.ResponseStatusException;
import pl.lodz.p.edu.exception.ExceptionMessage;
import pl.lodz.p.edu.presentation.dto.exception.ExceptionResponseDto;
import pl.lodz.p.edu.presentation.dto.exception.ValidationExceptionResponseDto;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import static java.util.stream.Collectors.*;

@RestControllerAdvice
@Slf4j
public class ExceptionController {

    @ExceptionHandler(Exception.class)
    ResponseEntity<?> handleOtherExceptions(Exception e) {
        log.error("Unknown exception occurred: ", e);

        ExceptionResponseDto body = ExceptionResponseDto.builder()
            .status(HttpStatus.INTERNAL_SERVER_ERROR.value())
            .timestamp(LocalDateTime.now())
            .message(ExceptionMessage.UNKNOWN)
            .build();

        return ResponseEntity.internalServerError().body(body);
    }

    @ExceptionHandler(ResponseStatusException.class)
    ResponseEntity<?> handleResponseStatusException(ResponseStatusException e) {
        log.info("Exception occured: ", e);

        ExceptionResponseDto body = ExceptionResponseDto.builder()
            .timestamp(LocalDateTime.now())
            .status(e.getStatusCode().value())
            .message(e.getReason())
            .build();

        return ResponseEntity.status(e.getStatusCode()).body(body);
    }


    @ExceptionHandler(MethodArgumentNotValidException.class)
    ResponseEntity<?> handleValidationException(MethodArgumentNotValidException ex) {
        log.info("Validation exception occurred: ", ex);

        Map<String, List<String>> fieldErrors = ex.getFieldErrors().stream()
            .collect(groupingBy(FieldError::getField,
                mapping(DefaultMessageSourceResolvable::getDefaultMessage, toList())));


        ValidationExceptionResponseDto response = ValidationExceptionResponseDto.builder()
            .status(HttpStatus.BAD_REQUEST.value())
            .error(HttpStatus.BAD_REQUEST.getReasonPhrase())
            .messages(fieldErrors)
            .build();

        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    ResponseEntity<?> handleJacksonParsingException(HttpMessageNotReadableException e) {
        log.info("Exception occurred during json mapping: ", e);

        ExceptionResponseDto body = ExceptionResponseDto.builder()
            .timestamp(LocalDateTime.now())
            .status(HttpStatus.BAD_REQUEST.value())
            .message("Wrong content format")
            .build();

        return ResponseEntity.badRequest().body(body);
    }

    @ExceptionHandler(AccessDeniedException.class)
    ResponseEntity<?> handleAccessDeniedException(AccessDeniedException e) {
        log.info("Exception occurred during authorization: ", e);

        ExceptionResponseDto body = ExceptionResponseDto.builder()
            .timestamp(LocalDateTime.now())
            .status(HttpStatus.FORBIDDEN.value())
            .message("You are not allowed to do this operation")
            .build();

        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(body);
    }
}
