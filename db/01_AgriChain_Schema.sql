-- =============================================================================
-- Dự án: AgriChain - Hệ thống Quản lý Chuỗi Cung ứng Nông nghiệp
-- Phát triển bởi: AriOps Team
-- Phiên bản: 2.0 (Chuẩn hóa RDBMS & Lookup Tables)
-- Mô tả: File khởi tạo cấu trúc Database (Schema)
-- Công nghệ: MySQL 8.0+ / InnoDB Engine
-- =============================================================================

DROP DATABASE IF EXISTS agri_chain;
CREATE DATABASE agri_chain CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE agri_chain;
SET NAMES 'utf8mb4';

-- =====================================================
-- [1. LOOKUP TABLES]
-- =====================================================

CREATE TABLE role (
    code VARCHAR(30) PRIMARY KEY
);
INSERT INTO role VALUES ('MANAGER'), ('TECHNICIAN'), ('WORKER');

CREATE TABLE lot_status (
    code VARCHAR(30) PRIMARY KEY
);
INSERT INTO lot_status VALUES 
('PLANTED'), ('GROWING'), ('FLOWERING'), ('FRUITING'), ('HARVESTED'), ('IDLE');

CREATE TABLE activity_type (
    code VARCHAR(30) PRIMARY KEY
);
INSERT INTO activity_type VALUES 
('FERTILIZE'), ('PESTICIDE'), ('FUNGICIDE'), ('FOLIAR');

CREATE TABLE severity_level (
    code VARCHAR(30) PRIMARY KEY
);
INSERT INTO severity_level VALUES 
('LOW'), ('MEDIUM'), ('HIGH'), ('CRITICAL');

CREATE TABLE quality_grade (
    code VARCHAR(10) PRIMARY KEY
);
INSERT INTO quality_grade VALUES ('GRADE_A'), ('GRADE_B'), ('GRADE_C');

-- =====================================================
-- [2. MASTER DATA]
-- =====================================================

CREATE TABLE crop_category (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    parent_id BIGINT,
    FOREIGN KEY (parent_id) REFERENCES crop_category(id)
);

CREATE TABLE crop_type (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    crop_code VARCHAR(20) UNIQUE,
    name VARCHAR(100),
    category_id BIGINT,
    growth_days INT,
    FOREIGN KEY (category_id) REFERENCES crop_category(id)
);

CREATE TABLE season (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    season_code VARCHAR(20) UNIQUE,
    name VARCHAR(100),
    start_date DATE,
    end_date DATE
);

CREATE TABLE supply_category (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100)
);

-- =====================================================
-- [3. CORE]
-- =====================================================

CREATE TABLE farm (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    farm_code VARCHAR(20) UNIQUE,
    name VARCHAR(100),
    address VARCHAR(255),
    total_area DOUBLE,
    owner_name VARCHAR(100),
    phone VARCHAR(20),

    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    created_by VARCHAR(50),
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    updated_by VARCHAR(50),
    deleted_at DATETIME  
);

CREATE TABLE department (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    dept_code VARCHAR(20) UNIQUE,
    name VARCHAR(100),
    farm_id BIGINT,
    FOREIGN KEY (farm_id) REFERENCES farm(id)
);

CREATE TABLE employee (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    emp_code VARCHAR(20) UNIQUE,
    full_name VARCHAR(100),
    phone VARCHAR(20),
    email VARCHAR(100),

    role_code VARCHAR(30),
    hire_date DATE,

    department_id BIGINT,
    FOREIGN KEY (department_id) REFERENCES department(id),
    FOREIGN KEY (role_code) REFERENCES role(code),

    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

-- =====================================================
-- [4. PRODUCTION]
-- =====================================================

CREATE TABLE production_lot (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    lot_code VARCHAR(20) UNIQUE,

    area_m2 DOUBLE,
    status_code VARCHAR(30),
    location_desc VARCHAR(255),

    plant_date DATE,
    expected_harvest_date DATE,
    actual_harvest_date DATE,

    farm_id BIGINT,
    crop_type_id BIGINT,
    season_id BIGINT,
    manager_id BIGINT,

    FOREIGN KEY (status_code) REFERENCES lot_status(code),
    FOREIGN KEY (farm_id) REFERENCES farm(id),
    FOREIGN KEY (crop_type_id) REFERENCES crop_type(id),
    FOREIGN KEY (season_id) REFERENCES season(id),
    FOREIGN KEY (manager_id) REFERENCES employee(id),

    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

-- =====================================================
-- [5. INVENTORY]
-- =====================================================

CREATE TABLE supplier (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    supplier_code VARCHAR(20) UNIQUE,
    name VARCHAR(100),
    phone VARCHAR(20)
);

CREATE TABLE agri_supply (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    supply_code VARCHAR(20) UNIQUE,
    farm_id BIGINT,
    FOREIGN KEY (farm_id) REFERENCES farm(id),
    name VARCHAR(100),

    category_id BIGINT,
    unit VARCHAR(20),
    stock_qty DOUBLE DEFAULT 0,
    min_stock DOUBLE DEFAULT 0,

    supplier_id BIGINT,

    FOREIGN KEY (category_id) REFERENCES supply_category(id),
    FOREIGN KEY (supplier_id) REFERENCES supplier(id)
);

CREATE TABLE stock_transaction (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    supply_id BIGINT,
    type VARCHAR(30), -- IMPORT | USAGE | ADJUST
    quantity DOUBLE,
    ref_id BIGINT,
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,

    FOREIGN KEY (supply_id) REFERENCES agri_supply(id)
);

CREATE TABLE supply_import (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    import_code VARCHAR(20) UNIQUE,
    supplier_id BIGINT,
    employee_id BIGINT,
    import_date DATETIME,

    FOREIGN KEY (supplier_id) REFERENCES supplier(id),
    FOREIGN KEY (employee_id) REFERENCES employee(id)
);

CREATE TABLE supply_import_detail (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    import_id BIGINT,
    supply_id BIGINT,
    quantity DOUBLE,
    unit_price DECIMAL(15,2),

    FOREIGN KEY (import_id) REFERENCES supply_import(id),
    FOREIGN KEY (supply_id) REFERENCES agri_supply(id)
);

-- =====================================================
-- [6. OPERATION]
-- =====================================================

CREATE TABLE cultivation_log (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    lot_id BIGINT,
    supply_id BIGINT,
    employee_id BIGINT,

    activity_type_code VARCHAR(30),
    applied_at DATETIME,
    dosage_used DOUBLE,

    FOREIGN KEY (lot_id) REFERENCES production_lot(id),
    FOREIGN KEY (supply_id) REFERENCES agri_supply(id),
    FOREIGN KEY (employee_id) REFERENCES employee(id),
    FOREIGN KEY (activity_type_code) REFERENCES activity_type(code),

    INDEX idx_lot_date (lot_id, applied_at)
);

CREATE TABLE irrigation_log (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    lot_id BIGINT,
    employee_id BIGINT,
    irrigated_at DATETIME,
    water_amount DOUBLE,
source VARCHAR(100),
duration_min INT,

    FOREIGN KEY (lot_id) REFERENCES production_lot(id),
    FOREIGN KEY (employee_id) REFERENCES employee(id)
);

CREATE TABLE pest_report (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    lot_id BIGINT,
    employee_id BIGINT,
    severity_code VARCHAR(30),
    pest_name  VARCHAR(100),
treatment  VARCHAR(255),
damage_pct DOUBLE,
reported_at DATETIME,

    FOREIGN KEY (lot_id) REFERENCES production_lot(id),
    FOREIGN KEY (employee_id) REFERENCES employee(id),
    FOREIGN KEY (severity_code) REFERENCES severity_level(code)
);

CREATE TABLE weather_log (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    farm_id BIGINT,
    lot_id BIGINT,
    weather_date DATE,

    temperature DOUBLE,
    rainfall_mm DOUBLE,

    FOREIGN KEY (farm_id) REFERENCES farm(id),
    FOREIGN KEY (lot_id) REFERENCES production_lot(id),

    INDEX idx_weather (farm_id, weather_date)
);

-- =====================================================
-- [7. OUTPUT]
-- =====================================================

CREATE TABLE customer (
    id            BIGINT      AUTO_INCREMENT PRIMARY KEY,
    customer_code VARCHAR(20) UNIQUE,
    name          VARCHAR(100),
    phone         VARCHAR(20),
    email         VARCHAR(100),
    type          VARCHAR(50),   -- WHOLESALER | RETAILER | PROCESSOR | EXPORTER
    status        VARCHAR(20)    DEFAULT 'ACTIVE'
);

CREATE TABLE harvest_record (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    lot_id BIGINT,
    employee_id BIGINT,
    customer_id BIGINT,

    harvest_date DATE,
    yield_kg DOUBLE,
    quality_grade_code VARCHAR(10),

    FOREIGN KEY (lot_id) REFERENCES production_lot(id),
    FOREIGN KEY (employee_id) REFERENCES employee(id),
    FOREIGN KEY (customer_id) REFERENCES customer(id),
    FOREIGN KEY (quality_grade_code) REFERENCES quality_grade(code),

    INDEX idx_harvest (lot_id, harvest_date)
);