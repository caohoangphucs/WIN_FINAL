package com.example.winfinal.service;

import com.example.winfinal.dao.IrrigationLogDAO;
import com.example.winfinal.entity.operation.IrrigationLog;
import java.util.List;

/**
 * IrrigationLog: không có Mapper/DTO riêng (dạng raw entity),
 * các phương thức thống kê trả về Object[].
 */
public class IrrigationLogService {
    private final IrrigationLogDAO dao = new IrrigationLogDAO();

    private void validate(IrrigationLog log) {
        if (log == null) throw new IllegalArgumentException("Dữ liệu tưới tiêu không được để trống");
        if (log.getLot() == null) throw new IllegalArgumentException("Phải chỉ định lô đất (Lot)");
        if (log.getEmployee() == null) throw new IllegalArgumentException("Phải chỉ định nhân viên thực hiện (Employee)");
    }

    public void save(IrrigationLog log) { 
        validate(log); 
        dao.save(log); 
    }
    
    public void update(IrrigationLog log) { 
        validate(log);
        if (log.getId() == null) throw new IllegalArgumentException("ID không được để trống khi phân bổ cập nhật (update)");
        dao.update(log); 
    }
    public void delete(Long id) { dao.delete(id); }
    public List<IrrigationLog> getAll() { return dao.findAll(); }
    public IrrigationLog getById(Long id) { return (IrrigationLog) dao.findById(id); }

    // [4.4] Tổng lượng nước tưới theo tháng cho một lô
    public List<Object[]> getMonthlyWaterUsage(Long lotId, int year) {
        return dao.getMonthlyWaterUsage(lotId, year);
    }

    // [5.4] Nhật ký tưới tiêu theo mã lô (Tra cứu nguồn gốc)
    public List<Object[]> getTraceabilityLogs(String lotCode) {
        return dao.getTraceabilityLogs(lotCode);
    }

    // Lấy tất cả nhật ký tưới tiêu của một lô
    public List<IrrigationLog> findByLot(Long lotId) {
        return dao.findByLot(lotId);
    }
}
