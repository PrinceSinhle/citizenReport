package citizenReport.com.citizenReport.exception;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import java.util.Map;
import java.util.stream.Collectors;

@RestControllerAdvice
public class GlobalExceptionHandler {
    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(ComplaintNotFoundException.class)
    ResponseEntity<ApiError> complaintNotFound(ComplaintNotFoundException exception, HttpServletRequest request) {
        return error(HttpStatus.NOT_FOUND, "Not Found", exception.getMessage(), request);
    }

    @ExceptionHandler(ComplaintNotSettledException.class)
    ResponseEntity<ApiError> complaintNotSettled(ComplaintNotSettledException exception,
                                                 HttpServletRequest request) {
        return error(HttpStatus.CONFLICT, "Conflict", exception.getMessage(), request);
    }

    @ExceptionHandler(UserNotFoundException.class)
    ResponseEntity<ApiError> userNotFound(UserNotFoundException exception, HttpServletRequest request) {
        return error(HttpStatus.NOT_FOUND, "Not Found", exception.getMessage(), request);
    }

    @ExceptionHandler(EmailAlreadyRegisteredException.class)
    ResponseEntity<ApiError> emailAlreadyRegistered(EmailAlreadyRegisteredException exception,
                                                     HttpServletRequest request) {
        return error(HttpStatus.CONFLICT, "Conflict", exception.getMessage(), request);
    }

    @ExceptionHandler(CannotDeleteCurrentUserException.class)
    ResponseEntity<ApiError> cannotDeleteCurrentUser(CannotDeleteCurrentUserException exception,
                                                     HttpServletRequest request) {
        return error(HttpStatus.CONFLICT, "Conflict", exception.getMessage(), request);
    }

    @ExceptionHandler(InvalidComplaintFilterException.class)
    ResponseEntity<ApiError> invalidComplaintFilter(InvalidComplaintFilterException exception,
                                                    HttpServletRequest request) {
        return error(HttpStatus.BAD_REQUEST, "Bad Request", exception.getMessage(), request);
    }

    @ExceptionHandler(AuthenticationException.class)
    ResponseEntity<ApiError> authenticationFailed(HttpServletRequest request) {
        return error(HttpStatus.UNAUTHORIZED, "Unauthorized", "Invalid email or password.", request);
    }

    @ExceptionHandler(org.springframework.security.access.AccessDeniedException.class)
    ResponseEntity<ApiError> accessDenied(HttpServletRequest request) {
        return error(HttpStatus.FORBIDDEN, "Forbidden", "You do not have permission to perform this action.", request);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    ResponseEntity<ApiError> invalidRequest(MethodArgumentNotValidException exception,
                                            HttpServletRequest request) {
        Map<String, String> details = exception.getBindingResult().getFieldErrors().stream()
                .collect(Collectors.toMap(
                        fieldError -> fieldError.getField(),
                        fieldError -> fieldError.getDefaultMessage() == null ? "Invalid value." : fieldError.getDefaultMessage(),
                        (first, ignored) -> first));
        ApiError body = ApiError.validation(HttpStatus.BAD_REQUEST.value(), "Bad Request",
                "Request validation failed.", request.getRequestURI(), details);
        return ResponseEntity.badRequest().body(body);
    }

    @ExceptionHandler({HttpMessageNotReadableException.class, ConstraintViolationException.class,
            MethodArgumentTypeMismatchException.class})
    ResponseEntity<ApiError> malformedRequest(Exception exception, HttpServletRequest request) {
        return error(HttpStatus.BAD_REQUEST, "Bad Request", "The request body or parameters are invalid.", request);
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    ResponseEntity<ApiError> dataConflict(HttpServletRequest request) {
        return error(HttpStatus.CONFLICT, "Conflict", "The request conflicts with existing data.", request);
    }

    @ExceptionHandler(Exception.class)
    ResponseEntity<ApiError> unexpected(Exception exception, HttpServletRequest request) {
        log.error("Unhandled error for {} {}", request.getMethod(), request.getRequestURI(), exception);
        return error(HttpStatus.INTERNAL_SERVER_ERROR, "Internal Server Error",
                "An unexpected error occurred.", request);
    }

    private ResponseEntity<ApiError> error(HttpStatus status, String title, String message,
                                           HttpServletRequest request) {
        return ResponseEntity.status(status)
                .body(ApiError.of(status.value(), title, message, request.getRequestURI()));
    }
}
