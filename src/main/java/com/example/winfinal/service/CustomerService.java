package com.example.winfinal.service;

import com.example.winfinal.dao.CustomerDAO;
import com.example.winfinal.dto.CustomerDTO;
import com.example.winfinal.entity.output.Customer;
import com.example.winfinal.mapper.CustomerMapper;

public class CustomerService extends BaseService<Customer, CustomerDTO> {
    private static final CustomerMapper mapper = CustomerMapper.INSTANCE;

    public CustomerService() {
        super(new CustomerDAO());
    }

    @Override
    protected CustomerDTO toDTO(Customer entity) {
        return mapper.toDTO(entity);
    }

    @Override
    protected Customer toEntity(CustomerDTO dto) {
        return mapper.toEntity(dto);
    }

    @Override
    protected void updateEntityFromDTO(CustomerDTO dto, Customer entity) {
        mapper.updateEntityFromDTO(dto, entity);
    }

    @Override
    protected Object getEntityId(CustomerDTO dto) {
        return dto.getId();
    }
}
