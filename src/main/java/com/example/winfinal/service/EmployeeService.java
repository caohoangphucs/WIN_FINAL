package com.example.winfinal.service;

import com.example.winfinal.dao.EmployeeDAO;
import com.example.winfinal.dto.EmployeeDTO;
import com.example.winfinal.entity.core.Employee;
import com.example.winfinal.mapper.EmployeeMapper;
import java.util.List;
import java.util.stream.Collectors;

public class EmployeeService extends BaseService<Employee, EmployeeDTO> {
    private static final EmployeeMapper mapper = EmployeeMapper.INSTANCE;
    private final EmployeeDAO employeeDAO;

    public EmployeeService() {
        super(new EmployeeDAO());
        this.employeeDAO = (EmployeeDAO) dao;
    }

    @Override protected EmployeeDTO toDTO(Employee e) { return mapper.toDTO(e); }
    @Override protected Employee toEntity(EmployeeDTO d) { return mapper.toEntity(d); }
    @Override protected void updateEntityFromDTO(EmployeeDTO d, Employee e) { mapper.updateEntityFromDTO(d, e); }
    @Override protected Object getEntityId(EmployeeDTO d) { return d.getId(); }

    @Override
    protected void validate(EmployeeDTO dto) {
        super.validate(dto);
        if (dto.getEmpCode() == null || dto.getEmpCode().trim().isEmpty()) {
            throw new IllegalArgumentException("Mã nhân viên không được để trống");
        }
        if (dto.getFullName() == null || dto.getFullName().trim().isEmpty()) {
            throw new IllegalArgumentException("Họ tên nhân viên không được để trống");
        }
        if (dto.getRoleCode() == null || dto.getRoleCode().trim().isEmpty()) {
            throw new IllegalArgumentException("Vai trò (roleCode) không được để trống");
        }
        if (dto.getId() == null && existsByEmpCode(dto.getEmpCode())) {
            throw new IllegalArgumentException("Mã nhân viên đã tồn tại trong hệ thống: " + dto.getEmpCode());
        }
    }

    // [1.6] Tìm nhân viên theo tên hoặc mã
    public List<EmployeeDTO> search(String keyword) {
        return employeeDAO.search(keyword).stream().map(this::toDTO).collect(Collectors.toList());
    }

    // [4.5] Top nhân viên hoạt động nhiều nhất trong tháng
    public List<Object[]> getTopPerformers(int month, int year) {
        return employeeDAO.getTopPerformers(month, year);
    }

    // [7.2] Kiểm tra mã nhân viên tồn tại
    public boolean existsByEmpCode(String empCode) {
        return employeeDAO.existsByEmpCode(empCode);
    }

    // [7.6] Đếm nhật ký hôm nay của nhân viên
    public Long countTodayLogs(Long employeeId) {
        return employeeDAO.countTodayLogs(employeeId);
    }

    public List<Object[]> getRoleDistribution() { return employeeDAO.getRoleDistribution(); }
    public List<Object[]> getHarvestPerformance() { return employeeDAO.getHarvestPerformance(); }
}
