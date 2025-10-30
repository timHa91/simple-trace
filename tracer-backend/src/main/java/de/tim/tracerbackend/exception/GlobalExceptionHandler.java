package de.tim.tracerbackend.exception;

import de.tim.tracerbackend.dto.ErrorResponse;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.convert.ConversionFailedException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.time.Instant;

@ControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger LOGGER = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(ConversionFailedException.class)
    public ResponseEntity<ErrorResponse> handleConversionFailedException(
            HttpServletRequest request,
            ConversionFailedException ex
    ) {
        String path = request.getRequestURI();
        String targetTypeName = ex.getTargetType().getObjectType().getSimpleName();
        String queryString = request.getQueryString();

        LOGGER.warn(
                "Enum conversion failed: {} to type {} | Path: {}",
                queryString,
                targetTypeName,
                path,
                ex
        );

        String message = String.format(
                "Invalid parameter value. Expected type: %s",
                targetTypeName
        );

        var errorResponse = new ErrorResponse(
                Instant.now(),
                "Invalid Parameter",
                message,
                400,
                path
        );

        return ResponseEntity.badRequest().body(errorResponse);
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ErrorResponse> handleMethodArgumentTypeMismatch(
            HttpServletRequest request,
            MethodArgumentTypeMismatchException ex
    ) {
        String path = request.getRequestURI();
        String paramName = ex.getPropertyName();
        String requiredType = ex.getRequiredType() != null
                ? ex.getRequiredType().getSimpleName()
                : "unknown";

        LOGGER.warn(
                "Method argument type mismatch: parameter '{}' | Path: {}",
                paramName,
                path,
                ex
        );

        String message = String.format(
                "Invalid value for parameter '%s'. Expected type: %s",
                paramName,
                requiredType
        );

        var errorResponse = new ErrorResponse(
                Instant.now(),
                "Invalid Parameter",
                message,
                400,
                path
        );

        return ResponseEntity.badRequest().body(errorResponse);
    }
}
