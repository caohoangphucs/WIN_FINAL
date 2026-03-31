package com.example.winfinal.controller;

import com.example.winfinal.service.BaseService;
import java.util.List;

public abstract class BaseController<D> {
    protected final BaseService<?, D> service;

    protected BaseController(BaseService<?, D> service) {
        this.service = service;
    }

    public List<D> getAll() {
        return service.getAll();
    }

    public D getById(Object id) {
        return service.getById(id);
    }

    public void create(D dto) {
        service.create(dto);
    }

    public void update(D dto) {
        service.update(dto);
    }

    public void delete(Object id) {
        service.delete(id);
    }
}
