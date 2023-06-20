package com.example.restservice.employee;

import com.example.restservice.errors.EmployeeExistsEx;
import com.example.restservice.errors.EmployeeNotFoundEx;

import java.util.ArrayList;
import java.util.List;

public class EmployeeRepositoryImpl implements EmployeeRepository {
    private List<Employee> employeeList;
    public EmployeeRepositoryImpl() {
        employeeList = new ArrayList<>();
        employeeList.add(new Employee(1, "Alojzy" , 29, 6000, EmployeeStatus.BUSY));
        employeeList.add(new Employee(2, "Jeremiasz", 20, 4252.1,
                EmployeeStatus.BUSY, "src/main/resources/users/photo1.jpg"));
        employeeList.add(new Employee(3, "Gerwazy", 21, 3000));
        employeeList.add(new Employee(4, "Ludomir", 22, 13250));
        employeeList.add(new Employee(5, "Marian", 23, 5700));
    }
    public int lastId(){
        if (employeeList.isEmpty()) return 1;
        return employeeList.get(employeeList.size()-1).getId()+1;
    }
    public List<Employee> getAllEmployees() {
        return employeeList;
    }
    public Employee getEmployee(int id) throws EmployeeNotFoundEx {
        for (Employee theEmployee : employeeList) {
            if (theEmployee.getId() == id) {
                return theEmployee;
            }
        }
        throw new EmployeeNotFoundEx();
    }
    public Employee addEmployee(int id, String name, int age, double wage) throws
            EmployeeExistsEx {
        for (Employee theEmployee : employeeList) {
            if (theEmployee.getId() == id) {
                throw new EmployeeExistsEx();
            }
        }
        Employee employee = new Employee(id, name, age, wage);
        employeeList.add(employee);
        return employee;
    }
    public boolean deleteEmployee(int id) throws EmployeeNotFoundEx {
        for (int i = 0; i < employeeList.size(); i++) {
            if (employeeList.get(i).getId() == id) {
                employeeList.remove(i);
                return true;
            }
        }
        throw new EmployeeNotFoundEx();
    }
    public void updateEmployee(int id, String name, int age, double wage) throws EmployeeNotFoundEx {
        for (int i = 0; i < employeeList.size(); i++) {
            if (employeeList.get(i).getId() == id) {
                employeeList.set(i, new Employee(id, name, age, wage, employeeList.get(i).getStatus()));
                return;
            }
        }
        throw new EmployeeNotFoundEx();
    }
    public int countEmployees() {
        return employeeList.size();
    }
}