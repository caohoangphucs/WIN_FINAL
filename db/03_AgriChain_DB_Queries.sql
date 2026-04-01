-- =============================================
-- SMART AGRI-CHAIN — JPQL Queries
-- Sử dụng trong tầng DAO / Repository
-- Entity names: Farm, ProductionLot, CropType,
--   Season, Department, Employee, AgriSupply,
--   Supplier, SupplyImport, SupplyImportDetail,
--   CultivationLog, IrrigationLog, PestReport,
--   WeatherLog, HarvestRecord, Customer
-- =============================================


-- ══════════════════════════════════════════
-- NHÓM 1: CRUD CƠ BẢN
-- Dùng trong tất cả DAO — bắt buộc phải có
-- ══════════════════════════════════════════

-- [1.1] Lấy toàn bộ lô sản xuất, sắp xếp mới nhất
SELECT l FROM ProductionLot l ORDER BY l.plantDate DESC

-- [1.2] Tìm lô theo mã lô (dùng ở ô tìm kiếm)
SELECT l FROM ProductionLot l WHERE l.lotCode = :lotCode

-- [1.3] Tìm lô theo trạng thái (filter trên Farm Grid)
SELECT l FROM ProductionLot l WHERE l.status = :status ORDER BY l.lotCode


-- [1.5] Tìm vật tư theo từ khóa (tên hoặc mã)
SELECT s FROM AgriSupply s
WHERE LOWER(s.name) LIKE LOWER(CONCAT('%', :keyword, '%'))
   OR LOWER(s.supplyCode) LIKE LOWER(CONCAT('%', :keyword, '%'))

-- [1.6] Tìm nhân viên theo tên hoặc mã
SELECT e FROM Employee e
WHERE LOWER(e.fullName) LIKE LOWER(CONCAT('%', :keyword, '%'))
   OR LOWER(e.empCode) LIKE LOWER(CONCAT('%', :keyword, '%'))

-- [1.7] Lấy toàn bộ khách hàng đang hoạt động
SELECT c FROM Customer c WHERE c.status = 'ACTIVE' ORDER BY c.name

-- [1.8] Tìm kiếm lô theo nhiều điều kiện (Dynamic Search)
-- Truyền null để bỏ qua điều kiện đó
SELECT l FROM ProductionLot l
WHERE (:farmId IS NULL OR l.farm.id = :farmId)
  AND (:status IS NULL OR l.status = :status)
  AND (:cropTypeId IS NULL OR l.cropType.id = :cropTypeId)
  AND (:seasonId IS NULL OR l.season.id = :seasonId)
ORDER BY l.plantDate DESC


-- ══════════════════════════════════════════
-- NHÓM 2: THỐNG KÊ NĂNG SUẤT & SẢN LƯỢNG
-- Dùng trên Dashboard — dành cho Manager
-- ══════════════════════════════════════════

-- [2.1] Năng suất trung bình theo loại cây trồng
-- Biết loại cây nào cho năng suất tốt nhất
-- Trả về: Object[] { cropTypeName, avgYieldKg }
SELECT l.cropType.name, AVG(h.yieldKg)
FROM HarvestRecord h
JOIN h.productionLot l
WHERE h.yieldKg IS NOT NULL
GROUP BY l.cropType.name
ORDER BY AVG(h.yieldKg) DESC

-- [2.2] Tổng sản lượng thu hoạch theo mùa vụ
-- So sánh hiệu quả Đông Xuân vs Hè Thu
-- Trả về: Object[] { seasonName, totalYieldKg }
SELECT l.season.name, SUM(h.yieldKg)
FROM HarvestRecord h
JOIN h.productionLot l
WHERE h.yieldKg IS NOT NULL
GROUP BY l.season.name
ORDER BY SUM(h.yieldKg) DESC

-- [2.3] Hiệu suất diện tích — sản lượng trên mỗi m²
-- Tìm lô canh tác hiệu quả nhất
-- Trả về: Object[] { lotCode, yieldPerM2 }
SELECT l.lotCode, SUM(h.yieldKg) / l.areaM2
FROM HarvestRecord h
JOIN h.productionLot l
WHERE h.yieldKg IS NOT NULL
  AND l.areaM2 > 0
GROUP BY l.lotCode, l.areaM2
ORDER BY SUM(h.yieldKg) / l.areaM2 DESC

-- [2.4] Tỉ lệ xếp loại chất lượng nông sản
-- Bao nhiêu % Loại A, B, C trong vụ này
-- Trả về: Object[] { qualityGrade, count }
SELECT h.qualityGrade, COUNT(h.id)
FROM HarvestRecord h
WHERE h.qualityGrade IS NOT NULL
GROUP BY h.qualityGrade
ORDER BY h.qualityGrade

-- [2.5] Tổng sản lượng thu hoạch theo từng trang trại
-- Trả về: Object[] { farmName, totalYieldKg }
SELECT l.farm.name, SUM(h.yieldKg)
FROM HarvestRecord h
JOIN h.productionLot l
WHERE h.yieldKg IS NOT NULL
GROUP BY l.farm.name
ORDER BY SUM(h.yieldKg) DESC

-- [2.6] Danh sách lô sắp đến ngày thu hoạch (trong 30 ngày tới)
-- Cảnh báo nhắc nhở trên Dashboard
SELECT l FROM ProductionLot l
WHERE l.expectedHarvestDate BETWEEN CURRENT_DATE AND :nextMonth
  AND l.status != 'HARVESTED'
  AND l.status != 'IDLE'
ORDER BY l.expectedHarvestDate ASC


-- ══════════════════════════════════════════
-- NHÓM 3: QUẢN LÝ KHO VẬT TƯ
-- Dùng cho màn hình Kho — dành cho Thủ kho
-- ══════════════════════════════════════════

-- [3.1] Cảnh báo vật tư sắp hết hàng
-- stock_qty <= min_stock → cần nhập thêm
SELECT s FROM AgriSupply s
WHERE s.stockQty <= s.minStock AND s.minStock > 0
ORDER BY (s.stockQty / s.minStock) ASC

-- [3.2] Tổng lượng vật tư đã tiêu thụ theo loại
-- Biết loại nào dùng nhiều nhất trong vụ
-- Trả về: Object[] { supplyType, totalDosage }
SELECT s.category.name, SUM(c.dosageUsed)
FROM CultivationLog c
JOIN c.supply s
GROUP BY s.category.name
ORDER BY SUM(c.dosageUsed) DESC

-- [3.3] Tổng chi phí nhập kho theo nhà cung cấp
-- Nhà cung cấp nào là đối tác lớn nhất
-- Trả về: Object[] { supplierName, totalAmount }
SELECT d.supply.supplier.name, SUM(d.quantity * d.unitPrice)
FROM SupplyImportDetail d
GROUP BY d.supply.supplier.name
ORDER BY SUM(d.quantity * d.unitPrice) DESC

-- [3.4] Lịch sử nhập kho của một vật tư cụ thể
-- Truy vết nguồn gốc vật tư
SELECT si FROM SupplyImport si
JOIN si.details d
WHERE d.supply.id = :supplyId
ORDER BY si.importDate DESC

-- [3.5] Tổng lượng vật tư đã dùng cho một lô cụ thể
-- Tính chi phí đầu vào của một lô canh tác
-- Trả về: Object[] { supplyName, unit, totalDosage }
SELECT c.supply.name, SUM(c.dosageUsed)
FROM CultivationLog c
WHERE c.productionLot.id = :lotId
GROUP BY c.supply.name
ORDER BY SUM(c.dosageUsed) DESC

-- [3.6] Chi tiết từng lần bón phân/phun thuốc cho một lô
-- Dùng để tính toán chi phí theo từng hoạt động
-- Trả về: Object[] { activityType, supplyName, dosageUsed, unit, appliedAt }
SELECT c.activityType.code, c.supply.name, c.dosageUsed, c.appliedAt
FROM CultivationLog c
WHERE c.productionLot.id = :lotId
ORDER BY c.appliedAt DESC


-- ══════════════════════════════════════════
-- NHÓM 4: GIÁM SÁT CANH TÁC
-- Dùng cho màn hình Nhật ký — dành cho Kỹ thuật
-- ══════════════════════════════════════════

-- [4.1] Lịch sử bón phân/phun thuốc theo lô và khoảng thời gian
-- Phục vụ truy xuất nguồn gốc VietGAP
SELECT c FROM CultivationLog c
WHERE c.productionLot.id = :lotId
  AND c.appliedAt BETWEEN :startDate AND :endDate
ORDER BY c.appliedAt DESC

-- [4.2] Tìm các lô đang có cảnh báo sâu bệnh nghiêm trọng
-- Ưu tiên xử lý khẩn cấp — màu đỏ trên Farm Grid
SELECT p FROM PestReport p
WHERE p.severity.code IN ('HIGH', 'CRITICAL')
AND p.productionLot.status.code != 'HARVESTED'
ORDER BY p.reportedAt DESC

-- [4.3] Số lần phun thuốc theo loại hoạt động của một lô
-- Đánh giá mức độ chăm sóc của lô đó
-- Trả về: Object[] { activityType, count }
SELECT c.activityType.code, COUNT(c.id)
FROM CultivationLog c
WHERE c.productionLot.id = :lotId
GROUP BY c.activityType.code

-- [4.4] Tổng lượng nước tưới theo tháng cho một lô
-- Theo dõi chế độ tưới tiêu
-- Trả về: Object[] { month, totalWater }
SELECT MONTH(i.irrigatedAt), SUM(i.waterAmount)
FROM IrrigationLog i
WHERE i.productionLot.id = :lotId
  AND YEAR(i.irrigatedAt) = :year
GROUP BY MONTH(i.irrigatedAt)
ORDER BY MONTH(i.irrigatedAt)

-- [4.5] Nhân viên thực hiện nhiều hoạt động nhất trong tháng
-- Đánh giá hiệu suất làm việc
-- Trả về: Object[] { fullName, activityCount }
SELECT c.employee.fullName, COUNT(c.id)
FROM CultivationLog c
WHERE MONTH(c.appliedAt) = :month
  AND YEAR(c.appliedAt) = :year
GROUP BY c.employee.id, c.employee.fullName
ORDER BY COUNT(c.id) DESC


-- ══════════════════════════════════════════
-- NHÓM 5: TRUY XUẤT NGUỒN GỐC (TRACEABILITY)
-- Đây là tính năng đặc trưng của Agri-Chain
-- Nhập mã lô → ra toàn bộ lịch sử
-- ══════════════════════════════════════════

-- [5.1] Thông tin đầy đủ của một lô theo mã lô
-- Bước 1 trong truy xuất nguồn gốc
SELECT l FROM ProductionLot l
JOIN FETCH l.farm
JOIN FETCH l.cropType
JOIN FETCH l.season
JOIN FETCH l.manager
WHERE l.lotCode = :lotCode

-- [5.2] Toàn bộ vật tư đã dùng cho lô — ai bón, khi nào, bao nhiêu
-- Bước 2 — danh sách vật tư đầu vào
SELECT c.appliedAt, c.activityType.code, c.supply.name,
       c.dosageUsed,
       c.employee.fullName
FROM CultivationLog c
WHERE c.productionLot.lotCode = :lotCode
ORDER BY c.appliedAt ASC

-- [5.3] Tình hình sâu bệnh của lô
-- Bước 3 — rủi ro trong quá trình canh tác
SELECT p.reportedAt, p.pestName, p.severity.code,
       p.damagePct, p.treatment, p.employee.fullName
FROM PestReport p
WHERE p.productionLot.lotCode = :lotCode
ORDER BY p.reportedAt ASC

-- [5.4] Lịch sử tưới tiêu của lô
-- Bước 4 — chế độ nước
SELECT i.irrigatedAt, i.waterAmount,
       i.source, i.durationMin, i.employee.fullName
FROM IrrigationLog i
WHERE i.productionLot.lotCode = :lotCode
ORDER BY i.irrigatedAt ASC

-- [5.5] Kết quả thu hoạch và khách mua
-- Bước 5 — đầu ra của lô
SELECT h.harvestDate, h.yieldKg, h.qualityGrade.code,
       h.customer.name, h.customer.type
FROM HarvestRecord h
WHERE h.productionLot.lotCode = :lotCode


-- ══════════════════════════════════════════
-- NHÓM 6: BÁO CÁO TỔNG HỢP
-- Dùng cho màn hình Báo cáo
-- ══════════════════════════════════════════

-- [6.1] Tổng quan hoạt động của trang trại trong một mùa vụ
-- Số lô, tổng diện tích, tổng sản lượng
-- Trả về: Object[] { seasonName, lotCount, totalArea, totalYield }
SELECT l.season.name,
       COUNT(DISTINCT l.id),
       SUM(l.areaM2),
       SUM(h.yieldKg)
FROM ProductionLot l
LEFT JOIN l.harvestRecords h
WHERE l.farm.id = :farmId
GROUP BY l.season.name
ORDER BY l.season.startDate DESC

-- [6.2] Top 5 lô có sản lượng cao nhất
SELECT l.lotCode, l.cropType.name, SUM(h.yieldKg)
FROM HarvestRecord h
JOIN h.productionLot l
GROUP BY l.id, l.lotCode, l.cropType.name
ORDER BY SUM(h.yieldKg) DESC

-- [6.3] Thống kê số lần sâu bệnh theo mức độ trong một mùa vụ
-- Trả về: Object[] { severity, count }
SELECT p.severity.code, COUNT(p.id)
FROM PestReport p
JOIN p.productionLot l
WHERE l.season.id = :seasonId
GROUP BY p.severity.code
ORDER BY COUNT(p.id) DESC

-- [6.4] Doanh thu theo khách hàng (tính từ sản lượng * đơn giá thị trường)
-- Khách hàng nào mua nhiều nhất
-- Trả về: Object[] { customerName, customerType, totalYieldKg, recordCount }
SELECT h.customer.name, h.customer.type,
       SUM(h.yieldKg), COUNT(h.id)
FROM HarvestRecord h
WHERE h.customer IS NOT NULL
GROUP BY h.customer.id, h.customer.name, h.customer.type
ORDER BY SUM(h.yieldKg) DESC

-- [6.5] Thời tiết ảnh hưởng đến canh tác — ngày mưa trong tháng
-- Phục vụ phân tích tương quan thời tiết - năng suất
-- Trả về: Object[] { month, rainyDays, avgRainfall }
SELECT MONTH(w.weatherDate), COUNT(w.id), AVG(w.rainfallMm)
FROM WeatherLog w
WHERE w.farm.id = :farmId
  AND YEAR(w.weatherDate) = :year
GROUP BY MONTH(w.weatherDate)
ORDER BY MONTH(w.weatherDate)

-- [6.6] Tổng chi phí vật tư theo lô tính lợi nhuận (lấy giá vật tư trung bình mới và cũ)
-- Trả về: Object[] { lotCode, cropName, totalCostEstimate }
SELECT l.lotCode, l.cropType.name,
       SUM(c.dosageUsed * (
           SELECT AVG(d.unitPrice)
           FROM SupplyImportDetail d
           WHERE d.supply.id = c.supply.id
       ))
FROM CultivationLog c
JOIN c.productionLot l
GROUP BY l.id, l.lotCode, l.cropType.name
ORDER BY SUM(c.dosageUsed * (
           SELECT AVG(d.unitPrice)
           FROM SupplyImportDetail d
           WHERE d.supply.id = c.supply.id
       )) DESC

-- ══════════════════════════════════════════
-- NHÓM 7: KIỂM TRA DỮ LIỆU (VALIDATION)
-- Dùng trong Service layer để validate
-- ══════════════════════════════════════════

-- [7.1] Kiểm tra mã lô đã tồn tại chưa (trước khi INSERT)
SELECT COUNT(l.id) FROM ProductionLot l WHERE l.lotCode = :lotCode

-- [7.2] Kiểm tra mã nhân viên đã tồn tại chưa
SELECT COUNT(e.id) FROM Employee e WHERE e.empCode = :empCode

-- [7.3] Kiểm tra mã vật tư đã tồn tại chưa
SELECT COUNT(s.id) FROM AgriSupply s WHERE s.supplyCode = :supplyCode

-- [7.4] Kiểm tra lô có đang ở trạng thái cho phép bón phân không
-- Chỉ các lô GROWING/FLOWERING/FRUITING mới được ghi nhật ký
SELECT COUNT(l.id) FROM ProductionLot l
WHERE l.id = :lotId
AND l.status.code IN ('PLANTED', 'GROWING', 'FLOWERING', 'FRUITING')

-- [7.5] Kiểm tra tồn kho đủ để bón không (trước khi ghi CultivationLog)
SELECT s.stockQty FROM AgriSupply s WHERE s.id = :supplyId

-- [7.6] Đếm số nhật ký của một nhân viên trong ngày hôm nay
-- Kiểm soát việc nhập liệu hàng ngày
SELECT COUNT(c.id) FROM CultivationLog c
WHERE c.employee.id = :employeeId
  AND c.appliedAt >= CURRENT_DATE


-- ══════════════════════════════════════════
-- GHI CHÚ SỬ DỤNG TRONG JAVA
-- ══════════════════════════════════════════
--
-- Cách dùng trong DAO với EntityManager:
--
-- Ví dụ [3.1] — Cảnh báo vật tư sắp hết:
--
--   public List<AgriSupply> findLowStock() {
--       return em.createQuery(
--           "SELECT s FROM AgriSupply s " +
--           "WHERE s.stockQty <= s.minStock " +
--           "ORDER BY (s.stockQty / s.minStock) ASC",
--           AgriSupply.class
--       ).getResultList();
--   }
--
-- Ví dụ [1.8] — Dynamic Search với tham số nullable:
--
--   public List<ProductionLot> search(Long farmId, String status,
--                                     Long cropTypeId, Long seasonId) {
--       return em.createQuery(
--           "SELECT l FROM ProductionLot l " +
--           "WHERE (:farmId IS NULL OR l.farm.id = :farmId) " +
--           "  AND (:status IS NULL OR l.status = :status) " +
--           "  AND (:cropTypeId IS NULL OR l.cropType.id = :cropTypeId) " +
--           "  AND (:seasonId IS NULL OR l.season.id = :seasonId) " +
--           "ORDER BY l.plantDate DESC",
--           ProductionLot.class
--       )
--       .setParameter("farmId", farmId)
--       .setParameter("status", status)
--       .setParameter("cropTypeId", cropTypeId)
--       .setParameter("seasonId", seasonId)
--       .getResultList();
--   }
--
-- Ví dụ [2.1] — Thống kê trả về Object[]:
--
--   public List<Object[]> avgYieldByCropType() {
--       return em.createQuery(
--           "SELECT l.cropType.name, AVG(h.yieldKg) " +
--           "FROM HarvestRecord h JOIN h.productionLot l " +
--           "WHERE h.yieldKg IS NOT NULL " +
--           "GROUP BY l.cropType.name " +
--           "ORDER BY AVG(h.yieldKg) DESC"
--       ).getResultList();
--   }
--
-- Cập nhật tồn kho sau khi ghi CultivationLog (@Transactional):
--
--   em.createQuery(
--       "UPDATE AgriSupply s " +
--       "SET s.stockQty = s.stockQty - :dosage, " +
--       "    s.updatedAt = CURRENT_TIMESTAMP " +
--       "WHERE s.id = :supplyId"
--   )
--   .setParameter("dosage", dosage)
--   .setParameter("supplyId", supplyId)
--   .executeUpdate();
--
-- ══════════════════════════════════════════
