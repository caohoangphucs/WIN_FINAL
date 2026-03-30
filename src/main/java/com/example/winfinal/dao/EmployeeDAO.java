package com.example.winfinal.dao;

import com.example.winfinal.entity.Employee;

public class EmployeeDAO extends BaseDAO<Employee> {
    public EmployeeDAO() {
        super(Employee.class);
    }
}
