package com.example.winfinal.controller;

import com.example.winfinal.dto.CustomerDTO;
import com.example.winfinal.service.CustomerService;

public class CustomerController extends BaseController<CustomerDTO> {
    public CustomerController() {
        super(new CustomerService());
    }

    public void createCustomer(CustomerDTO dto) {
        create(dto);
    }
    
    public void updateCustomer(CustomerDTO dto) {
        update(dto);
    }
    
    public void deleteCustomer(Long id) {
        delete(id);
    }

    public java.util.List<CustomerDTO> getAllCustomers() {
        return getAll();
    }
}
