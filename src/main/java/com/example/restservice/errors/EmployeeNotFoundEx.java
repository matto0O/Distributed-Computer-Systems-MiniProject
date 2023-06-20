package com.example.restservice.errors;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(code = HttpStatus.NOT_FOUND)
public class EmployeeNotFoundEx extends RuntimeException {
    public EmployeeNotFoundEx() {
        super("The specified employee does not exist");
    }
    public EmployeeNotFoundEx(int id) {
//super("The employee of id="+id+" does NOT exist");
        super(String.valueOf(id));
    }
}