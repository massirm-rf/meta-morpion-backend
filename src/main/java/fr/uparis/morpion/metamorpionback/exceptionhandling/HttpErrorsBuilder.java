package fr.uparis.morpion.metamorpionback.exceptionhandling;

import lombok.Getter;
import lombok.Setter;
import org.springframework.http.HttpStatus;

import java.util.*;

@Getter
@Setter
public class HttpErrorsBuilder {

    private static final String TIMESTAMP_KEY = "timestamp";
    private static final String STATUS_KEY = "status";
    private static final String ERRORS_LIST_KEY = "errors";
    private static final String DATA_KEY = "data";


    private Map<String, Object> body = new HashMap<>();
    private Set<String> errors = new HashSet<>();
    private Set<Long> data = new HashSet<>();

    public HttpErrorsBuilder() {
        this.body.put(TIMESTAMP_KEY, new Date());
        this.body.put(STATUS_KEY, HttpStatus.BAD_REQUEST.value());
        this.body.put(ERRORS_LIST_KEY, errors);
        this.body.put(DATA_KEY, data);
    }

    public HttpErrorsBuilder(Date timestamp, HttpStatus status) {
        this.body.put(TIMESTAMP_KEY, timestamp);
        this.body.put(STATUS_KEY, status.value());
        this.body.put(ERRORS_LIST_KEY, errors);
        this.body.put(DATA_KEY, data);
    }

    public HttpErrorsBuilder timestamp(Date timestamp) {
        this.body.put(TIMESTAMP_KEY, timestamp);
        return this;
    }

    public HttpErrorsBuilder status(HttpStatus status) {
        this.body.put(STATUS_KEY, status.value());
        return this;
    }

    public HttpErrorsBuilder addError(String error) {
        this.errors.add(error);
        return this;
    }

    public HttpErrorsBuilder removeError(String error) {
        this.errors.remove(error);
        return this;
    }

    public HttpErrorsBuilder errors(Set<String> errors) {
        this.errors.clear();
        this.errors.addAll(errors);
        return this;
    }

    public HttpErrorsBuilder data(Set<Long> data) {
        this.data.clear();
        this.data.addAll(data);
        return this;
    }

    public HttpErrorsBuilder addData(Long data) {
        this.data.add(data);
        return this;
    }

    public Map<String, Object> build() {
        return this.body;
    }
}

