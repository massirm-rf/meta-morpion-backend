package fr.uparis.morpion.metamorpionback.exceptionhandling;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.util.Map;

@Order(Ordered.HIGHEST_PRECEDENCE)
@org.springframework.web.bind.annotation.ControllerAdvice
public class ControllerAdvice extends ResponseEntityExceptionHandler {
    private static final Logger EXCEPTION_LOGGER = LoggerFactory.getLogger(ControllerAdvice.class);

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Object> exception(Exception ex) {
        Map<String, Object> body = new HttpErrorsBuilder()
                .addError("error.server.internal")
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .build();
        ex.printStackTrace();
        EXCEPTION_LOGGER.warn("UNKNOWN EXCEPTION : {}", body);
        EXCEPTION_LOGGER.warn("EXCEPTION MESSAGE EXCEPTION : {}", ex.getMessage());
        EXCEPTION_LOGGER.error("INTERNAL SERVER ERROR", ex);
        return new ResponseEntity<>(body, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Object> handleExceptions(IllegalArgumentException ex) {
        Map<String, Object> body = new HttpErrorsBuilder().addError(ex.getMessage()).build();
        EXCEPTION_LOGGER.error("One or more parameters are not sent correctly : {} ", body, ex);
        return new ResponseEntity<>(body, HttpStatus.BAD_REQUEST);
    }
}
