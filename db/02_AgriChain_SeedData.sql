-- =============================================================================
-- Dự án: AgriChain - Hệ thống Quản lý Chuỗi Cung ứng Nông nghiệp
-- Phát triển bởi: AriOps Team
-- Phiên bản: 2.0 (Dữ liệu mẫu chuẩn hóa)
-- Mô tả: File khởi tạo dữ liệu mẫu (Seed Data) cho toàn bộ hệ thống
-- Ghi chú: Cần chạy file [01_AgriChain_Schema_v2.sql] trước khi chạy file này
-- =============================================================================

USE agri_chain;
SET NAMES 'utf8mb4';

-- ══════════════════════════════════════════
-- [1] CROP CATEGORY (thay thế ENUM category)
-- ══════════════════════════════════════════

INSERT INTO crop_category (id, name, parent_id) VALUES
(1, 'Cây lương thực',   NULL),
(2, 'Cây ăn quả',       NULL),
(3, 'Cây rau màu',      NULL),
(4, 'Cây công nghiệp',  NULL),
(5, 'Lúa',              1),
(6, 'Bắp/Ngô',          1),
(7, 'Dưa các loại',     2),
(8, 'Cà chua',          3),
(9, 'Rau ăn lá',        3),
(10,'Cà phê',           4);

-- ══════════════════════════════════════════
-- [2] CROP TYPE
-- ══════════════════════════════════════════

INSERT INTO crop_type (crop_code, name, category_id, growth_days) VALUES
('CT001', 'Lúa IR50404',      5,  90),
('CT002', 'Lúa OM5451',       5,  95),
('CT003', 'Dưa lưới Taki F1', 7,  65),
('CT004', 'Cà chua Cherry',   8,  75),
('CT005', 'Cà phê Arabica',   10, 365),
('CT006', 'Cà phê Robusta',   10, 365),
('CT007', 'Bắp lai NK66',     6,  110),
('CT008', 'Rau cải ngọt',     9,  30);

-- ══════════════════════════════════════════
-- [3] SEASON
-- ══════════════════════════════════════════

INSERT INTO season (season_code, name, start_date, end_date) VALUES
('S2024A', 'Đông Xuân 2023-2024', '2023-11-01', '2024-03-31'),
('S2024B', 'Hè Thu 2024',         '2024-04-01', '2024-08-31'),
('S2024C', 'Thu Đông 2024',       '2024-09-01', '2025-01-31'),
('S2025A', 'Đông Xuân 2024-2025', '2024-11-01', '2025-03-31');

-- ══════════════════════════════════════════
-- [4] SUPPLY CATEGORY
-- ══════════════════════════════════════════

INSERT INTO supply_category (id, name) VALUES
(1, 'Phân bón'),
(2, 'Thuốc bảo vệ thực vật'),
(3, 'Hạt giống'),
(4, 'Vật tư nông nghiệp');

-- ══════════════════════════════════════════
-- [5] FARM
-- ══════════════════════════════════════════

INSERT INTO farm (farm_code, name, address, total_area, owner_name, phone,
                  created_at, created_by, updated_at, updated_by)
VALUES
('F001', 'Trang trại Xanh Việt',      'Xã Ea Ktur, Cư Kuin, Đắk Lắk',      15000.0, 'Nguyễn Văn Hùng', '0901234567', '2023-01-10 08:00:00', 'admin', '2023-01-10 08:00:00', 'admin'),
('F002', 'Nông trại Mekong Fresh',    'Xã Thới Sơn, Cái Bè, Tiền Giang',   22000.0, 'Trần Thị Mai',    '0912345678', '2023-03-15 09:00:00', 'admin', '2023-03-15 09:00:00', 'admin'),
('F003', 'HTX Nông nghiệp Lâm Đồng', 'Phường 7, Đà Lạt, Lâm Đồng',        8500.0,  'Lê Văn Phúc',     '0923456789', '2023-06-01 07:30:00', 'admin', '2023-06-01 07:30:00', 'admin');

-- ══════════════════════════════════════════
-- [6] DEPARTMENT
-- ══════════════════════════════════════════

INSERT INTO department (dept_code, name, farm_id) VALUES
('D001', 'Tổ Kỹ thuật',   1),
('D002', 'Tổ Kho vật tư', 1),
('D003', 'Tổ Chăm sóc',   1),
('D004', 'Tổ Kỹ thuật',   2),
('D005', 'Tổ Vận hành',   2),
('D006', 'Ban Quản lý',   3),
('D007', 'Tổ Sản xuất',   3);

-- ══════════════════════════════════════════
-- [7] EMPLOYEE  (role_code → FK → role)
-- ══════════════════════════════════════════

INSERT INTO employee (emp_code, full_name, phone, email, role_code, hire_date, department_id) VALUES
('E001', 'Nguyễn Văn An',  '0901111111', 'an.nv@agrichain.vn',     'MANAGER',    '2023-01-15', 1),
('E002', 'Trần Thị Bình',  '0902222222', 'binh.tt@agrichain.vn',   'TECHNICIAN', '2023-02-01', 1),
('E003', 'Lê Văn Cường',   '0903333333', 'cuong.lv@agrichain.vn',  'WORKER',     '2023-02-15', 3),
('E004', 'Phạm Thị Dung',  '0904444444', 'dung.pt@agrichain.vn',   'WORKER',     '2023-03-01', 3),
('E005', 'Hoàng Văn Em',   '0905555555', 'em.hv@agrichain.vn',     'TECHNICIAN', '2023-03-10', 2),
('E006', 'Võ Thị Phương',  '0906666666', 'phuong.vt@agrichain.vn', 'MANAGER',    '2023-04-01', 4),
('E007', 'Đặng Văn Giang', '0907777777', 'giang.dv@agrichain.vn',  'TECHNICIAN', '2023-04-15', 4),
('E008', 'Bùi Thị Hoa',   '0908888888', 'hoa.bt@agrichain.vn',    'WORKER',     '2023-05-01', 5),
('E009', 'Ngô Văn Inh',   '0909999999', 'inh.nv@agrichain.vn',    'MANAGER',    '2023-06-01', 6),
('E010', 'Đinh Thị Kim',  '0910101010', 'kim.dt@agrichain.vn',    'WORKER',     '2023-06-15', 7);

-- ══════════════════════════════════════════
-- [8] PRODUCTION LOT  (status_code → FK → lot_status)
-- Lưu ý: 'FRUITING' và 'PLANTED' không có trong lot_status.
-- Dùng các giá trị hợp lệ: PLANTED, GROWING, FLOWERING, HARVESTED, IDLE
-- ══════════════════════════════════════════

INSERT INTO production_lot
    (lot_code, area_m2, status_code, location_desc,
     plant_date, expected_harvest_date, actual_harvest_date,
     farm_id, crop_type_id, season_id, manager_id)
VALUES
('LOT-S1', 1200.0,  'FLOWERING', 'Nhà màng số 1, khu A',        '2025-01-12', '2025-03-18', NULL,         1, 3, 4, 1),
('LOT-S2', 1500.0,  'GROWING',   'Nhà màng số 2, khu A',        '2025-01-20', '2025-03-26', NULL,         1, 3, 4, 1),
('LOT-S3', 800.0,   'FLOWERING', 'Nhà kính khu B',              '2025-01-08', '2025-03-24', NULL,         1, 4, 4, 2),
('LOT-C1', 20000.0, 'GROWING',   'Lô cà phê phía Đông',         '2024-06-01', '2025-06-01', NULL,         1, 5, 2, 1),
('LOT-C2', 18000.0, 'GROWING',   'Lô cà phê phía Tây',          '2024-06-15', '2025-06-15', NULL,         1, 6, 2, 2),
('LOT-L1', 30000.0, 'PLANTED',   'Cánh đồng lúa khu 1',         '2025-01-25', '2025-04-25', NULL,         2, 1, 4, 6),
('LOT-L2', 28000.0, 'GROWING',   'Cánh đồng lúa khu 2',         '2024-11-05', '2025-02-10', NULL,         2, 2, 1, 6),
('LOT-L3', 25000.0, 'HARVESTED', 'Cánh đồng lúa khu 3',         '2024-08-01', '2024-11-01', '2024-10-28', 2, 1, 2, 6),
('LOT-R1', 500.0,   'GROWING',   'Vườn rau nhà màng Đà Lạt 1',  '2025-02-01', '2025-03-03', NULL,         3, 8, 4, 9),
('LOT-R2', 450.0,   'HARVESTED', 'Vườn rau nhà màng Đà Lạt 2',  '2025-01-01', '2025-01-31', '2025-01-28', 3, 8, 3, 9),
('LOT-B1', 7000.0,  'GROWING',   'Lô bắp lai khu C',            '2025-01-15', '2025-05-05', NULL,         2, 7, 4, 7),
('LOT-S4', 1100.0,  'IDLE',      'Nhà màng số 3 đang cải tạo',  '2024-10-01', '2024-12-10', NULL,         1, 3, 3, 1);

-- ══════════════════════════════════════════
-- [9] SUPPLIER
-- ══════════════════════════════════════════

INSERT INTO supplier (supplier_code, name, phone) VALUES
('SUP001', 'Công ty CP Phân bón Bình Điền',  '02513991999'),
('SUP002', 'Công ty TNHH Syngenta Việt Nam', '02838405557'),
('SUP003', 'Công ty CP Giống cây trồng TW',  '02438223451'),
('SUP004', 'Công ty TNHH Trang Nông',        '02838359999'),
('SUP005', 'Công ty CP Thuốc BVTV An Giang', '02963955031');

-- ══════════════════════════════════════════
-- [10] AGRI SUPPLY
-- ══════════════════════════════════════════

INSERT INTO agri_supply (supply_code, name, category_id, unit, stock_qty, min_stock, supplier_id) VALUES
('AS001', 'Phân NPK 16-16-8 Đầu Trâu',    1, 'kg',  180.0, 50.0,  1),
('AS002', 'Phân DAP 18-46',               1, 'kg',  250.0, 80.0,  1),
('AS003', 'Phân Kali Clorua (KCl)',        1, 'kg',  120.0, 40.0,  1),
('AS004', 'Phân bón lá Đầu Trâu 701',     1, 'lit', 45.0,  15.0,  1),
('AS005', 'Thuốc trừ sâu Sherpa 25EC',    2, 'lit', 28.0,  10.0,  2),
('AS006', 'Thuốc trừ bệnh Mancozeb 80WP', 2, 'kg',  35.0,  10.0,  2),
('AS007', 'Thuốc trừ cỏ Sofit 300EC',     2, 'lit', 15.0,  5.0,   5),
('AS008', 'Hạt giống Dưa lưới Taki F1',   3, 'gói', 20.0,  5.0,   3),
('AS009', 'Hạt giống Cà chua Cherry TN',  3, 'gói', 15.0,  5.0,   3),
('AS010', 'Hạt giống Lúa OM5451',         3, 'kg',  300.0, 100.0, 3),
('AS011', 'Phân hữu cơ vi sinh Sông Gianh',1,'kg',  500.0, 100.0, 4),
('AS012', 'Thuốc nấm Ridomil Gold 68WP',  2, 'kg',  18.0,  5.0,   2);

-- ══════════════════════════════════════════
-- [11] SUPPLY IMPORT
-- ══════════════════════════════════════════

INSERT INTO supply_import (import_code, supplier_id, employee_id, import_date) VALUES
('IMP-001', 1, 5, '2024-12-01 09:00:00'),
('IMP-002', 2, 5, '2024-12-05 10:00:00'),
('IMP-003', 3, 5, '2024-12-10 08:30:00'),
('IMP-004', 1, 5, '2025-01-05 09:00:00'),
('IMP-005', 5, 5, '2025-01-15 14:00:00'),
('IMP-006', 4, 5, '2025-02-01 08:00:00');

-- ══════════════════════════════════════════
-- [12] SUPPLY IMPORT DETAIL  (unit_price → DECIMAL)
-- ══════════════════════════════════════════

INSERT INTO supply_import_detail (import_id, supply_id, quantity, unit_price) VALUES
-- IMP-001: Phân bón đầu vụ Đông Xuân
(1, 1,  200.0, 18000.00),   -- NPK 16-16-8: 200kg × 18.000đ
(1, 2,  150.0, 22000.00),   -- DAP 18-46:   150kg × 22.000đ
(1, 3,  100.0, 15000.00),   -- KCl:          100kg × 15.000đ

-- IMP-002: Thuốc BVTV dự phòng
(2, 5,  20.0,  185000.00),  -- Sherpa 25EC:   20lit × 185.000đ
(2, 6,  25.0,  120000.00),  -- Mancozeb 80WP: 25kg  × 120.000đ
(2, 12, 10.0,  205000.00),  -- Ridomil Gold:  10kg  × 205.000đ

-- IMP-003: Hạt giống vụ mới
(3, 8,  10.0,  280000.00),  -- Hạt dưa lưới: 10 gói × 280.000đ
(3, 9,  8.0,   150000.00),  -- Hạt cà chua:   8 gói × 150.000đ
(3, 10, 200.0, 12000.00),   -- Hạt lúa OM:  200kg  × 12.000đ

-- IMP-004: Bổ sung phân bón giữa vụ
(4, 1,  200.0, 18500.00),   -- NPK bổ sung: 200kg × 18.500đ
(4, 4,  50.0,  42000.00),   -- Phân bón lá:  50lit × 42.000đ

-- IMP-005: Thuốc trừ cỏ & thuốc đặc trị nấm
(5, 7,  10.0,  180000.00),  -- Sofit 300EC:  10lit × 180.000đ
(5, 12, 8.0,   205000.00),  -- Ridomil bổ sung: 8kg × 205.000đ

-- IMP-006: Phân hữu cơ cải tạo đất
(6, 11, 500.0, 14400.00);   -- Phân hữu cơ: 500kg × 14.400đ

-- ══════════════════════════════════════════
-- [13] STOCK TRANSACTION (IMPORT)
-- Tự động ghi nhận khi nhập kho
-- ══════════════════════════════════════════

INSERT INTO stock_transaction (supply_id, type, quantity, ref_id, created_at) VALUES
-- IMP-001
(1,  'IMPORT', 200.0, 1, '2024-12-01 09:30:00'),
(2,  'IMPORT', 150.0, 1, '2024-12-01 09:30:00'),
(3,  'IMPORT', 100.0, 1, '2024-12-01 09:30:00'),
-- IMP-002
(5,  'IMPORT', 20.0,  2, '2024-12-05 10:30:00'),
(6,  'IMPORT', 25.0,  2, '2024-12-05 10:30:00'),
(12, 'IMPORT', 10.0,  2, '2024-12-05 10:30:00'),
-- IMP-003
(8,  'IMPORT', 10.0,  3, '2024-12-10 09:00:00'),
(9,  'IMPORT', 8.0,   3, '2024-12-10 09:00:00'),
(10, 'IMPORT', 200.0, 3, '2024-12-10 09:00:00'),
-- IMP-004
(1,  'IMPORT', 200.0, 4, '2025-01-05 09:30:00'),
(4,  'IMPORT', 50.0,  4, '2025-01-05 09:30:00'),
-- IMP-005
(7,  'IMPORT', 10.0,  5, '2025-01-15 14:30:00'),
(12, 'IMPORT', 8.0,   5, '2025-01-15 14:30:00'),
-- IMP-006
(11, 'IMPORT', 500.0, 6, '2025-02-01 08:30:00');

-- ══════════════════════════════════════════
-- [14] CULTIVATION LOG
-- activity_type_code → FK → activity_type: FERTILIZE | PESTICIDE | FUNGICIDE | FOLIAR
-- ══════════════════════════════════════════

INSERT INTO cultivation_log (lot_id, supply_id, employee_id, activity_type_code, applied_at, dosage_used) VALUES
-- ── LOT-S1: Dưa lưới nhà màng số 1 ──
(1,  2,  2,  'FERTILIZE', '2025-01-14 07:30:00', 2.5),   -- Bón lót DAP trước trồng
(1,  1,  3,  'FERTILIZE', '2025-01-28 08:00:00', 1.8),   -- Bón thúc NPK sau 14 ngày
(1,  4,  2,  'FOLIAR',    '2025-02-05 06:30:00', 0.05),  -- Phân bón lá kích thích ra hoa
(1,  6,  2,  'FUNGICIDE', '2025-02-10 07:00:00', 0.03),  -- Phòng ngừa nấm phấn trắng
(1,  3,  3,  'FERTILIZE', '2025-02-18 08:00:00', 2.0),   -- Bón kali tăng chất lượng quả
(1,  4,  3,  'FOLIAR',    '2025-02-25 06:30:00', 0.05),  -- Phân bón lá tăng trọng lượng quả
(1,  12, 2,  'FUNGICIDE', '2025-03-03 07:00:00', 0.025), -- Xử lý nấm trên thân lá

-- ── LOT-S2: Dưa lưới nhà màng số 2 ──
(2,  2,  3,  'FERTILIZE', '2025-01-22 07:30:00', 3.0),   -- Bón lót DAP
(2,  1,  3,  'FERTILIZE', '2025-02-05 08:00:00', 2.0),   -- Bón thúc NPK lần 1
(2,  4,  2,  'FOLIAR',    '2025-02-15 06:30:00', 0.06),  -- Kích thích sinh trưởng
(2,  5,  2,  'PESTICIDE', '2025-02-20 07:00:00', 0.04),  -- Xử lý bọ trĩ trên lá non
(2,  1,  3,  'FERTILIZE', '2025-02-28 08:00:00', 2.5),   -- Bón thúc trước ra hoa

-- ── LOT-S3: Cà chua Cherry ──
(3,  2,  3,  'FERTILIZE', '2025-01-10 07:30:00', 1.5),   -- Bón lót cho cà chua
(3,  6,  2,  'FUNGICIDE', '2025-01-20 07:00:00', 0.02),  -- Phòng nấm cây con
(3,  1,  3,  'FERTILIZE', '2025-02-01 08:00:00', 1.2),   -- Bón thúc NPK lần 1
(3,  4,  2,  'FOLIAR',    '2025-02-10 06:30:00', 0.04),  -- Phun vi lượng tăng đề kháng
(3,  5,  2,  'PESTICIDE', '2025-02-18 07:00:00', 0.03),  -- Trừ sâu đục trái giai đoạn ra hoa
(3,  3,  3,  'FERTILIZE', '2025-02-25 08:00:00', 1.8),   -- Bón kali hỗ trợ đậu quả

-- ── LOT-C1: Cà phê Arabica ──
(4,  1,  2,  'FERTILIZE', '2024-07-01 07:00:00', 15.0),  -- NPK đầu mùa mưa thúc cành
(4,  3,  2,  'FERTILIZE', '2024-09-01 07:00:00', 10.0),  -- Kali trước ra hoa
(4,  6,  2,  'FUNGICIDE', '2024-10-01 07:00:00', 1.2),   -- Phòng gỉ sắt trên lá
(4,  4,  3,  'FOLIAR',    '2024-11-01 07:00:00', 0.2),   -- Vi lượng tăng đậu quả
(4,  11, 3,  'FERTILIZE', '2024-12-01 07:00:00', 12.0),  -- Bón hữu cơ nuôi quả

-- ── LOT-C2: Cà phê Robusta ──
(5,  1,  2,  'FERTILIZE', '2024-07-15 07:00:00', 18.0),  -- NPK đầu vụ
(5,  11, 3,  'FERTILIZE', '2024-09-15 07:00:00', 15.0),  -- Hữu cơ bổ sung
(5,  6,  2,  'FUNGICIDE', '2024-10-20 07:00:00', 1.0),   -- Phòng nấm
(5,  3,  3,  'FERTILIZE', '2024-11-15 07:00:00', 8.0),   -- Kali nuôi quả

-- ── LOT-L2: Lúa OM5451 ──
(7,  2,  7,  'FERTILIZE', '2024-11-08 06:00:00', 8.0),   -- Bón lót DAP trước sạ
(7,  1,  8,  'FERTILIZE', '2024-11-22 06:00:00', 12.0),  -- Bón thúc đẻ nhánh NPK
(7,  5,  7,  'PESTICIDE', '2024-12-01 06:30:00', 0.5),   -- Trừ sâu cuốn lá lần 1
(7,  6,  7,  'FUNGICIDE', '2024-12-10 06:00:00', 0.8),   -- Phòng đạo ôn giai đoạn đứng cái
(7,  1,  8,  'FERTILIZE', '2024-12-20 06:00:00', 6.0),   -- Bón đón đòng NPK
(7,  5,  7,  'PESTICIDE', '2025-01-05 06:30:00', 0.6),   -- Trừ sâu đục thân giai đoạn trổ

-- ── LOT-L3: Lúa IR50404 (đã thu hoạch) ──
(8,  2,  7,  'FERTILIZE', '2024-08-05 06:00:00', 7.0),   -- Bón lót đầu vụ
(8,  1,  8,  'FERTILIZE', '2024-08-20 06:00:00', 10.0),  -- Bón thúc NPK
(8,  5,  7,  'PESTICIDE', '2024-09-01 06:00:00', 0.4),   -- Phun sâu lần 1
(8,  6,  7,  'FUNGICIDE', '2024-09-15 06:00:00', 0.6),   -- Phòng bệnh khô vằn
(8,  7,  8,  'PESTICIDE', '2024-08-10 06:00:00', 0.3),   -- Phun trừ cỏ sớm

-- ── LOT-B1: Bắp lai NK66 ──
(11, 2,  7,  'FERTILIZE', '2025-01-18 07:00:00', 5.0),   -- Bón lót DAP
(11, 1,  8,  'FERTILIZE', '2025-02-05 07:00:00', 8.0),   -- Bón thúc NPK
(11, 5,  7,  'PESTICIDE', '2025-02-20 06:30:00', 0.3),   -- Trừ sâu đục thân bắp
(11, 3,  8,  'FERTILIZE', '2025-03-01 07:00:00', 4.0),   -- Bón kali trước trổ cờ

-- ── LOT-R1: Rau cải Đà Lạt 1 ──
(9,  2,  10, 'FERTILIZE', '2025-02-03 07:00:00', 0.5),   -- Bón lót trước trồng rau
(9,  1,  10, 'FERTILIZE', '2025-02-10 07:00:00', 0.8),   -- Bón thúc NPK khi rau 7 ngày
(9,  4,  10, 'FOLIAR',    '2025-02-17 06:30:00', 0.02),  -- Phun kích thích tăng trưởng lá

-- ── LOT-R2: Rau cải Đà Lạt 2 (đã thu hoạch) ──
(10, 2,  10, 'FERTILIZE', '2025-01-03 07:00:00', 0.4),   -- Bón lót đầu vụ
(10, 1,  10, 'FERTILIZE', '2025-01-12 07:00:00', 0.6),   -- Bón thúc lần 1
(10, 4,  10, 'FOLIAR',    '2025-01-20 06:30:00', 0.02);  -- Phân bón lá trước thu hoạch

-- ══════════════════════════════════════════
-- [15] STOCK TRANSACTION (USAGE — từ CultivationLog)
-- ══════════════════════════════════════════

INSERT INTO stock_transaction (supply_id, type, quantity, ref_id, created_at) VALUES
-- LOT-S1
(2,  'USAGE', 2.5,   1,  '2025-01-14 07:30:00'),
(1,  'USAGE', 1.8,   2,  '2025-01-28 08:00:00'),
(4,  'USAGE', 0.05,  3,  '2025-02-05 06:30:00'),
(6,  'USAGE', 0.03,  4,  '2025-02-10 07:00:00'),
(3,  'USAGE', 2.0,   5,  '2025-02-18 08:00:00'),
(4,  'USAGE', 0.05,  6,  '2025-02-25 06:30:00'),
(12, 'USAGE', 0.025, 7,  '2025-03-03 07:00:00'),
-- LOT-S2
(2,  'USAGE', 3.0,   8,  '2025-01-22 07:30:00'),
(1,  'USAGE', 2.0,   9,  '2025-02-05 08:00:00'),
(4,  'USAGE', 0.06,  10, '2025-02-15 06:30:00'),
(5,  'USAGE', 0.04,  11, '2025-02-20 07:00:00'),
(1,  'USAGE', 2.5,   12, '2025-02-28 08:00:00'),
-- LOT-L2
(2,  'USAGE', 8.0,   29, '2024-11-08 06:00:00'),
(1,  'USAGE', 12.0,  30, '2024-11-22 06:00:00'),
(5,  'USAGE', 0.5,   31, '2024-12-01 06:30:00'),
(6,  'USAGE', 0.8,   32, '2024-12-10 06:00:00'),
(1,  'USAGE', 6.0,   33, '2024-12-20 06:00:00'),
(5,  'USAGE', 0.6,   34, '2025-01-05 06:30:00');

-- ══════════════════════════════════════════
-- [16] IRRIGATION LOG
-- ══════════════════════════════════════════

INSERT INTO irrigation_log (lot_id, employee_id, irrigated_at) VALUES
(1,  3,  '2025-01-15 06:00:00'),
(1,  3,  '2025-01-22 06:00:00'),
(1,  3,  '2025-01-29 06:00:00'),
(1,  4,  '2025-02-05 06:00:00'),
(1,  4,  '2025-02-12 06:00:00'),
(1,  3,  '2025-02-19 06:00:00'),
(1,  4,  '2025-02-26 06:00:00'),
(2,  3,  '2025-01-23 06:00:00'),
(2,  4,  '2025-02-02 06:00:00'),
(2,  3,  '2025-02-09 06:00:00'),
(2,  4,  '2025-02-16 06:00:00'),
(2,  3,  '2025-02-23 06:00:00'),
(3,  3,  '2025-01-10 06:00:00'),
(3,  4,  '2025-01-18 06:00:00'),
(3,  3,  '2025-01-26 06:00:00'),
(3,  4,  '2025-02-03 06:00:00'),
(3,  3,  '2025-02-11 06:00:00'),
(7,  8,  '2024-11-12 05:30:00'),
(7,  8,  '2024-12-05 05:30:00'),
(7,  8,  '2025-01-25 05:30:00'),
(4,  3,  '2024-07-15 06:00:00'),
(4,  3,  '2024-09-10 06:00:00'),
(4,  3,  '2024-11-20 06:00:00'),
(5,  3,  '2024-08-01 06:00:00'),
(5,  3,  '2024-10-01 06:00:00'),
(9,  10, '2025-02-05 06:30:00'),
(9,  10, '2025-02-12 06:30:00'),
(9,  10, '2025-02-19 06:30:00'),
(10, 10, '2025-01-05 06:30:00'),
(10, 10, '2025-01-15 06:30:00'),
(10, 10, '2025-01-24 06:30:00'),
(11, 8,  '2025-01-20 06:00:00'),
(11, 8,  '2025-02-10 06:00:00'),
(11, 8,  '2025-03-05 06:00:00'),
(8,  8,  '2024-08-15 05:30:00'),
(8,  8,  '2024-09-10 05:30:00');

-- ══════════════════════════════════════════
-- [17] PEST REPORT
-- severity_code → FK → severity_level: LOW | MEDIUM | HIGH | CRITICAL
-- ══════════════════════════════════════════

INSERT INTO pest_report (lot_id, employee_id, severity_code) VALUES
(2,  2,  'MEDIUM'),   -- Bọ trĩ trên dưa lưới S2
(1,  2,  'LOW'),      -- Nấm phấn trắng nhẹ trên S1
(7,  7,  'HIGH'),     -- Sâu cuốn lá mật độ cao trên lúa L2
(7,  7,  'MEDIUM'),   -- Bệnh đạo ôn trên lúa L2
(4,  2,  'MEDIUM'),   -- Gỉ sắt cà phê Arabica
(3,  2,  'HIGH'),     -- Sâu đục trái cà chua S3
(9,  10, 'LOW'),      -- Nấm lở cổ rễ rau cải R1
(5,  2,  'LOW'),      -- Rệp sáp trên cà phê Robusta
(11, 7,  'MEDIUM'),   -- Sâu đục thân bắp B1
(8,  7,  'HIGH'),     -- Sâu cuốn lá trên lúa L3 (trước thu hoạch)
(1,  2,  'CRITICAL'), -- Phát hiện nấm Botrytis trên dưa lưới S1 (xử lý khẩn)
(2,  3,  'LOW');      -- Nhện đỏ xuất hiện đầu vụ trên dưa lưới S2

-- ══════════════════════════════════════════
-- [18] WEATHER LOG
-- ══════════════════════════════════════════

INSERT INTO weather_log (farm_id, lot_id, weather_date, temperature, rainfall_mm) VALUES
-- Farm 1 — Đắk Lắk (tháng 1-3/2025)
(1, NULL, '2025-01-15', 28.5,  0.0),
(1, NULL, '2025-01-16', 27.0,  5.2),
(1, NULL, '2025-01-17', 25.5,  22.5),
(1, NULL, '2025-01-18', 26.0,  8.0),
(1, NULL, '2025-01-19', 29.0,  0.0),
(1, NULL, '2025-01-25', 30.0,  0.0),
(1, NULL, '2025-02-01', 30.5,  0.0),
(1, NULL, '2025-02-02', 31.0,  0.0),
(1, NULL, '2025-02-10', 29.0,  12.0),
(1, NULL, '2025-02-15', 28.0,  3.5),
(1, 1,    '2025-02-20', 32.0,  0.0),
(1, 1,    '2025-03-01', 33.0,  0.0),
(1, 1,    '2025-03-05', 33.5,  0.0),
(1, 2,    '2025-02-25', 31.5,  2.0),
(1, 3,    '2025-03-03', 32.0,  0.0),

-- Farm 1 — Đắk Lắk (tháng 7-12/2024, mùa mưa cho cà phê)
(1, 4,    '2024-07-01', 24.0,  35.0),
(1, 4,    '2024-08-15', 23.5,  52.0),
(1, 4,    '2024-09-10', 24.0,  28.0),
(1, 4,    '2024-10-01', 23.0,  18.0),
(1, 4,    '2024-11-15', 22.0,  5.0),
(1, 5,    '2024-07-15', 24.5,  40.0),
(1, 5,    '2024-09-15', 23.8,  30.0),

-- Farm 2 — Tiền Giang (tháng 11/2024 – 3/2025, vụ lúa)
(2, NULL, '2025-01-15', 22.0,  18.0),
(2, NULL, '2025-01-25', 24.0,  0.0),
(2, NULL, '2025-02-05', 25.0,  3.5),
(2, 7,    '2024-11-08', 27.0,  8.0),
(2, 7,    '2024-12-01', 26.5,  0.0),
(2, 7,    '2024-12-20', 26.0,  0.0),
(2, 7,    '2025-01-05', 25.5,  0.0),
(2, 8,    '2024-08-05', 28.0,  45.0),
(2, 8,    '2024-09-20', 27.5,  30.0),
(2, 8,    '2024-10-15', 27.0,  12.0),
(2, 11,   '2025-01-18', 26.0,  0.0),
(2, 11,   '2025-02-20', 27.0,  5.0),

-- Farm 3 — Đà Lạt (mát quanh năm, hay có sương)
(3, NULL, '2025-02-01', 18.5,  8.0),
(3, NULL, '2025-02-10', 17.0,  15.0),
(3, NULL, '2025-02-15', 16.5,  20.0),
(3, 9,    '2025-02-20', 20.0,  2.0),
(3, 9,    '2025-02-25', 19.5,  0.0),
(3, 10,   '2025-01-10', 18.0,  10.0),
(3, 10,   '2025-01-20', 17.5,  5.0);

-- ══════════════════════════════════════════
-- [19] CUSTOMER
-- ══════════════════════════════════════════

INSERT INTO customer (customer_code, name) VALUES
('CUS001', 'Siêu thị Big C Buôn Ma Thuột'),
('CUS002', 'Công ty XNK Nông sản Việt'),
('CUS003', 'HTX Tiêu thụ Nông sản Tây Bắc'),
('CUS004', 'Nhà máy chế biến Vinafood 2'),
('CUS005', 'Chuỗi cửa hàng Sachi Mart'),
('CUS006', 'Công ty TNHH TM Rau sạch Đà Lạt'),
('CUS007', 'Đại lý thu mua nông sản Hải Nam');

-- ══════════════════════════════════════════
-- [20] HARVEST RECORD
-- quality_grade_code → FK → quality_grade: A | B | C
-- ══════════════════════════════════════════

INSERT INTO harvest_record (lot_id, employee_id, customer_id, harvest_date, yield_kg, quality_grade_code) VALUES
-- LOT-L3: Lúa IR50404 thu hoạch 10/2024
(8,  6, 4, '2024-10-28', 42000.0, 'GRADE_A'),  -- Lúa khô, tỷ lệ hạt chắc 92%, bán Vinafood 2
-- LOT-R2: Rau cải Đà Lạt 2 thu hoạch 1/2025
(10, 9, 5, '2025-01-28', 2850.0,  'GRADE_A'),  -- Rau tươi sạch, giao Sachi Mart
-- LOT-L2: Lúa OM5451 thu hoạch 2/2025
(7,  6, 4, '2025-02-08', 38500.0, 'GRADE_B'),  -- Lúa OM5451, ẩm độ 22%, bán Vinafood 2
-- LOT-S1: Dưa lưới nhà màng 1 — chưa thu hoạch (dự kiến 18/03/2025)
-- LOT-S2: Dưa lưới nhà màng 2 — chưa thu hoạch (dự kiến 26/03/2025)
-- LOT-S3: Cà chua Cherry — chưa thu hoạch (dự kiến 24/03/2025)
-- LOT-C1/C2: Cà phê — đang chín (dự kiến 6/2025)
-- LOT-B1: Bắp lai — đang phát triển (dự kiến 5/2025)
-- LOT-R1: Rau cải Đà Lạt 1 — đang growing (dự kiến 3/3/2025)
-- Thêm thu hoạch lịch sử vụ S2024A (Đông Xuân 2023-2024)
(8,  6, 4, '2024-03-10', 39800.0, 'GRADE_B'),  -- Lúa vụ trước, bán Vinafood 2
(10, 9, 6, '2025-01-05', 1200.0,  'GRADE_A');  -- Rau cải đợt đầu, bán Rau sạch Đà Lạt

-- ══════════════════════════════════════════
-- VERIFICATION — kiểm tra số bản ghi
-- ══════════════════════════════════════════

SELECT 'crop_category'        AS tbl, COUNT(*) AS total FROM crop_category
UNION ALL SELECT 'crop_type',          COUNT(*) FROM crop_type
UNION ALL SELECT 'season',             COUNT(*) FROM season
UNION ALL SELECT 'supply_category',    COUNT(*) FROM supply_category
UNION ALL SELECT 'farm',               COUNT(*) FROM farm
UNION ALL SELECT 'department',         COUNT(*) FROM department
UNION ALL SELECT 'employee',           COUNT(*) FROM employee
UNION ALL SELECT 'production_lot',     COUNT(*) FROM production_lot
UNION ALL SELECT 'supplier',           COUNT(*) FROM supplier
UNION ALL SELECT 'agri_supply',        COUNT(*) FROM agri_supply
UNION ALL SELECT 'supply_import',      COUNT(*) FROM supply_import
UNION ALL SELECT 'supply_import_detail',COUNT(*) FROM supply_import_detail
UNION ALL SELECT 'stock_transaction',  COUNT(*) FROM stock_transaction
UNION ALL SELECT 'cultivation_log',    COUNT(*) FROM cultivation_log
UNION ALL SELECT 'irrigation_log',     COUNT(*) FROM irrigation_log
UNION ALL SELECT 'pest_report',        COUNT(*) FROM pest_report
UNION ALL SELECT 'weather_log',        COUNT(*) FROM weather_log
UNION ALL SELECT 'customer',           COUNT(*) FROM customer
UNION ALL SELECT 'harvest_record',     COUNT(*) FROM harvest_record;