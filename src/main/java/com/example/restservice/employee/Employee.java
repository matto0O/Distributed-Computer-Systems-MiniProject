package com.example.restservice.employee;

//enum EmployeeDepartment {
//    HR,
//    SOFTWARE,
//    HARDWARE,
//    BOARD
//}

enum EmployeeStatus{
    BUSY,
    AVAILABLE
}

public class Employee {
    private int id;
    private String firstName;
    private int age;
//    private EmployeeDepartment department;
    private EmployeeStatus status;
    private double wage;
    private String photo;

    public Employee() {
    }
    public Employee(int id, String firstName, int age, double wage) {
        this.id = id;
        this.firstName = firstName;
        this.age = age;
        this.wage = wage;
        this.status = EmployeeStatus.AVAILABLE;
        photo = "src/main/resources/users/default.jpg";
    }
    public Employee(int id, String firstName, int age, double wage, EmployeeStatus es) {
        this.id = id;
        this.firstName = firstName;
        this.age = age;
        this.wage = wage;
        this.status = es;
        photo = "C:\\Users\\mateu\\IdeaProjects\\RestService\\src\\main\\resources\\users\\default.jpg";
    }

    public Employee(int id, String firstName, int age, double wage, EmployeeStatus es, String photo) {
        this.id = id;
        this.firstName = firstName;
        this.age = age;
        this.wage = wage;
        this.status = es;
        this.photo = photo;
    }


    public EmployeeStatus getStatus() {
        return status;
    }
    public void setStatus(EmployeeStatus status) {
        this.status = status;
    }

    public double getWage() {
        return wage;
    }
    public void setWage(double wage) {
        this.wage = wage;
    }
    public int getId() {
        return id;
    }
    public void setId(int id) {
        this.id = id;
    }
    public String getFirstName() {
        return firstName;
    }
    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }
    public int getAge() {
        return age;
    }
    public void setAge(int age) {
        this.age = age;
    }
    public String getPhoto(){return photo;}
    public void setPhoto(String photo){this.photo=photo;}

    @Override
    public String toString() {
        return this.id + " " + this.firstName + " " + this.age;
    }
}