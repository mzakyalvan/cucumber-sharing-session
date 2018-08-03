package com.tiket.poc.rest;

import lombok.Getter;
import org.springframework.core.NestedRuntimeException;
import org.springframework.http.HttpStatus;
import org.springframework.util.Assert;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.ResponseStatus;

@Getter
@ResponseStatus(value = HttpStatus.BAD_REQUEST)
@SuppressWarnings("serial")
public class DataBindingException extends NestedRuntimeException {
    private final Errors errors;

    public DataBindingException(Errors errors) {
        super("Data binding error");
        Assert.notNull(errors, "Errors object must be provided");
        this.errors = errors;
    }
}
