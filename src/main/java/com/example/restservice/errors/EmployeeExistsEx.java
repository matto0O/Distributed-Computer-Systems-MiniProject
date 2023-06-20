package com.example.restservice.errors;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(code = HttpStatus.IM_USED)
public class EmployeeExistsEx extends RuntimeException {
    public EmployeeExistsEx() {
        super("The specified employee already exist");
    }
    public EmployeeExistsEx(int id) {
//super("The employee of id="+id+" does NOT exist");
        super(String.valueOf(id));
    }
}