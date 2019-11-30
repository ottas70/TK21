package cz.cvut.fel.tk21.rest.handler;

import cz.cvut.fel.tk21.exception.InvalidCredentialsException;
import cz.cvut.fel.tk21.exception.NotFoundException;
import cz.cvut.fel.tk21.exception.ValidationException;
import cz.cvut.fel.tk21.model.security.ApiResponse;
import io.jsonwebtoken.SignatureException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import javax.persistence.PersistenceException;
import javax.servlet.http.HttpServletRequest;


@ControllerAdvice
public class RestExceptionHandler {

    private static final Logger LOG = LoggerFactory.getLogger(RestExceptionHandler.class);

    private static void logException(RuntimeException ex) {
        LOG.error("Exception caught:", ex);
    }

    private static ErrorInfo errorInfo(HttpServletRequest request, Throwable e) {
        return new ErrorInfo(e.getMessage(), request.getRequestURI());
    }

    @ExceptionHandler(PersistenceException.class)
    public ResponseEntity<ErrorInfo> persistenceException(HttpServletRequest request, PersistenceException e) {
        logException(e);
        return new ResponseEntity<>(errorInfo(request, e), HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(InvalidCredentialsException.class)
    public ResponseEntity<ApiResponse> invalidCredentialsException(HttpServletRequest request, InvalidCredentialsException e) {
        return new ResponseEntity<>(new ApiResponse(HttpStatus.UNAUTHORIZED.value(), e.getMessage()), HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<ErrorInfo> notFoundException(HttpServletRequest request, NotFoundException e) {
        return new ResponseEntity<>(errorInfo(request, e), HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(ValidationException.class)
    public ResponseEntity<ErrorInfo> validationException(HttpServletRequest request, ValidationException e) {
        return new ResponseEntity<>(errorInfo(request, e), HttpStatus.CONFLICT);
    }
}
