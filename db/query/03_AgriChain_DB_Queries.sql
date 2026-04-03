-- =============================================
-- SMART AGRI-CHAIN — JPQL Queries
-- Phiên bản: 2.0 (Đồng bộ với tầng DAO)
-- Entity names: Farm, ProductionLot, CropType,
--   Season, Department, Employee, AgriSupply,
--   Supplier, SupplyImport, SupplyImportDetail,
--   CultivationLog, IrrigationLog, PestReport,
--   WeatherLog, HarvestRecord, Customer
-- Lookup: Role, LotStatus, ActivityType,
--         SeverityLevel, QualityGrade
-- =============================================


-- ══════════════════════════════════════════
-- NHÓM 1: CRUD CƠ BẢN
-- ══════════════════════════════════════════

-- [1.1] Lấy toàn bộ lô sản xuất (ProductionLotDAO.findAll)
SELECT l FROM ProductionLot l
LEFT JOIN FETCH l.farm
LEFT JOIN FETCH l.cropType
LEFT JOIN FETCH l.season
LEFT JOIN FETCH l.manager
ORDER BY l.plantDate DESC

-- [1.2] Tìm lô theo mã lô (ProductionLotDAO.findByLotCode)
SELECT l FROM ProductionLot l
LEFT JOIN FETCH l.farm
LEFT JOIN FETCH l.cropType
LEFT JOIN FETCH l.season
LEFT JOIN FETCH l.manager
WHERE l.lotCode = :lotCode

-- [1.3] Tìm lô theo trạng thái (ProductionLotDAO.findByStatus)
-- Lưu ý: status là quan hệ @ManyToOne sang LotStatus, truy cập qua status.code
SELECT l FROM ProductionLot l
LEFT JOIN FETCH l.farm
LEFT JOIN FETCH l.cropType
LEFT JOIN FETCH l.season
LEFT JOIN FETCH l.manager
WHERE l.status.code = :statusCode
ORDER BY l.lotCode

-- [1.4] Tìm lô theo trang trại (ProductionLotDAO.findByFarm)
SELECT l FROM ProductionLot l
LEFT JOIN FETCH l.farm
LEFT JOIN FETCH l.cropType
LEFT JOIN FETCH l.season
LEFT JOIN FETCH l.manager
WHERE l.farm.id = :farmId
ORDER BY l.plantDate DESC

-- [1.5] Tìm vật tư theo từ khóa (AgriSupplyDAO.search)
SELECT s FROM AgriSupply s
WHERE LOWER(s.name) LIKE LOWER(:kw)
   OR LOWER(s.supplyCode) LIKE LOWER(:kw)
-- Ghi chú: :kw được truyền vào dạng '%' + keyword + '%' từ DAO

-- [1.6] Tìm nhân viên theo tên hoặc mã (EmployeeDAO.search)
SELECT e FROM Employee e
WHERE LOWER(e.fullName) LIKE LOWER(:kw)
   OR LOWER(e.empCode) LIKE LOWER(:kw)
-- Ghi chú: :kw được truyền vào dạng '%' + keyword + '%' từ DAO

-- [1.7] Lấy toàn bộ khách hàng đang hoạt động (CustomerDAO.findAllActive)
SELECT c FROM Customer c
WHERE c.status = 'ACTIVE'
ORDER BY c.name

-- [1.8] Tìm kiếm lô theo nhiều điều kiện động (ProductionLotDAO.searchByConditions)
-- Truyền null để bỏ qua điều kiện đó
SELECT l FROM ProductionLot l
LEFT JOIN FETCH l.farm
LEFT JOIN FETCH l.cropType
LEFT JOIN FETCH l.season
LEFT JOIN FETCH l.manager
WHERE (:farmId IS NULL OR l.farm.id = :farmId)
  AND (:statusCode IS NULL OR l.status.code = :statusCode)
  AND (:cropTypeId IS NULL OR l.cropType.id = :cropTypeId)
  AND (:seasonId IS NULL OR l.season.id = :seasonId)
ORDER BY l.plantDate DESC


-- ══════════════════════════════════════════
-- NHÓM 2: THỐNG KÊ NĂNG SUẤT & SẢN LƯỢNG
-- Dùng trên Dashboard — dành cho Manager
-- ══════════════════════════════════════════

-- [2.1] Năng suất trung bình theo danh mục cây trồng (HarvestRecordDAO.getAvgYieldByCropType)
-- Trả về: Object[] { categoryName, avgYieldKg }
SELECT l.cropType.category.name, AVG(h.yieldKg)
FROM HarvestRecord h
JOIN h.lot l
WHERE h.yieldKg IS NOT NULL
GROUP BY l.cropType.category.name
ORDER BY AVG(h.yieldKg) DESC

-- [2.2] Tổng sản lượng thu hoạch theo mùa vụ (HarvestRecordDAO.getYieldBySeason)
-- Trả về: Object[] { seasonName, totalYieldKg }
SELECT l.season.name, SUM(h.yieldKg)
FROM HarvestRecord h
JOIN h.lot l
WHERE h.yieldKg IS NOT NULL
GROUP BY l.season.name
ORDER BY SUM(h.yieldKg) DESC

-- [2.3] Hiệu suất diện tích — sản lượng trên mỗi m² (HarvestRecordDAO.getYieldPerM2ByLot)
-- Trả về: Object[] { lotCode, yieldPerM2 }
SELECT l.lotCode, SUM(h.yieldKg) / l.areaM2
FROM HarvestRecord h
JOIN h.lot l
WHERE h.yieldKg IS NOT NULL
  AND l.areaM2 > 0
GROUP BY l.lotCode, l.areaM2
ORDER BY SUM(h.yieldKg) / l.areaM2 DESC

-- [2.4] Tỉ lệ xếp loại chất lượng nông sản (HarvestRecordDAO.getQualityGradeStats)
-- Lưu ý: qualityGrade là quan hệ @ManyToOne sang QualityGrade, truy cập qua qualityGrade.code
-- Trả về: Object[] { qualityGradeCode, count }
SELECT h.qualityGrade.code, COUNT(h.id)
FROM HarvestRecord h
WHERE h.qualityGrade IS NOT NULL
GROUP BY h.qualityGrade.code
ORDER BY h.qualityGrade.code

-- [2.5] Tổng sản lượng thu hoạch theo trang trại (HarvestRecordDAO.getYieldByFarm)
-- Trả về: Object[] { farmName, totalYieldKg }
SELECT l.farm.name, SUM(h.yieldKg)
FROM HarvestRecord h
JOIN h.lot l
WHERE h.yieldKg IS NOT NULL
GROUP BY l.farm.name
ORDER BY SUM(h.yieldKg) DESC

-- [2.6] Danh sách lô sắp đến ngày thu hoạch (ProductionLotDAO.findUpcomingHarvest)
-- Lưu ý: status là @ManyToOne sang LotStatus, so sánh qua status.code
SELECT l FROM ProductionLot l
LEFT JOIN FETCH l.farm
LEFT JOIN FETCH l.cropType
LEFT JOIN FETCH l.season
LEFT JOIN FETCH l.manager
WHERE l.expectedHarvestDate BETWEEN CURRENT_DATE AND :nextDate
  AND l.status.code NOT IN ('HARVESTED', 'IDLE')
ORDER BY l.expectedHarvestDate ASC


-- ══════════════════════════════════════════
-- NHÓM 3: QUẢN LÝ KHO VẬT TƯ
-- ══════════════════════════════════════════

-- [3.1] Cảnh báo vật tư sắp hết hàng (AgriSupplyDAO.findLowStock)
SELECT s FROM AgriSupply s
WHERE s.stockQty <= s.minStock
ORDER BY (s.stockQty / s.minStock) ASC

-- [3.2] Tổng lượng vật tư đã tiêu thụ theo danh mục (CultivationLogDAO.getMaterialConsumptionByType)
-- Lưu ý: truy cập qua supply.category.name (supply là @ManyToOne AgriSupply)
-- Trả về: Object[] { categoryName, totalDosage }
SELECT c.supply.category.name, SUM(c.dosageUsed)
FROM CultivationLog c
GROUP BY c.supply.category.name
ORDER BY SUM(c.dosageUsed) DESC

-- [3.3] Tổng chi phí nhập kho theo nhà cung cấp (SupplyImportDAO.getTotalCostBySupplier)
-- Trả về: Object[] { supplierName, totalAmount }
SELECT d.supply.supplier.name, SUM(d.quantity * d.unitPrice)
FROM SupplyImportDetail d
GROUP BY d.supply.supplier.name
ORDER BY SUM(d.quantity * d.unitPrice) DESC

-- [3.4] Lịch sử nhập kho của một vật tư cụ thể (SupplyImportDAO.findBySupply)
SELECT si FROM SupplyImport si
JOIN FETCH si.supplier
JOIN si.details d
WHERE d.supply.id = :supplyId
ORDER BY si.importDate DESC

-- [3.5] Tổng lượng vật tư đã dùng cho một lô (CultivationLogDAO.getUsageByLot)
-- Trả về: Object[] { supplyName, totalDosage }
SELECT c.supply.name, SUM(c.dosageUsed)
FROM CultivationLog c
WHERE c.lot.id = :lotId
GROUP BY c.supply.name
ORDER BY SUM(c.dosageUsed) DESC

-- [3.6] Chi tiết từng lần hoạt động của một lô (CultivationLogDAO.getDetailedActivityByLot)
-- Lưu ý: activityType là @ManyToOne sang ActivityType, truy cập qua activityType.code
-- Trả về: Object[] { activityTypeCode, supplyName, dosageUsed, appliedAt }
SELECT c.activityType.code, c.supply.name, c.dosageUsed, c.appliedAt
FROM CultivationLog c
WHERE c.lot.id = :lotId
ORDER BY c.appliedAt DESC


-- ══════════════════════════════════════════
-- NHÓM 4: GIÁM SÁT CANH TÁC
-- ══════════════════════════════════════════

-- [4.1] Lịch sử bón phân/phun thuốc theo lô và khoảng thời gian (CultivationLogDAO.findByLotAndDateRange)
SELECT c FROM CultivationLog c
LEFT JOIN FETCH c.lot
LEFT JOIN FETCH c.supply
LEFT JOIN FETCH c.activityType
LEFT JOIN FETCH c.employee
WHERE c.lot.id = :lotId
  AND c.appliedAt BETWEEN :startDate AND :endDate
ORDER BY c.appliedAt DESC

-- [4.2] Tìm các lô đang có cảnh báo sâu bệnh nghiêm trọng (PestReportDAO.findHighSeverityReports)
-- Lưu ý: severity là @ManyToOne sang SeverityLevel, status là @ManyToOne sang LotStatus
SELECT p FROM PestReport p
JOIN FETCH p.lot
JOIN FETCH p.employee
JOIN FETCH p.severity
WHERE p.severity.code IN ('HIGH', 'CRITICAL')
  AND p.lot.status.code != 'HARVESTED'
ORDER BY p.lot.id

-- [4.3] Số lần thực hiện theo loại hoạt động của một lô (CultivationLogDAO.getActivityStatsByLot)
-- Trả về: Object[] { activityTypeCode, count }
SELECT c.activityType.code, COUNT(c.id)
FROM CultivationLog c
WHERE c.lot.id = :lotId
GROUP BY c.activityType.code

-- [4.4] Tổng lượng nước tưới theo tháng cho một lô (IrrigationLogDAO.getMonthlyWaterUsage)
-- Trả về: Object[] { month, totalWaterAmount }
SELECT MONTH(i.irrigatedAt), SUM(i.waterAmount)
FROM IrrigationLog i
WHERE i.lot.id = :lotId
  AND YEAR(i.irrigatedAt) = :year
GROUP BY MONTH(i.irrigatedAt)
ORDER BY MONTH(i.irrigatedAt)

-- [4.5] Nhân viên thực hiện nhiều hoạt động canh tác nhất trong tháng (EmployeeDAO.getTopPerformers)
-- Trả về: Object[] { fullName, activityCount }
SELECT c.employee.fullName, COUNT(c.id)
FROM CultivationLog c
WHERE MONTH(c.appliedAt) = :month
  AND YEAR(c.appliedAt) = :year
GROUP BY c.employee.id, c.employee.fullName
ORDER BY COUNT(c.id) DESC


-- ══════════════════════════════════════════
-- NHÓM 5: TRUY XUẤT NGUỒN GỐC (TRACEABILITY)
-- Nhập mã lô → toàn bộ lịch sử canh tác
-- ══════════════════════════════════════════

-- [5.1] Thông tin đầy đủ của một lô (ProductionLotDAO.getFullTraceabilityInfo)
SELECT l FROM ProductionLot l
JOIN FETCH l.farm
JOIN FETCH l.cropType
JOIN FETCH l.season
JOIN FETCH l.manager
WHERE l.lotCode = :lotCode

-- [5.2] Toàn bộ vật tư đã dùng — ai bón, khi nào, bao nhiêu (CultivationLogDAO.getTraceabilityLogs)
-- Trả về: Object[] { appliedAt, activityTypeCode, supplyName, dosageUsed, employeeName }
SELECT c.appliedAt, c.activityType.code, c.supply.name,
       c.dosageUsed, c.employee.fullName
FROM CultivationLog c
WHERE c.lot.lotCode = :lotCode
ORDER BY c.appliedAt ASC

-- [5.3] Tình hình sâu bệnh của lô (PestReportDAO.getTraceabilityLogs)
-- Lưu ý: trả về lotId thay vì pestName/damagePct theo đúng DAO
-- Trả về: Object[] { lotId, severityCode, employeeName }
SELECT p.lot.id, p.severity.code, p.employee.fullName
FROM PestReport p
WHERE p.lot.lotCode = :lotCode
ORDER BY p.lot.id

-- [5.4] Lịch sử tưới tiêu của lô (IrrigationLogDAO.getTraceabilityLogs)
-- Trả về: Object[] { irrigatedAt, waterAmount, employeeName }
SELECT i.irrigatedAt, i.waterAmount, i.employee.fullName
FROM IrrigationLog i
WHERE i.lot.lotCode = :lotCode
ORDER BY i.irrigatedAt ASC

-- [5.5] Kết quả thu hoạch và khách mua (HarvestRecordDAO.findByLotCode)
SELECT h FROM HarvestRecord h
LEFT JOIN FETCH h.lot
LEFT JOIN FETCH h.customer
LEFT JOIN FETCH h.employee
WHERE h.lot.lotCode = :lotCode
ORDER BY h.harvestDate ASC


-- ══════════════════════════════════════════
-- NHÓM 6: BÁO CÁO TỔNG HỢP
-- ══════════════════════════════════════════

-- [6.1] Tổng quan hoạt động trang trại theo mùa vụ (FarmDAO.getSeasonalSummary)
-- Trả về: Object[] { seasonName, lotCount, totalAreaM2, totalYieldKg }
SELECT l.season.name, COUNT(DISTINCT l.id), SUM(l.areaM2), SUM(h.yieldKg)
FROM ProductionLot l
LEFT JOIN HarvestRecord h ON h.lot.id = l.id
WHERE l.farm.id = :farmId
GROUP BY l.season.id, l.season.name
ORDER BY l.season.startDate DESC

-- [6.2] Top N lô có sản lượng cao nhất (ProductionLotDAO.getTopYieldingLots)
-- setMaxResults(:limit) được set ở tầng DAO
-- Trả về: Object[] { lotCode, cropTypeName, totalYieldKg }
SELECT l.lotCode, l.cropType.name, SUM(h.yieldKg)
FROM HarvestRecord h
JOIN h.lot l
GROUP BY l.id, l.lotCode, l.cropType.name
ORDER BY SUM(h.yieldKg) DESC

-- [6.3] Thống kê số lần sâu bệnh theo mức độ trong mùa vụ (PestReportDAO.getSeverityStatsBySeason)
-- Trả về: Object[] { severityCode, count }
SELECT p.severity.code, COUNT(p.id)
FROM PestReport p
JOIN p.lot l
WHERE l.season.id = :seasonId
GROUP BY p.severity.code
ORDER BY COUNT(p.id) DESC

-- [6.4] Sản lượng theo khách hàng (CustomerDAO.getYieldStatsByCustomer / HarvestRecordDAO.getCustomerYieldStats)
-- Trả về: Object[] { customerName, customerType, totalYieldKg, recordCount }
SELECT h.customer.name, h.customer.type, SUM(h.yieldKg), COUNT(h.id)
FROM HarvestRecord h
WHERE h.customer IS NOT NULL
GROUP BY h.customer.id, h.customer.name, h.customer.type
ORDER BY SUM(h.yieldKg) DESC

-- [6.5] Thống kê ngày mưa theo tháng trong năm (WeatherLogDAO.getMonthlyRainStats)
-- Trả về: Object[] { month, rainyDayCount, avgRainfallMm }
SELECT MONTH(w.weatherDate), COUNT(w.id), AVG(w.rainfallMm)
FROM WeatherLog w
WHERE w.farm.id = :farmId
  AND YEAR(w.weatherDate) = :year
  AND w.rainfallMm > 0
GROUP BY MONTH(w.weatherDate)
ORDER BY MONTH(w.weatherDate)

-- [6.6] Ước tính chi phí vật tư theo lô (SupplyImportDetailDAO.getCostEstimateByLot)
-- Dùng giá nhập kho trung bình làm đơn giá tham chiếu
-- Trả về: Object[] { lotCode, cropTypeName, totalCostEstimate }
SELECT l.lotCode, l.cropType.name,
       SUM(c.dosageUsed * (
           SELECT AVG(d.unitPrice)
           FROM SupplyImportDetail d
           WHERE d.supply.id = c.supply.id
       ))
FROM CultivationLog c
JOIN c.lot l
GROUP BY l.id, l.lotCode, l.cropType.name
ORDER BY 3 DESC


-- ══════════════════════════════════════════
-- NHÓM 7: KIỂM TRA DỮ LIỆU (VALIDATION)
-- Dùng trong Service layer
-- ══════════════════════════════════════════

-- [7.1] Kiểm tra mã lô đã tồn tại chưa (ProductionLotDAO.existsByLotCode)
SELECT COUNT(l.id) FROM ProductionLot l WHERE l.lotCode = :lotCode

-- [7.2] Kiểm tra mã nhân viên đã tồn tại chưa (EmployeeDAO.existsByEmpCode)
SELECT COUNT(e.id) FROM Employee e WHERE e.empCode = :empCode

-- [7.3] Kiểm tra mã vật tư đã tồn tại chưa (AgriSupplyDAO.existsBySupplyCode)
SELECT COUNT(s.id) FROM AgriSupply s WHERE s.supplyCode = :supplyCode

-- [7.4] Kiểm tra lô có đang ở trạng thái cho phép ghi nhật ký không
-- Chỉ PLANTED/GROWING/FLOWERING/FRUITING mới được ghi CultivationLog
-- Lưu ý: status là @ManyToOne sang LotStatus, so sánh qua status.code
SELECT COUNT(l.id) FROM ProductionLot l
WHERE l.id = :lotId
  AND l.status.code IN ('PLANTED', 'GROWING', 'FLOWERING', 'FRUITING')

-- [7.5] Lấy tồn kho hiện tại của vật tư (AgriSupplyDAO.getStockQty)
-- Dùng để validate trước khi ghi CultivationLog
SELECT s.stockQty FROM AgriSupply s WHERE s.id = :supplyId

-- [7.6] Đếm số nhật ký của nhân viên trong ngày hôm nay (EmployeeDAO.countTodayLogs)
SELECT COUNT(c.id) FROM CultivationLog c
WHERE c.employee.id = :employeeId
  AND c.appliedAt >= CURRENT_DATE


-- ══════════════════════════════════════════
-- NHÓM 8: BIỂU ĐỒ THỐNG KÊ (CHART DATA)
-- Dùng cho màn hình Dashboard với JFreeChart
-- ══════════════════════════════════════════

-- [8.1] Tỷ lệ chức vụ nhân viên — PieChart (EmployeeDAO.getRoleDistribution)
-- Lưu ý: role là @ManyToOne sang Role, truy cập qua role.code
-- Trả về: Object[] { roleCode, count }
SELECT e.role.code, COUNT(e.id)
FROM Employee e
GROUP BY e.role.code

-- [8.2] Top 10 nhân viên WORKER theo sản lượng thu hoạch — BarChart (EmployeeDAO.getHarvestPerformance)
-- Trả về: Object[] { fullName, totalYieldKg }
SELECT e.fullName, SUM(h.yieldKg)
FROM HarvestRecord h
JOIN h.employee e
WHERE e.role.code = 'WORKER'
GROUP BY e.id, e.fullName
ORDER BY SUM(h.yieldKg) DESC

-- [8.3] Tỷ lệ mức độ sâu bệnh — PieChart (PestReportDAO.getSeverityDistribution)
-- Trả về: Object[] { severityCode, count }
SELECT p.severity.code, COUNT(p.id)
FROM PestReport p
GROUP BY p.severity.code

-- [8.4] Diễn biến sâu bệnh theo tháng — LineChart (PestReportDAO.getMonthlyTrend)
-- Trả về: Object[] { year, month, count }
SELECT YEAR(p.reportedAt), MONTH(p.reportedAt), COUNT(p.id)
FROM PestReport p
GROUP BY YEAR(p.reportedAt), MONTH(p.reportedAt)
ORDER BY YEAR(p.reportedAt) ASC, MONTH(p.reportedAt) ASC