package com.example.winfinal.dao;

import com.example.winfinal.entity.core.*;
import com.example.winfinal.entity.lookup.*;
import com.example.winfinal.entity.master.*;
import com.example.winfinal.entity.production.*;
import com.example.winfinal.entity.inventory.*;
import com.example.winfinal.entity.operation.*;
import com.example.winfinal.entity.output.*;

public class FarmDAO extends BaseDAO<Farm> {
    public FarmDAO() {
        super(Farm.class);
    }
}
