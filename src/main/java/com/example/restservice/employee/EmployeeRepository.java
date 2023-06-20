package com.example.restservice.employee;

import com.example.restservice.errors.EmployeeExistsEx;
import com.example.restservice.errors.EmployeeNotFoundEx;

import java.util.List;

public interface EmployeeRepository {
    List<Employee> getAllEmployees();
    Employee getEmployee(int id) throws EmployeeNotFoundEx;
    void updateEmployee(int id, String name, int age, double wage) throws
            EmployeeNotFoundEx;
    boolean deleteEmployee(int id) throws EmployeeNotFoundEx;
    Employee addEmployee(int id, String name, int age, double wage) throws EmployeeExistsEx;
    int countEmployees();
    public int lastId();
}