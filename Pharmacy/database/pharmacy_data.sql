
CREATE SCHEMA IF NOT EXISTS pharmacy;
SET search_path = pharmacy;

-- ========================================
-- ENUM TYPES
-- ========================================

DO $$ BEGIN CREATE TYPE app_role AS ENUM ('PHARMACY_TECH','PHARMACIST'); EXCEPTION WHEN duplicate_object THEN NULL; END $$;
DO $$ BEGIN CREATE TYPE facility_type AS ENUM ('HOSPITAL','CLINIC'); EXCEPTION WHEN duplicate_object THEN NULL; END $$;
DO $$ BEGIN CREATE TYPE fulfillment_status AS ENUM ('FILLED','VERIFIED','REWORK','CANCELED'); EXCEPTION WHEN duplicate_object THEN NULL; END $$;
DO $$ BEGIN CREATE TYPE sex_enum AS ENUM ('M','F'); EXCEPTION WHEN duplicate_object THEN NULL; END $$;
DO $$ BEGIN CREATE TYPE daw_code_enum AS ENUM ('0','1','2','3','4','5','6','7','8','9'); EXCEPTION WHEN duplicate_object THEN NULL; END $$;

-- ========================================
-- ENUMS
-- ========================================

CREATE TYPE workflow_stage AS ENUM (
    'DATA_ENTRY',
    'DATA_REVIEW',
    'PRODUCT',
    'PRODUCT_REVIEW',
    'NOTIFICATIONS',
    'COMPLETED',
    'CANCELLED'
);

CREATE TYPE workflow_status AS ENUM (
    'DATA_ENTRY_PENDING',
    'DATA_ENTRY_IN_PROGRESS',
    'DATA_ENTRY_COMPLETED',
    'DATA_REVIEW_PENDING',
    'DATA_REVIEW_IN_PROGRESS',
    'DATA_REVIEW_APPROVED',
    'DATA_REVIEW_REJECTED',
    'PRODUCT_PENDING',
    'PRODUCT_IN_PROGRESS',
    'PRODUCT_SELECTED',
    'PRODUCT_OUT_OF_STOCK',
    'PRODUCT_FULFILLED',
    'PRODUCT_REVIEW_PENDING',
    'PRODUCT_REVIEW_IN_PROGRESS',
    'PRODUCT_REVIEW_APPROVED',
    'PRODUCT_REVIEW_REJECTED',
    'NOTIFICATIONS_PENDING',
    'NOTIFICATIONS_SENT',
    'NOTIFICATIONS_FAILED',
    'COMPLETED',
    'CANCELLED'
);

CREATE TABLE IF NOT EXISTS app_user (
    user_id bigserial PRIMARY KEY,
    username text NOT NULL UNIQUE,
    email text UNIQUE,
    password_hash text NOT NULL,
    role app_role NOT NULL DEFAULT 'PHARMACY_TECH',
    created_at timestamptz NOT NULL DEFAULT now()
);

CREATE TABLE IF NOT EXISTS facility (
    facility_id bigserial PRIMARY KEY,
    name text NOT NULL,
    type facility_type NOT NULL,
    phone text,
    address text,
    city text,
    state text,
    zip_code text
);

CREATE TABLE IF NOT EXISTS prescriber (
    prescriber_id bigserial PRIMARY KEY,
    first_name text NOT NULL,
    last_name text NOT NULL,
    npi text UNIQUE,
    phone text,
    email text,
    address text,
    city text,
    state text,
    zip_code text
);

CREATE TABLE IF NOT EXISTS prescriber_facility (
    prescriber_id bigint NOT NULL REFERENCES prescriber(prescriber_id) ON DELETE CASCADE,
    facility_id bigint NOT NULL REFERENCES facility(facility_id) ON DELETE CASCADE,
    PRIMARY KEY (prescriber_id, facility_id)
);

CREATE TABLE IF NOT EXISTS patient (
    patient_id bigserial PRIMARY KEY,
    first_name text NOT NULL,
    last_name text NOT NULL,
    date_of_birth date,
    sex sex_enum,
    phone text,
    email text,
    address text,
    city text,
    state text,
    zip_code text,
    created_at timestamptz NOT NULL DEFAULT now()
);

CREATE TABLE IF NOT EXISTS medication (
    medication_id bigserial PRIMARY KEY,
    generic_name text NOT NULL,
    brand_name text,
    dosage_form text NOT NULL
);

CREATE TABLE IF NOT EXISTS product (
    product_id bigserial PRIMARY KEY,
    ndc text UNIQUE,
    name text NOT NULL,
    generic_name text,
    manufacturer text,
    dosage_form text,
    strength text,
    is_rx boolean NOT NULL DEFAULT true,
    is_vaccine boolean NOT NULL DEFAULT false,
    unit_price numeric(12,2) CHECK (unit_price >= 0),
    stock_qty integer NOT NULL DEFAULT 0 CHECK (stock_qty >= 0),
    medication_id bigint REFERENCES medication(medication_id)
);

CREATE TABLE IF NOT EXISTS product_lot (
    product_lot_id bigserial PRIMARY KEY,
    product_id bigint NOT NULL REFERENCES product(product_id) ON DELETE CASCADE,
    lot_number text NOT NULL,
    expiration_date date,
    quantity integer NOT NULL DEFAULT 0 CHECK (quantity >= 0),
    UNIQUE(product_id, lot_number)
);

CREATE TABLE IF NOT EXISTS prescription (
    prescription_id bigserial PRIMARY KEY,
    rx_number text UNIQUE,
    patient_id bigint NOT NULL REFERENCES patient(patient_id) ON DELETE CASCADE,
    prescriber_id bigint NOT NULL REFERENCES prescriber(prescriber_id),
    facility_id bigint NOT NULL REFERENCES facility(facility_id),
    date_written date NOT NULL,
    status rx_status NOT NULL DEFAULT 'ACTIVE',
    -- Workflow tracking fields
    current_stage workflow_stage NOT NULL DEFAULT 'DATA_ENTRY',
    workflow_status workflow_status NOT NULL DEFAULT 'DATA_ENTRY_PENDING',
    entered_workflow_at timestamptz NOT NULL DEFAULT now(),
    current_stage_entered_at timestamptz NOT NULL DEFAULT now(),
    assigned_to_user_id text,
    notes text,
    priority boolean NOT NULL DEFAULT false,
    completed_at timestamptz
);

CREATE TABLE IF NOT EXISTS prescription_item (
    item_id bigserial PRIMARY KEY,
    prescription_id bigint NOT NULL REFERENCES prescription(prescription_id) ON DELETE CASCADE,
    medication_id bigint NOT NULL REFERENCES medication(medication_id),
    strength text NOT NULL,
    dose text NOT NULL,
    quantity numeric(10,2) NOT NULL CHECK (quantity > 0),
    sig text NOT NULL,
    daw daw_code_enum NOT NULL,
    refills smallint NOT NULL DEFAULT 0 CHECK (refills >= 0 AND refills <= 12)
);

CREATE TABLE IF NOT EXISTS fulfillment (
    fulfillment_id bigserial PRIMARY KEY,
    prescription_id bigint NOT NULL REFERENCES prescription(prescription_id) ON DELETE CASCADE,
    product_id bigint REFERENCES product(product_id),
    lot_id bigint REFERENCES product_lot(product_lot_id),
    quantity_dispensed integer NOT NULL CHECK (quantity_dispensed > 0),
    filled_at timestamptz,
    filled_by_user_id bigint REFERENCES app_user(user_id),
    verified_by_pharmacist_id bigint REFERENCES app_user(user_id),
    status fulfillment_status NOT NULL
);

CREATE TABLE IF NOT EXISTS insurance_profile (
    insurance_id bigserial PRIMARY KEY,
    patient_id bigint NOT NULL REFERENCES patient(patient_id) ON DELETE CASCADE,
    provider text NOT NULL,
    member_id text,
    group_number text,
    bin_number text,
    pcn text,
    effective_from date,
    created_at timestamptz NOT NULL DEFAULT now()
);

-- ========================================
-- INDEXES
-- ========================================

CREATE INDEX IF NOT EXISTS idx_patient_phone ON patient(phone);
CREATE INDEX IF NOT EXISTS idx_prescription_patient ON prescription(patient_id);
CREATE INDEX IF NOT EXISTS idx_prescription_prescriber ON prescription(prescriber_id);
CREATE INDEX IF NOT EXISTS idx_prescription_facility ON prescription(facility_id);
CREATE INDEX IF NOT EXISTS idx_item_prescription ON prescription_item(prescription_id);
CREATE INDEX IF NOT EXISTS idx_item_medication ON prescription_item(medication_id);
CREATE INDEX IF NOT EXISTS idx_fulfillment_prescription ON fulfillment(prescription_id);
CREATE INDEX IF NOT EXISTS idx_product_medication ON product(medication_id);
CREATE INDEX IF NOT EXISTS idx_product_lot_product ON product_lot(product_id);

-- ========================================
-- BASIC INSERT STATEMENTS
-- ========================================

INSERT INTO daw_code (code, description) VALUES
('0', 'No product selection indicated'),
('1', 'Substitution not allowed by prescriber'),
('2', 'Substitution allowed – patient requested product dispensed'),
('3', 'Substitution allowed – pharmacist selected product'),
('4', 'Substitution allowed – generic drug not in stock'),
('5', 'Substitution allowed – brand drug dispensed as generic'),
('6', 'Override'),
('7', 'Substitution not allowed – brand mandated by law'),
('8', 'Substitution allowed – generic not available in marketplace'),
('9', 'Other');

INSERT INTO app_user (username, email, password_hash, role) VALUES
('pharmacist_admin', 'admin@pharmacyportal.local', 'hashed-placeholder', 'PHARMACIST'),
('tech.jane', 'jane@example.com', 'hashed-placeholder', 'PHARMACY_TECH'),
('tech.john', 'john@example.com', 'hashed-placeholder', 'PHARMACY_TECH');

INSERT INTO facility (name, type, phone, address, city, state, zip_code) VALUES
('General Hospital', 'HOSPITAL', '555-1000', '123 Main St', 'San Diego', 'CA', '92101'),
('City Medical Center', 'CLINIC', '555-1001', '456 Oak Ave', 'San Diego', 'CA', '92102'),
('Regional Health Clinic', 'CLINIC', '555-1002', '789 Elm St', 'Chula Vista', 'CA', '91910'),
('Family Practice Associates', 'CLINIC', '555-1003', '321 Pine Rd', 'La Mesa', 'CA', '91941'),
('Downtown Medical Plaza', 'CLINIC', '555-1004', '555 Broadway', 'San Diego', 'CA', '92101'),
('North County Hospital', 'HOSPITAL', '555-1005', '1000 Health Way', 'Oceanside', 'CA', '92054'),
('South Bay Family Clinic', 'CLINIC', '555-1006', '200 Palm Ave', 'Imperial Beach', 'CA', '91932'),
('East County Medical Center', 'CLINIC', '555-1007', '300 Alpine Blvd', 'Alpine', 'CA', '91901'),
('Westside Health Partners', 'CLINIC', '555-1008', '400 Sunset Blvd', 'San Diego', 'CA', '92107'),
('Coastal Community Hospital', 'HOSPITAL', '555-1009', '500 Ocean View Dr', 'Encinitas', 'CA', '92024'),
('Valley Medical Associates', 'CLINIC', '555-1010', '600 Valley Pkwy', 'Escondido', 'CA', '92025'),
('Mountain Health Center', 'CLINIC', '555-1011', '700 Mountain Rd', 'Julian', 'CA', '92036'),
('Desert Regional Hospital', 'HOSPITAL', '555-1012', '800 Desert Way', 'El Centro', 'CA', '92243'),
('Bay Area Medical Group', 'CLINIC', '555-1013', '900 Bay St', 'Chula Vista', 'CA', '91911'),
('Central San Diego Clinic', 'CLINIC', '555-1014', '1000 Central Ave', 'San Diego', 'CA', '92105'),
('North Park Family Practice', 'CLINIC', '555-1015', '1100 University Ave', 'San Diego', 'CA', '92104'),
('Hillcrest Medical Center', 'CLINIC', '555-1016', '1200 Washington St', 'San Diego', 'CA', '92103'),
('Mission Valley Hospital', 'HOSPITAL', '555-1017', '1300 Mission Center Rd', 'San Diego', 'CA', '92108'),
('La Jolla Medical Plaza', 'CLINIC', '555-1018', '1400 La Jolla Village Dr', 'San Diego', 'CA', '92161'),
('Pacific Beach Health Center', 'CLINIC', '555-1019', '1500 Garnet Ave', 'San Diego', 'CA', '92109'),
('Point Loma Family Clinic', 'CLINIC', '555-1020', '1600 Rosecrans St', 'San Diego', 'CA', '92106'),
('Scripps Mercy Hospital', 'HOSPITAL', '555-1021', '1700 5th Ave', 'San Diego', 'CA', '92103'),
('Sharp Memorial Hospital', 'HOSPITAL', '555-1022', '1800 Sharp Ave', 'San Diego', 'CA', '92123'),
('UCSD Medical Center', 'HOSPITAL', '555-1023', '1900 Gilman Dr', 'La Jolla', 'CA', '92093'),
('Naval Medical Center', 'HOSPITAL', '555-1024', '2000 Bob Wilson Dr', 'San Diego', 'CA', '92134'),
('Veterans Affairs Hospital', 'HOSPITAL', '555-1025', '2100 Villa La Jolla Dr', 'La Jolla', 'CA', '92037'),
('Children''s Hospital', 'HOSPITAL', '555-1026', '2200 Childrens Way', 'San Diego', 'CA', '92123'),
('Scripps Green Hospital', 'HOSPITAL', '555-1027', '2300 Torrey Pines Rd', 'La Jolla', 'CA', '92037'),
('Alvarado Hospital', 'HOSPITAL', '555-1028', '2400 Alvarado Rd', 'San Diego', 'CA', '92123'),
('Grossmont Hospital', 'HOSPITAL', '555-1029', '2500 Grossmont Center Dr', 'La Mesa', 'CA', '91942'),
('Palomar Medical Center', 'HOSPITAL', '555-1030', '2600 Pomerado Rd', 'Poway', 'CA', '92064'),
('Tri-City Medical Center', 'HOSPITAL', '555-1031', '2700 Medical Center Dr', 'Oceanside', 'CA', '92056'),
('Pioneers Memorial Hospital', 'HOSPITAL', '555-1032', '2800 N Imperial Ave', 'El Centro', 'CA', '92243'),
('El Centro Regional Medical Center', 'HOSPITAL', '555-1033', '2900 Ross Ave', 'El Centro', 'CA', '92243'),
('Paradise Valley Hospital', 'HOSPITAL', '555-1034', '3000 Medical Center Dr', 'National City', 'CA', '91950'),
('Bayview Hospital', 'HOSPITAL', '555-1035', '3100 Bayview Dr', 'Chula Vista', 'CA', '91910'),
('Coronado Hospital', 'HOSPITAL', '555-1036', '3200 4th St', 'Coronado', 'CA', '92118'),
('Fallbrook Hospital', 'HOSPITAL', '555-1037', '3300 S Mission Rd', 'Fallbrook', 'CA', '92028'),
('Ramona Medical Center', 'CLINIC', '555-1038', '3400 Montecito Rd', 'Ramona', 'CA', '92065'),
('Borrego Medical Center', 'CLINIC', '555-1039', '3500 Palm Canyon Dr', 'Borrego Springs', 'CA', '92004'),
('Campo Medical Clinic', 'CLINIC', '555-1040', '3600 Buckman Springs Rd', 'Campo', 'CA', '91906'),
('Tecate Family Clinic', 'CLINIC', '555-1041', '3700 Tecate Rd', 'Tecate', 'CA', '91980'),
('San Ysidro Health Center', 'CLINIC', '555-1042', '3800 Beyer Blvd', 'San Ysidro', 'CA', '92173'),
('Imperial Beach Health Clinic', 'CLINIC', '555-1043', '3900 Imperial Beach Blvd', 'Imperial Beach', 'CA', '91932'),
('National City Medical Plaza', 'CLINIC', '555-1044', '4000 Highland Ave', 'National City', 'CA', '91950'),
('Lemon Grove Family Practice', 'CLINIC', '555-1045', '4100 Broadway', 'Lemon Grove', 'CA', '91945'),
('Spring Valley Medical Center', 'CLINIC', '555-1046', '4200 Campo Rd', 'Spring Valley', 'CA', '91977'),
('Lakeside Community Clinic', 'CLINIC', '555-1047', '4300 Maine Ave', 'Lakeside', 'CA', '92040'),
('Santee Family Health Center', 'CLINIC', '555-1048', '4400 Mission Gorge Rd', 'Santee', 'CA', '92071'),
('El Cajon Valley Hospital', 'HOSPITAL', '555-1049', '4500 Lexington Ave', 'El Cajon', 'CA', '92020');

INSERT INTO prescriber (first_name, last_name, npi, phone, email, address, city, state, zip_code) VALUES
('Robert', 'Smith', '1234567890', '555-2000', 'rsmith@generalhospital.org', '100 Doctor Way', 'San Diego', 'CA', '92101'),
('Jennifer', 'Brown', '1234567891', '555-2001', 'jbrown@citymedical.com', '200 Clinic Blvd', 'San Diego', 'CA', '92102'),
('Michael', 'Davis', '1234567892', '555-2002', 'mdavis@regionalclinic.net', '300 Health St', 'Chula Vista', 'CA', '91910'),
('Sarah', 'Wilson', '1234567893', '555-2003', 'swilson@familymedical.com', '400 Care Ave', 'La Mesa', 'CA', '91941'),
('David', 'Johnson', '1234567894', '555-2004', 'djohnson@downtownmedical.com', '500 Medical Plaza', 'San Diego', 'CA', '92101'),
('Lisa', 'Garcia', '1234567895', '555-2005', 'lgarcia@northcounty.org', '600 Hospital Way', 'Oceanside', 'CA', '92054'),
('James', 'Martinez', '1234567896', '555-2006', 'jmartinez@southbayclinic.com', '700 Palm Ave', 'Imperial Beach', 'CA', '91932'),
('Maria', 'Rodriguez', '1234567897', '555-2007', 'mrodriguez@eastcounty.com', '800 Alpine Blvd', 'Alpine', 'CA', '91901'),
('Carlos', 'Hernandez', '1234567898', '555-2008', 'chernandez@westsidehealth.com', '900 Sunset Blvd', 'San Diego', 'CA', '92107'),
('Ana', 'Lopez', '1234567899', '555-2009', 'alopez@coastalhospital.org', '1000 Ocean View Dr', 'Encinitas', 'CA', '92024'),
('Thomas', 'Anderson', '1234567900', '555-2010', 'tanderson@valleymedical.com', '1100 Valley Pkwy', 'Escondido', 'CA', '92025'),
('Patricia', 'Taylor', '1234567901', '555-2011', 'ptaylor@mountainhealth.org', '1200 Mountain Rd', 'Julian', 'CA', '92036'),
('Christopher', 'Moore', '1234567902', '555-2012', 'cmoore@deserthospital.org', '1300 Desert Way', 'El Centro', 'CA', '92243'),
('Linda', 'Jackson', '1234567903', '555-2013', 'ljackson@bayareamedical.com', '1400 Bay St', 'Chula Vista', 'CA', '91911'),
('Mark', 'White', '1234567904', '555-2014', 'mwhite@centralsandiego.com', '1500 Central Ave', 'San Diego', 'CA', '92105'),
('Susan', 'Harris', '1234567905', '555-2015', 'sharris@northparkfp.com', '1600 University Ave', 'San Diego', 'CA', '92104'),
('Steven', 'Martin', '1234567906', '555-2016', 'smartin@hillcrestmed.com', '1700 Washington St', 'San Diego', 'CA', '92103'),
('Nancy', 'Thompson', '1234567907', '555-2017', 'nthompson@missionvalley.org', '1800 Mission Center Rd', 'San Diego', 'CA', '92108'),
('Kevin', 'Clark', '1234567908', '555-2018', 'kclark@lajollamedical.com', '1900 La Jolla Village Dr', 'San Diego', 'CA', '92161'),
('Betty', 'Lewis', '1234567909', '555-2019', 'blewis@pacificbeach.com', '2000 Garnet Ave', 'San Diego', 'CA', '92109'),
('Daniel', 'Robinson', '1234567910', '555-2020', 'drobinson@pointloma.com', '2100 Rosecrans St', 'San Diego', 'CA', '92106'),
('Helen', 'Walker', '1234567911', '555-2021', 'hwalker@scrippsmercy.org', '2200 5th Ave', 'San Diego', 'CA', '92103'),
('John', 'Hall', '1234567912', '555-2022', 'jhall@sharpmemorial.org', '2300 Sharp Ave', 'San Diego', 'CA', '92123'),
('Mary', 'Allen', '1234567913', '555-2023', 'mallen@ucsdmedical.org', '2400 Gilman Dr', 'La Jolla', 'CA', '92093'),
('Richard', 'Young', '1234567914', '555-2024', 'ryoung@navalmedical.org', '2500 Bob Wilson Dr', 'San Diego', 'CA', '92134'),
('Sandra', 'King', '1234567915', '555-2025', 'sking@veteranshospital.org', '2600 Villa La Jolla Dr', 'La Jolla', 'CA', '92037'),
('Joseph', 'Wright', '1234567916', '555-2026', 'jwright@childrenshospital.org', '2700 Childrens Way', 'San Diego', 'CA', '92123'),
('Carol', 'Lopez', '1234567917', '555-2027', 'clopez@scrippsgreen.org', '2800 Torrey Pines Rd', 'La Jolla', 'CA', '92037'),
('Charles', 'Hill', '1234567918', '555-2028', 'chill@alvaradohospital.org', '2900 Alvarado Rd', 'San Diego', 'CA', '92123'),
('Ruth', 'Scott', '1234567919', '555-2029', 'rscott@grossmonthospital.org', '3000 Grossmont Center Dr', 'La Mesa', 'CA', '91942'),
('Anthony', 'Green', '1234567920', '555-2030', 'agreen@palomarhospital.org', '3100 Pomerado Rd', 'Poway', 'CA', '92064'),
('Sharon', 'Adams', '1234567921', '555-2031', 'sadams@tricitymedical.org', '3200 Medical Center Dr', 'Oceanside', 'CA', '92056'),
('Paul', 'Baker', '1234567922', '555-2032', 'pbaker@pioneershospital.org', '3300 N Imperial Ave', 'El Centro', 'CA', '92243'),
('Virginia', 'Gonzalez', '1234567923', '555-2033', 'vgonzalez@elcentrohospital.org', '3400 Ross Ave', 'El Centro', 'CA', '92243'),
('Kenneth', 'Nelson', '1234567924', '555-2034', 'knelson@paradisevalley.org', '3500 Medical Center Dr', 'National City', 'CA', '91950'),
('Donna', 'Carter', '1234567925', '555-2035', 'dcarter@bayviewhospital.org', '3600 Bayview Dr', 'Chula Vista', 'CA', '91910'),
('George', 'Mitchell', '1234567926', '555-2036', 'gmitchell@coronadohospital.org', '3700 4th St', 'Coronado', 'CA', '92118'),
('Dorothy', 'Perez', '1234567927', '555-2037', 'dperez@fallbrookhospital.org', '3800 S Mission Rd', 'Fallbrook', 'CA', '92028'),
('Edward', 'Roberts', '1234567928', '555-2038', 'eroberts@ramonamedical.org', '3900 Montecito Rd', 'Ramona', 'CA', '92065'),
('Michelle', 'Turner', '1234567929', '555-2039', 'mturner@borregomedical.org', '4000 Palm Canyon Dr', 'Borrego Springs', 'CA', '92004'),
('Brian', 'Phillips', '1234567930', '555-2040', 'bphillips@campomedical.com', '4100 Buckman Springs Rd', 'Campo', 'CA', '91906'),
('Amanda', 'Campbell', '1234567931', '555-2041', 'acampbell@tecateclinic.com', '4200 Tecate Rd', 'Tecate', 'CA', '91980'),
('Ronald', 'Parker', '1234567932', '555-2042', 'rparker@sanyisidrohealth.org', '4300 Beyer Blvd', 'San Ysidro', 'CA', '92173'),
('Melissa', 'Evans', '1234567933', '555-2043', 'mevans@imperialbeach.com', '4400 Imperial Beach Blvd', 'Imperial Beach', 'CA', '91932'),
('Timothy', 'Edwards', '1234567934', '555-2044', 'tedwards@nationalcitymed.com', '4500 Highland Ave', 'National City', 'CA', '91950'),
('Rebecca', 'Collins', '1234567935', '555-2045', 'rcollins@lemongrovefp.com', '4600 Broadway', 'Lemon Grove', 'CA', '91945'),
('Jason', 'Stewart', '1234567936', '555-2046', 'jstewart@springvalleymed.com', '4700 Campo Rd', 'Spring Valley', 'CA', '91977'),
('Laura', 'Sanchez', '1234567937', '555-2047', 'lsanchez@lakesideclinic.com', '4800 Maine Ave', 'Lakeside', 'CA', '92040'),
('Jeffrey', 'Morris', '1234567938', '555-2048', 'jmorris@santeefamily.com', '4900 Mission Gorge Rd', 'Santee', 'CA', '92071'),
('Stephanie', 'Rogers', '1234567939', '555-2049', 'srogers@elcajonhospital.org', '5000 Lexington Ave', 'El Cajon', 'CA', '92020');

INSERT INTO prescriber_facility (prescriber_id, facility_id) VALUES
(1, 1), (2, 2), (3, 3), (4, 4), (5, 5), (6, 6), (7, 7), (8, 8), (9, 9), (10, 10),
(11, 11), (12, 12), (13, 13), (14, 14), (15, 15), (16, 16), (17, 17), (18, 18), (19, 19), (20, 20),
(21, 21), (22, 22), (23, 23), (24, 24), (25, 25), (26, 26), (27, 27), (28, 28), (29, 29), (30, 30),
(31, 31), (32, 32), (33, 33), (34, 34), (35, 35), (36, 36), (37, 37), (38, 38), (39, 39), (40, 40),
(41, 41), (42, 42), (43, 43), (44, 44), (45, 45), (46, 46), (47, 47), (48, 48), (49, 49), (50, 50);

INSERT INTO patient (first_name, last_name, date_of_birth, sex, phone, email, address, city, state, zip_code) VALUES
('Alexander', 'Thompson', '1985-03-12', 'M', '555-3000', 'alexander.thompson@email.com', '100 Oak Street', 'San Diego', 'CA', '92101'),
('Sophia', 'Martinez', '1992-07-28', 'F', '555-3001', 'sophia.martinez@email.com', '200 Pine Avenue', 'San Diego', 'CA', '92102'),
('Ethan', 'Rodriguez', '1978-11-05', 'M', '555-3002', 'ethan.rodriguez@email.com', '300 Elm Drive', 'Chula Vista', 'CA', '91910'),
('Isabella', 'Hernandez', '1989-01-19', 'F', '555-3003', 'isabella.hernandez@email.com', '400 Maple Lane', 'La Mesa', 'CA', '91941'),
('Mason', 'Garcia', '1965-09-14', 'M', '555-3004', 'mason.garcia@email.com', '500 Cedar Road', 'San Diego', 'CA', '92101'),
('Olivia', 'Lopez', '1995-05-22', 'F', '555-3005', 'olivia.lopez@email.com', '600 Birch Street', 'Oceanside', 'CA', '92054'),
('Lucas', 'Gonzalez', '1982-12-08', 'M', '555-3006', 'lucas.gonzalez@email.com', '700 Willow Way', 'Imperial Beach', 'CA', '91932'),
('Ava', 'Perez', '1991-04-17', 'F', '555-3007', 'ava.perez@email.com', '800 Ash Avenue', 'Alpine', 'CA', '91901'),
('Logan', 'Sanchez', '1973-08-26', 'M', '555-3008', 'logan.sanchez@email.com', '900 Poplar Drive', 'San Diego', 'CA', '92107'),
('Emma', 'Rivera', '1987-06-11', 'F', '555-3009', 'emma.rivera@email.com', '1000 Spruce Lane', 'Encinitas', 'CA', '92024'),
('Noah', 'Torres', '1998-02-03', 'M', '555-3010', 'noah.torres@email.com', '1100 Fir Street', 'Escondido', 'CA', '92025'),
('Mia', 'Flores', '1970-10-29', 'F', '555-3011', 'mia.flores@email.com', '1200 Cypress Road', 'Julian', 'CA', '92036'),
('Liam', 'Ramirez', '1984-07-15', 'M', '555-3012', 'liam.ramirez@email.com', '1300 Palm Avenue', 'El Centro', 'CA', '92243'),
('Charlotte', 'Morales', '1993-11-20', 'F', '555-3013', 'charlotte.morales@email.com', '1400 Oak Drive', 'Chula Vista', 'CA', '91911'),
('Benjamin', 'Ortiz', '1979-05-07', 'M', '555-3014', 'benjamin.ortiz@email.com', '1500 Pine Street', 'San Diego', 'CA', '92105'),
('Amelia', 'Gutierrez', '1986-09-18', 'F', '555-3015', 'amelia.gutierrez@email.com', '1600 Elm Avenue', 'San Diego', 'CA', '92104'),
('William', 'Chavez', '1962-01-25', 'M', '555-3016', 'william.chavez@email.com', '1700 Maple Lane', 'San Diego', 'CA', '92103'),
('Harper', 'Ramos', '1997-03-14', 'F', '555-3017', 'harper.ramos@email.com', '1800 Cedar Drive', 'San Diego', 'CA', '92108'),
('James', 'Herrera', '1981-12-01', 'M', '555-3018', 'james.herrera@email.com', '1900 Birch Street', 'San Diego', 'CA', '92161'),
('Evelyn', 'Medina', '1994-08-09', 'F', '555-3019', 'evelyn.medina@email.com', '2000 Willow Road', 'San Diego', 'CA', '92109'),
('Oliver', 'Castro', '1976-04-23', 'M', '555-3020', 'oliver.castro@email.com', '2100 Ash Avenue', 'San Diego', 'CA', '92106'),
('Abigail', 'Vargas', '1988-06-30', 'F', '555-3021', 'abigail.vargas@email.com', '2200 Poplar Lane', 'San Diego', 'CA', '92103'),
('Elijah', 'Moreno', '1967-02-16', 'M', '555-3022', 'elijah.moreno@email.com', '2300 Spruce Drive', 'San Diego', 'CA', '92123'),
('Emily', 'Jimenez', '1999-10-05', 'F', '555-3023', 'emily.jimenez@email.com', '2400 Fir Street', 'La Jolla', 'CA', '92093'),
('Henry', 'Ruiz', '1983-07-21', 'M', '555-3024', 'henry.ruiz@email.com', '2500 Cypress Avenue', 'San Diego', 'CA', '92134'),
('Madison', 'Fernandez', '1990-11-12', 'F', '555-3025', 'madison.fernandez@email.com', '2600 Palm Road', 'La Jolla', 'CA', '92037'),
('Sebastian', 'Silva', '1977-05-28', 'M', '555-3026', 'sebastian.silva@email.com', '2700 Oak Lane', 'San Diego', 'CA', '92123'),
('Avery', 'Santos', '1985-09-04', 'F', '555-3027', 'avery.santos@email.com', '2800 Pine Drive', 'La Jolla', 'CA', '92037'),
('Jack', 'Guerrero', '1964-03-19', 'M', '555-3028', 'jack.guerrero@email.com', '2900 Elm Street', 'San Diego', 'CA', '92123'),
('Ella', 'Ortega', '1996-01-08', 'F', '555-3029', 'ella.ortega@email.com', '3000 Maple Avenue', 'La Mesa', 'CA', '91942'),
('Aiden', 'Delgado', '1980-08-27', 'M', '555-3030', 'aiden.delgado@email.com', '3100 Cedar Lane', 'Poway', 'CA', '92064'),
('Madison', 'Cabrera', '1992-04-15', 'F', '555-3031', 'madison.cabrera@email.com', '3200 Birch Road', 'Oceanside', 'CA', '92056'),
('Matthew', 'Pena', '1971-12-22', 'M', '555-3032', 'matthew.pena@email.com', '3300 Willow Street', 'El Centro', 'CA', '92243'),
('Scarlett', 'Munoz', '1989-06-10', 'F', '555-3033', 'scarlett.munoz@email.com', '3400 Ash Drive', 'El Centro', 'CA', '92243'),
('Samuel', 'Herrera', '1968-10-31', 'M', '555-3034', 'samuel.herrera@email.com', '3500 Poplar Avenue', 'National City', 'CA', '91950'),
('Victoria', 'Leon', '1995-02-17', 'F', '555-3035', 'victoria.leon@email.com', '3600 Spruce Lane', 'Chula Vista', 'CA', '91910'),
('Jackson', 'Maldonado', '1984-09-06', 'M', '555-3036', 'jackson.maldonado@email.com', '3700 Fir Road', 'Coronado', 'CA', '92118'),
('Aria', 'Estrada', '1991-05-24', 'F', '555-3037', 'aria.estrada@email.com', '3800 Cypress Street', 'Fallbrook', 'CA', '92028'),
('David', 'Vega', '1974-11-13', 'M', '555-3038', 'david.vega@email.com', '3900 Palm Drive', 'Ramona', 'CA', '92065'),
('Luna', 'Santiago', '1987-03-02', 'F', '555-3039', 'luna.santiago@email.com', '4000 Oak Avenue', 'Borrego Springs', 'CA', '92004'),
('Joseph', 'Dominguez', '1961-07-29', 'M', '555-3040', 'joseph.dominguez@email.com', '4100 Pine Lane', 'Campo', 'CA', '91906'),
('Grace', 'Contreras', '1998-01-18', 'F', '555-3041', 'grace.contreras@email.com', '4200 Elm Road', 'Tecate', 'CA', '91980'),
('Carter', 'Reyes', '1982-08-07', 'M', '555-3042', 'carter.reyes@email.com', '4300 Maple Street', 'San Ysidro', 'CA', '92173'),
('Chloe', 'Ramirez', '1993-04-25', 'F', '555-3043', 'chloe.ramirez@email.com', '4400 Cedar Avenue', 'Imperial Beach', 'CA', '91932'),
('Owen', 'Alvarez', '1976-12-14', 'M', '555-3044', 'owen.alvarez@email.com', '4500 Birch Drive', 'National City', 'CA', '91950'),
('Penelope', 'Romero', '1989-06-03', 'F', '555-3045', 'penelope.romero@email.com', '4600 Willow Lane', 'Lemon Grove', 'CA', '91945'),
('Wyatt', 'Gutierrez', '1969-10-21', 'M', '555-3046', 'wyatt.gutierrez@email.com', '4700 Ash Road', 'Spring Valley', 'CA', '91977'),
('Riley', 'Chavez', '1994-02-09', 'F', '555-3047', 'riley.chavez@email.com', '4800 Poplar Street', 'Lakeside', 'CA', '92040'),
('Jayden', 'Ramos', '1981-09-28', 'M', '555-3048', 'jayden.ramos@email.com', '4900 Spruce Avenue', 'Santee', 'CA', '92071'),
('Lily', 'Herrera', '1997-05-16', 'F', '555-3049', 'lily.herrera@email.com', '5000 Fir Drive', 'El Cajon', 'CA', '92020');

INSERT INTO medication (generic_name, brand_name, dosage_form) VALUES
('Lisinopril', 'Prinivil', 'Tablet'),
('Amoxicillin', 'Amoxil', 'Capsule'),
('Ibuprofen', 'Advil', 'Tablet'),
('Acetaminophen', 'Tylenol', 'Tablet'),
('Omeprazole', 'Prilosec', 'Capsule'),
('Simvastatin', 'Zocor', 'Tablet'),
('Metformin', 'Glucophage', 'Tablet'),
('Hydrochlorothiazide', 'Microzide', 'Tablet'),
('Prednisone', 'Deltasone', 'Tablet'),
('Warfarin', 'Coumadin', 'Tablet'),
('Clopidogrel', 'Plavix', 'Tablet'),
('Sertraline', 'Zoloft', 'Tablet'),
('Citalopram', 'Celexa', 'Tablet'),
('Fluoxetine', 'Prozac', 'Capsule'),
('Escitalopram', 'Lexapro', 'Tablet'),
('Levothyroxine', 'Synthroid', 'Tablet'),
('Amlodipine', 'Norvasc', 'Tablet'),
('Losartan', 'Cozaar', 'Tablet'),
('Gabapentin', 'Neurontin', 'Capsule'),
('Tramadol', 'Ultram', 'Tablet'),
('Albuterol', 'Ventolin', 'Inhaler'),
('Fluticasone', 'Flovent', 'Inhaler'),
('Montelukast', 'Singulair', 'Tablet'),
('Cetirizine', 'Zyrtec', 'Tablet'),
('Loratadine', 'Claritin', 'Tablet'),
('Fexofenadine', 'Allegra', 'Tablet'),
('Diphenhydramine', 'Benadryl', 'Capsule'),
('Ranitidine', 'Zantac', 'Tablet'),
('Pantoprazole', 'Protonix', 'Tablet'),
('Esomeprazole', 'Nexium', 'Capsule'),
('Alprazolam', 'Xanax', 'Tablet'),
('Lorazepam', 'Ativan', 'Tablet'),
('Diazepam', 'Valium', 'Tablet'),
('Clonazepam', 'Klonopin', 'Tablet'),
('Temazepam', 'Restoril', 'Capsule'),
('Zolpidem', 'Ambien', 'Tablet'),
('Trazodone', 'Desyrel', 'Tablet'),
('Mirtazapine', 'Remeron', 'Tablet'),
('Bupropion', 'Wellbutrin', 'Tablet'),
('Duloxetine', 'Cymbalta', 'Capsule'),
('Venlafaxine', 'Effexor', 'Capsule'),
('Paroxetine', 'Paxil', 'Tablet'),
('Citalopram', 'Celexa', 'Solution'),
('Quetiapine', 'Seroquel', 'Tablet'),
('Risperidone', 'Risperdal', 'Tablet'),
('Olanzapine', 'Zyprexa', 'Tablet'),
('Aripiprazole', 'Abilify', 'Tablet'),
('Lamotrigine', 'Lamictal', 'Tablet'),
('Topiramate', 'Topamax', 'Tablet'),
('Levetiracetam', 'Keppra', 'Tablet'),
('Hepatitis A Vaccine (Inactivated)', 'Havrix', 'Injection'),
('Hepatitis B Vaccine (Recombinant)', 'Engerix-B', 'Injection'),
('SARS-CoV-2 mRNA Vaccine (Pfizer-BioNTech)', 'Comirnaty', 'Injection'),
('SARS-CoV-2 mRNA Vaccine (Moderna)', 'Spikevax', 'Injection'),
('Recombinant Zoster Vaccine', 'Shingrix', 'Injection'),
('Influenza Virus Vaccine, Quadrivalent', 'Fluzone', 'Injection'),
('Pneumococcal 20-valent Conjugate Vaccine', 'Prevnar 20', 'Injection'),
('Human Papillomavirus 9-valent Vaccine', 'Gardasil 9', 'Injection'),
('Varicella Virus Vaccine Live', 'Varivax', 'Injection'),
('SARS-CoV-2 mRNA Vaccine (Moderna)', 'Spikevax', 'Injection'),
('Measles, Mumps and Rubella Vaccine', 'M-M-R II', 'Injection'),
('Tetanus and Diphtheria Toxoids', 'Tenivac', 'Injection'),
('Tetanus, Diphtheria, Acellular Pertussis Vaccine', 'Adacel', 'Injection');

INSERT INTO product (ndc, name, generic_name, manufacturer, dosage_form, strength, is_rx, is_vaccine, unit_price, stock_qty, medication_id) VALUES
('00002030401', 'Lisinopril 10mg', 'Lisinopril', 'Aurobindo', 'Tablet', '10mg', true, false, 0.50, 100, 1),
('00071080401', 'Amoxicillin 500mg', 'Amoxicillin', 'GlaxoSmithKline', 'Capsule', '500mg', true, false, 1.25, 50, 2),
('00045080401', 'Ibuprofen 200mg', 'Ibuprofen', 'Johnson & Johnson', 'Tablet', '200mg', false, false, 0.25, 200, 3),
('00067030401', 'Acetaminophen 500mg', 'Acetaminophen', 'Johnson & Johnson', 'Tablet', '500mg', false, false, 0.15, 300, 4),
('00008070401', 'Omeprazole 20mg', 'Omeprazole', 'AstraZeneca', 'Capsule', '20mg', true, false, 2.00, 75, 5),
('00025030401', 'Simvastatin 20mg', 'Simvastatin', 'Merck', 'Tablet', '20mg', true, false, 0.75, 120, 6),
('00069030401', 'Metformin 500mg', 'Metformin', 'Bristol-Myers Squibb', 'Tablet', '500mg', true, false, 0.40, 150, 7),
('00093040401', 'Hydrochlorothiazide 25mg', 'Hydrochlorothiazide', 'PharmaCorp USA', 'Tablet', '25mg', true, false, 0.30, 180, 8),
('55501000901', 'Hepatitis A (Adult) 1 mL','Hepatitis A Vaccine (Inactivated)','GSK','Injection','1 mL', true, true, 75.00, 50, 9),
('00054050401', 'Prednisone 10mg', 'Prednisone', 'PharmaCorp USA', 'Tablet', '10mg', true, false, 0.60, 90, 10),
('00056020401', 'Warfarin 5mg', 'Warfarin Sodium', 'Bristol-Myers Squibb', 'Tablet', '5mg', true, false, 1.50, 60, 11),
('55501000801', 'Hepatitis B (Adult) 1 mL','Hepatitis B Vaccine (Recombinant)','GSK','Injection','1 mL', true, true, 62.00, 85, 12),
('55501000201', 'COVID-19 mRNA (Pfizer) 0.3 mL','SARS-CoV-2 mRNA Vaccine (Pfizer-BioNTech)','Pfizer','Injection','0.3 mL', true, true, 95.00, 80, 13),
('55501000601', 'COVID-19 mRNA (Moderna) 0.3 mL','SARS-CoV-2 mRNA Vaccine (Moderna)','Moderna','Injection','0.3 mL', true, true, 95.00, 80, 14),
('00071000401', 'Clopidogrel 75mg', 'Clopidogrel', 'Sanofi', 'Tablet', '75mg', true, false, 2.25, 45, 15),
('00031010401', 'Sertraline 50mg', 'Sertraline HCl', 'Pfizer', 'Tablet', '50mg', true, false, 1.80, 80, 16),
('55501001201', 'Zoster (Shingrix) 0.5 mL','Recombinant Zoster Vaccine','GSK','Injection (IM, 2-dose)','0.5 mL', true, true, 175.00, 55, 17),
('00078000401', 'Citalopram 20mg', 'Citalopram', 'PharmaCorp USA', 'Tablet', '20mg', true, false, 0.90, 110, 18),
('00013000401', 'Fluoxetine 20mg', 'Fluoxetine HCl', 'PharmaCorp USA', 'Capsule', '20mg', true, false, 1.20, 70, 19),
('00035020401', 'Escitalopram 10mg', 'Escitalopram', 'PharmaCorp USA', 'Tablet', '10mg', true, false, 1.60, 55, 20),
('00068000401', 'Levothyroxine 50mcg', 'Levothyroxine Sodium', 'AbbVie', 'Tablet', '50mcg', true, false, 0.85, 140, 21),
('55501000101', 'Influenza Quad 0.5 mL','Influenza Virus Vaccine, Quadrivalent','Sanofi Pasteur','Injection','0.5 mL', true, true, 25.00, 120, 22),
('00022000401', 'Amlodipine 5mg', 'Amlodipine Besylate', 'Pfizer', 'Tablet', '5mg', true, true, 1.10, 95, 23),
('00017000401', 'Losartan 50mg', 'Losartan Potassium', 'Merck', 'Tablet', '50mg', true, false, 1.35, 85, 24),
('00097000401', 'Gabapentin 300mg', 'Gabapentin', 'Pfizer', 'Capsule', '300mg', true, false, 0.95, 130, 25),
('00004000401', 'Tramadol 50mg', 'Tramadol HCl', 'PharmaCorp USA', 'Tablet', '50mg', true, false, 0.70, 160, 26),
('55501001101', 'Pneumococcal PCV20 0.5 mL','Pneumococcal 20-valent Conjugate Vaccine','Pfizer','Injection','0.5 mL', true, true, 195.00, 30, 27),
('00085020401', 'Albuterol Inhaler', 'Albuterol', 'GlaxoSmithKline', 'Inhaler', '90mcg', true, false, 45.00, 25, 28),
('00012000401', 'Fluticasone Inhaler', 'Fluticasone', 'GlaxoSmithKline', 'Inhaler', '110mcg', true, false, 120.00, 20, 29),
('00015000401', 'Montelukast 10mg', 'Montelukast', 'Merck', 'Tablet', '10mg', true, false, 8.50, 40, 30),
('00016000401', 'Cetirizine 10mg', 'Cetirizine', 'PharmaCorp USA', 'Tablet', '10mg', false, false, 0.35, 250, 31),
('00017000901', 'Loratadine 10mg', 'Loratadine', 'PharmaCorp USA', 'Tablet', '10mg', false, false, 0.45, 220, 32),
('00018000401', 'Fexofenadine 180mg', 'Fexofenadine', 'PharmaCorp USA', 'Tablet', '180mg', false, false, 0.65, 180, 33),
('00019000401', 'Diphenhydramine 25mg', 'Diphenhydramine', 'PharmaCorp USA', 'Capsule', '25mg', false, false, 0.20, 300, 34),
('00020000401', 'Ranitidine 150mg', 'Ranitidine', 'PharmaCorp USA', 'Tablet', '150mg', false, false, 0.55, 190, 35),
('00021000401', 'Pantoprazole 40mg', 'Pantoprazole', 'PharmaCorp USA', 'Tablet', '40mg', true, false, 1.75, 85, 36),
('00022500401', 'Esomeprazole 40mg', 'Esomeprazole', 'AstraZeneca', 'Capsule', '40mg', true, false, 3.20, 65, 37),
('55501001001', 'HPV 9-valent 0.5 mL','Human Papillomavirus 9-valent Vaccine','Merck','Injection','0.5 mL', true, true, 210.00, 45, 38),
('00029000401', 'Alprazolam 0.5mg', 'Alprazolam', 'PharmaCorp USA', 'Tablet', '0.5mg', true, false, 0.80, 120, 39),
('00030000401', 'Lorazepam 1mg', 'Lorazepam', 'PharmaCorp USA', 'Tablet', '1mg', true, false, 0.90, 100, 40),
('00031000401', 'Diazepam 5mg', 'Diazepam', 'PharmaCorp USA', 'Tablet', '5mg', true, false, 1.05, 90, 41),
('00032000401', 'Clonazepam 1mg', 'Clonazepam', 'PharmaCorp USA', 'Tablet', '1mg', true, false, 1.15, 80, 42),
('00033000401', 'Temazepam 15mg', 'Temazepam', 'PharmaCorp USA', 'Capsule', '15mg', true, false, 0.85, 110, 43),
('55501000701', 'Varicella 0.5 mL','Varicella Virus Vaccine Live','Merck','Injection (SubQ)','0.5 mL', true, true, 135.00, 35, 44),
('00034000401', 'Zolpidem 10mg', 'Zolpidem', 'PharmaCorp USA', 'Tablet', '10mg', true, false, 1.25, 75, 45),
('00035000401', 'Trazodone 50mg', 'Trazodone', 'PharmaCorp USA', 'Tablet', '50mg', true, false, 0.95, 130, 46),
('00036000401', 'Mirtazapine 15mg', 'Mirtazapine', 'PharmaCorp USA', 'Tablet', '15mg', true, false, 1.40, 70, 47),
('00037000401', 'Bupropion 150mg', 'Bupropion', 'PharmaCorp USA', 'Tablet', '150mg', true, false, 1.60, 60, 48),
('00038000401', 'Duloxetine 30mg', 'Duloxetine', 'PharmaCorp USA', 'Capsule', '30mg', true, false, 2.80, 50, 49),
('55501000301', 'COVID-19 mRNA (Moderna) 0.5 mL','SARS-CoV-2 mRNA Vaccine (Moderna)','Moderna','Injection','0.5 mL', true, true, 105.00, 70, 50),
('00039000401', 'Venlafaxine 75mg', 'Venlafaxine', 'PharmaCorp USA', 'Capsule', '75mg', true, false, 1.90, 85, 51),
('00040000401', 'Paroxetine 20mg', 'Paroxetine', 'PharmaCorp USA', 'Tablet', '20mg', true, false, 1.30, 95, 52),
('00041000401', 'Citalopram Solution', 'Citalopram', 'PharmaCorp USA', 'Solution', '10mg', true, false, 2.10, 45, 53),
('00042000401', 'Quetiapine 25mg', 'Quetiapine', 'PharmaCorp USA', 'Tablet', '25mg', true, false, 1.70, 80, 54),
('00043000401', 'Risperidone 1mg', 'Risperidone', 'PharmaCorp USA', 'Tablet', '1mg', true, false, 2.30, 65, 55),
('00044000401', 'Olanzapine 5mg', 'Olanzapine', 'PharmaCorp USA', 'Tablet', '5mg', true, false, 3.50, 55, 56),
('55501001601', 'MMR 0.5 mL','Measles, Mumps and Rubella Vaccine','Merck','Injection','0.5 mL', true, true, 78.00, 40, 57),
('00045000401', 'Aripiprazole 10mg', 'Aripiprazole', 'PharmaCorp USA', 'Tablet', '10mg', true, false, 4.20, 40, 58),
('55501000501', 'Td 0.5 mL','Tetanus and Diphtheria Toxoids','Sanofi Pasteur','Injection','0.5 mL', true, true, 28.00, 60, 59),
('55501000401', 'Tdap 0.5 mL','Tetanus, Diphtheria, Acellular Pertussis Vaccine','GSK','Injection','0.5 mL', true, true, 52.00, 90, 60),
('00046000401', 'Lamotrigine 100mg', 'Lamotrigine', 'PharmaCorp USA', 'Tablet', '100mg', true, false, 1.80, 75, 61),
('00047000401', 'Topiramate 50mg', 'Topiramate', 'PharmaCorp USA', 'Tablet', '50mg', true, false, 1.45, 90, 62),
('00048000401', 'Levetiracetam 500mg', 'Levetiracetam', 'PharmaCorp USA', 'Tablet', '500mg', true, false, 2.60, 60, 63);


INSERT INTO product_lot (product_id, lot_number, expiration_date, quantity) VALUES
(1, 'LOT001', '2025-12-31', 100), (2, 'LOT002', '2025-11-30', 50), (3, 'LOT003', '2025-10-31', 200), (4, 'LOT004', '2025-09-30', 300), (5, 'LOT005', '2025-08-31', 75),
(6, 'LOT006', '2025-07-31', 120), (7, 'LOT007', '2025-06-30', 150), (8, 'LOT008', '2025-05-31', 180), (9, 'LOT009', '2025-04-30', 90), (10, 'LOT010', '2025-03-31', 60),
(11, 'LOT011', '2025-02-28', 45), (12, 'LOT012', '2025-01-31', 80), (13, 'LOT013', '2024-12-31', 110), (14, 'LOT014', '2024-11-30', 70), (15, 'LOT015', '2024-10-31', 55),
(16, 'LOT016', '2024-09-30', 140), (17, 'LOT017', '2024-08-31', 95), (18, 'LOT018', '2024-07-31', 85), (19, 'LOT019', '2024-06-30', 130), (20, 'LOT020', '2024-05-31', 160),
(21, 'LOT021', '2024-04-30', 25), (22, 'LOT022', '2024-03-31', 20), (23, 'LOT023', '2024-02-29', 40), (24, 'LOT024', '2024-01-31', 250), (25, 'LOT025', '2023-12-31', 220),
(26, 'LOT026', '2023-11-30', 180), (27, 'LOT027', '2023-10-31', 300), (28, 'LOT028', '2023-09-30', 190), (29, 'LOT029', '2023-08-31', 85), (30, 'LOT030', '2023-07-31', 65),
(31, 'LOT031', '2023-06-30', 120), (32, 'LOT032', '2023-05-31', 100), (33, 'LOT033', '2023-04-30', 90), (34, 'LOT034', '2023-03-31', 80), (35, 'LOT035', '2023-02-28', 110),
(36, 'LOT036', '2023-01-31', 75), (37, 'LOT037', '2022-12-31', 130), (38, 'LOT038', '2022-11-30', 70), (39, 'LOT039', '2022-10-31', 60), (40, 'LOT040', '2022-09-30', 50),
(41, 'LOT041', '2022-08-31', 85), (42, 'LOT042', '2022-07-31', 95), (43, 'LOT043', '2022-06-30', 45), (44, 'LOT044', '2022-05-31', 80), (45, 'LOT045', '2022-04-30', 65),
(51, 'LOT051', '2024-02-28', 45), (52, 'LOT052', '2024-01-31', 80), (53, 'LOT053', '2024-12-31', 110), (54, 'LOT054', '2024-11-30', 70), (55, 'LOT055', '2024-10-31', 55),
(56, 'LOT056', '2024-09-30', 140), (57, 'LOT057', '2024-08-31', 95), (58, 'LOT058', '2024-07-31', 85), (59, 'LOT059', '2024-06-30', 130), (60, 'LOT060', '2024-05-31', 160),
(61, 'LOT061', '2024-04-30', 25), (62, 'LOT062', '2024-03-31', 20), (63, 'LOT063', '2024-02-29', 40);

INSERT INTO prescription (rx_number, patient_id, prescriber_id, facility_id, date_written, status) VALUES
('RX100001', 1, 1, 1, '2024-01-15', 'ACTIVE'), ('RX100002', 2, 2, 2, '2024-01-16', 'ACTIVE'), ('RX100003', 3, 3, 3, '2024-01-17', 'READY'), ('RX100004', 4, 4, 4, '2024-01-18', 'ACTIVE'),
('RX100005', 5, 5, 5, '2024-01-19', 'ACTIVE'), ('RX100006', 6, 6, 6, '2024-01-20', 'ACTIVE'), ('RX100007', 7, 7, 7, '2024-01-21', 'READY'), ('RX100008', 8, 8, 8, '2024-01-22', 'ACTIVE'),
('RX100009', 9, 9, 9, '2024-01-23', 'ACTIVE'), ('RX100010', 10, 10, 10, '2024-01-24', 'READY'), ('RX100011', 11, 11, 11, '2024-01-25', 'ACTIVE'), ('RX100012', 12, 12, 12, '2024-01-26', 'ACTIVE'),
('RX100013', 13, 13, 13, '2024-01-27', 'READY'), ('RX100014', 14, 14, 14, '2024-01-28', 'ACTIVE'), ('RX100015', 15, 15, 15, '2024-01-29', 'ACTIVE'), ('RX100016', 16, 16, 16, '2024-01-30', 'READY'),
('RX100017', 17, 17, 17, '2024-01-31', 'ACTIVE'), ('RX100018', 18, 18, 18, '2024-02-01', 'ACTIVE'), ('RX100019', 19, 19, 19, '2024-02-02', 'READY'), ('RX100020', 20, 20, 20, '2024-02-03', 'ACTIVE'),
('RX100021', 21, 21, 21, '2024-02-04', 'ACTIVE'), ('RX100022', 22, 22, 22, '2024-02-05', 'READY'), ('RX100023', 23, 23, 23, '2024-02-06', 'ACTIVE'), ('RX100024', 24, 24, 24, '2024-02-07', 'ACTIVE'),
('RX100025', 25, 25, 25, '2024-02-08', 'READY'), ('RX100026', 26, 26, 26, '2024-02-09', 'ACTIVE'), ('RX100027', 27, 27, 27, '2024-02-10', 'ACTIVE'), ('RX100028', 28, 28, 28, '2024-02-11', 'READY'),
('RX100029', 29, 29, 29, '2024-02-12', 'ACTIVE'), ('RX100030', 30, 30, 30, '2024-02-13', 'ACTIVE'), ('RX100031', 31, 31, 31, '2024-02-14', 'READY'), ('RX100032', 32, 32, 32, '2024-02-15', 'ACTIVE'),
('RX100033', 33, 33, 33, '2024-02-16', 'ACTIVE'), ('RX100034', 34, 34, 34, '2024-02-17', 'READY'), ('RX100035', 35, 35, 35, '2024-02-18', 'ACTIVE'), ('RX100036', 36, 36, 36, '2024-02-19', 'ACTIVE'),
('RX100037', 37, 37, 37, '2024-02-20', 'READY'), ('RX100038', 38, 38, 38, '2024-02-21', 'ACTIVE'), ('RX100039', 39, 39, 39, '2024-02-22', 'ACTIVE'), ('RX100040', 40, 40, 40, '2024-02-23', 'READY'),
('RX100041', 41, 41, 41, '2024-02-24', 'ACTIVE'), ('RX100042', 42, 42, 42, '2024-02-25', 'ACTIVE'), ('RX100043', 43, 43, 43, '2024-02-26', 'READY'), ('RX100044', 44, 44, 44, '2024-02-27', 'ACTIVE'),
('RX100045', 45, 45, 45, '2024-02-28', 'ACTIVE'), ('RX100046', 46, 46, 46, '2024-03-01', 'READY'), ('RX100047', 47, 47, 47, '2024-03-02', 'ACTIVE'), ('RX100048', 48, 48, 48, '2024-03-03', 'ACTIVE'),
('RX100049', 49, 49, 49, '2024-03-04', 'READY'), ('RX100050', 50, 50, 50, '2024-03-05', 'ACTIVE');

INSERT INTO prescription_item (prescription_id, medication_id, strength, dose, quantity, sig, daw, refills) VALUES
(1, 1, '10mg', '1 tablet', 30, 'Take 1 tablet by mouth once daily', '0', 5), (2, 2, '500mg', '1 capsule', 20, 'Take 1 capsule by mouth three times daily for 7 days', '0', 0),
(3, 3, '200mg', '1 tablet', 60, 'Take 1 tablet by mouth every 4-6 hours as needed for pain', '0', 2), (4, 4, '500mg', '1 tablet', 40, 'Take 1-2 tablets by mouth every 4 hours as needed for pain', '0', 3),
(5, 5, '20mg', '1 capsule', 30, 'Take 1 capsule by mouth once daily before meals', '0', 5), (6, 6, '20mg', '1 tablet', 30, 'Take 1 tablet by mouth once daily in the evening', '0', 5),
(7, 7, '500mg', '1 tablet', 60, 'Take 1 tablet by mouth twice daily with meals', '0', 5), (8, 8, '25mg', '1 tablet', 30, 'Take 1 tablet by mouth once daily in the morning', '0', 5),
(9, 9, '10mg', '1 tablet', 20, 'Take 1 tablet by mouth once daily as directed', '0', 2), (10, 10, '5mg', '1 tablet', 30, 'Take 1 tablet by mouth once daily as directed', '0', 5),
(11, 11, '75mg', '1 tablet', 30, 'Take 1 tablet by mouth once daily with food', '0', 5), (12, 12, '50mg', '1 tablet', 30, 'Take 1 tablet by mouth once daily', '0', 5),
(13, 13, '20mg', '1 tablet', 30, 'Take 1 tablet by mouth once daily', '0', 5), (14, 14, '20mg', '1 capsule', 30, 'Take 1 capsule by mouth once daily', '0', 5),
(15, 15, '10mg', '1 tablet', 30, 'Take 1 tablet by mouth once daily', '0', 5), (16, 16, '50mcg', '1 tablet', 30, 'Take 1 tablet by mouth once daily on empty stomach', '0', 5),
(17, 17, '5mg', '1 tablet', 30, 'Take 1 tablet by mouth once daily', '0', 5), (18, 18, '50mg', '1 tablet', 30, 'Take 1 tablet by mouth once daily', '0', 5),
(19, 19, '300mg', '1 capsule', 60, 'Take 1 capsule by mouth three times daily with food', '0', 5), (20, 20, '50mg', '1 tablet', 30, 'Take 1 tablet by mouth every 4-6 hours as needed for pain', '0', 5),
(21, 21, '90mcg', '2 puffs', 1, 'Inhale 2 puffs by mouth every 4-6 hours as needed', '0', 5), (22, 22, '110mcg', '2 puffs', 1, 'Inhale 2 puffs by mouth twice daily', '0', 5),
(23, 23, '10mg', '1 tablet', 30, 'Take 1 tablet by mouth once daily in the evening', '0', 5), (24, 24, '10mg', '1 tablet', 30, 'Take 1 tablet by mouth once daily as needed for allergies', '0', 5),
(25, 25, '10mg', '1 tablet', 30, 'Take 1 tablet by mouth once daily as needed for allergies', '0', 5), (26, 26, '180mg', '1 tablet', 30, 'Take 1 tablet by mouth once daily as needed for allergies', '0', 5),
(27, 27, '25mg', '1 capsule', 30, 'Take 1 capsule by mouth every 6 hours as needed for allergies', '0', 5), (28, 28, '150mg', '1 tablet', 30, 'Take 1 tablet by mouth twice daily', '0', 5),
(29, 29, '40mg', '1 tablet', 30, 'Take 1 tablet by mouth once daily', '0', 5), (30, 30, '40mg', '1 capsule', 30, 'Take 1 capsule by mouth once daily', '0', 5),
(31, 31, '0.5mg', '1 tablet', 30, 'Take 1 tablet by mouth three times daily as needed for anxiety', '0', 5), (32, 32, '1mg', '1 tablet', 30, 'Take 1 tablet by mouth three times daily as needed for anxiety', '0', 5),
(33, 33, '5mg', '1 tablet', 30, 'Take 1 tablet by mouth three times daily as needed for anxiety', '0', 5), (34, 34, '1mg', '1 tablet', 30, 'Take 1 tablet by mouth three times daily as needed for anxiety', '0', 5),
(35, 35, '15mg', '1 capsule', 30, 'Take 1 capsule by mouth at bedtime', '0', 5), (36, 36, '10mg', '1 tablet', 30, 'Take 1 tablet by mouth at bedtime', '0', 5),
(37, 37, '50mg', '1 tablet', 30, 'Take 1 tablet by mouth at bedtime', '0', 5), (38, 38, '15mg', '1 tablet', 30, 'Take 1 tablet by mouth at bedtime', '0', 5),
(39, 39, '150mg', '1 tablet', 30, 'Take 1 tablet by mouth twice daily', '0', 5), (40, 40, '30mg', '1 capsule', 30, 'Take 1 capsule by mouth once daily', '0', 5),
(41, 41, '75mg', '1 capsule', 30, 'Take 1 capsule by mouth once daily', '0', 5), (42, 42, '20mg', '1 tablet', 30, 'Take 1 tablet by mouth once daily', '0', 5),
(43, 43, '10mg/5ml', '5ml', 300, 'Take 5ml by mouth once daily', '0', 5), (44, 44, '25mg', '1 tablet', 30, 'Take 1 tablet by mouth once daily', '0', 5),
(45, 45, '1mg', '1 tablet', 30, 'Take 1 tablet by mouth once daily', '0', 5), (46, 46, '5mg', '1 tablet', 30, 'Take 1 tablet by mouth once daily', '0', 5),
(47, 47, '10mg', '1 tablet', 30, 'Take 1 tablet by mouth once daily', '0', 5), (48, 48, '100mg', '1 tablet', 30, 'Take 1 tablet by mouth twice daily', '0', 5),
(49, 49, '50mg', '1 tablet', 30, 'Take 1 tablet by mouth twice daily', '0', 5), (50, 50, '500mg', '1 tablet', 30, 'Take 1 tablet by mouth twice daily', '0', 5);

INSERT INTO fulfillment (prescription_id, product_id, lot_id, quantity_dispensed, filled_at, filled_by_user_id, verified_by_pharmacist_id, status) VALUES
(1, 1, 1, 30, '2024-01-15 10:30:00', 2, 1, 'VERIFIED'), (3, 3, 3, 60, '2024-01-17 14:15:00', 3, 1, 'VERIFIED'),
(5, 5, 5, 30, '2024-01-19 09:45:00', 2, 1, 'VERIFIED'), (7, 7, 7, 60, '2024-01-21 11:20:00', 3, 1, 'VERIFIED'),
(9, 9, 9, 20, '2024-01-23 16:30:00', 2, 1, 'VERIFIED'), (11, 11, 11, 30, '2024-01-25 13:15:00', 3, 1, 'VERIFIED'),
(13, 13, 13, 30, '2024-01-27 10:45:00', 2, 1, 'VERIFIED'), (15, 15, 15, 30, '2024-01-29 15:20:00', 3, 1, 'VERIFIED'),
(17, 17, 17, 30, '2024-01-31 12:10:00', 2, 1, 'VERIFIED'), (19, 19, 19, 60, '2024-02-02 14:30:00', 3, 1, 'VERIFIED'),
(21, 21, 21, 1, '2024-02-04 09:15:00', 2, 1, 'VERIFIED'), (23, 23, 23, 30, '2024-02-06 11:45:00', 3, 1, 'VERIFIED'),
(25, 25, 25, 30, '2024-02-08 16:20:00', 2, 1, 'VERIFIED'), (27, 27, 27, 30, '2024-02-10 13:30:00', 3, 1, 'VERIFIED'),
(29, 29, 29, 30, '2024-02-12 10:15:00', 2, 1, 'VERIFIED'), (31, 31, 31, 30, '2024-02-14 15:45:00', 3, 1, 'VERIFIED'),
(33, 33, 33, 30, '2024-02-16 12:20:00', 2, 1, 'VERIFIED'), (35, 35, 35, 30, '2024-02-18 14:10:00', 3, 1, 'VERIFIED'),
(37, 37, 37, 30, '2024-02-20 11:30:00', 2, 1, 'VERIFIED'), (39, 39, 39, 30, '2024-02-22 16:15:00', 3, 1, 'VERIFIED'),
(41, 41, 41, 30, '2024-02-24 13:45:00', 2, 1, 'VERIFIED'), (43, 43, 43, 300, '2024-02-26 10:20:00', 3, 1, 'VERIFIED'),
(45, 45, 45, 30, '2024-02-28 15:30:00', 2, 1, 'VERIFIED'), (47, 47, 47, 30, '2024-03-02 12:45:00', 3, 1, 'VERIFIED'),
(49, 49, 49, 30, '2024-03-04 14:20:00', 2, 1, 'VERIFIED'),
(50, 50, 50, 30, '2024-03-05 11:30:00', 3, 1, 'VERIFIED'),
(51, 51, 51, 1, '2024-03-06 09:15:00', 2, 1, 'VERIFIED'),
(52, 52, 52, 1, '2024-03-07 14:30:00', 3, 1, 'VERIFIED'),
(53, 53, 53, 1, '2024-03-08 10:45:00', 2, 1, 'VERIFIED'),
(54, 54, 54, 1, '2024-03-09 16:20:00', 3, 1, 'VERIFIED'),
(55, 55, 55, 1, '2024-03-10 13:15:00', 2, 1, 'VERIFIED'),
(56, 56, 56, 1, '2024-03-11 11:30:00', 3, 1, 'VERIFIED'),
(57, 57, 57, 1, '2024-03-12 15:45:00', 2, 1, 'VERIFIED'),
(58, 58, 58, 1, '2024-03-13 09:20:00', 3, 1, 'VERIFIED'),
(59, 59, 59, 1, '2024-03-14 14:10:00', 2, 1, 'VERIFIED'),
(60, 60, 60, 1, '2024-03-15 12:30:00', 3, 1, 'VERIFIED'),
(61, 61, 61, 1, '2024-03-16 10:15:00', 2, 1, 'VERIFIED'),
(62, 62, 62, 1, '2024-03-17 16:45:00', 3, 1, 'VERIFIED'),
(63, 63, 63, 1, '2024-03-18 11:20:00', 2, 1, 'VERIFIED');

INSERT INTO insurance_profile (patient_id, provider, member_id, group_number, bin_number, pcn, effective_from) VALUES
(1, 'Aetna', 'MEM001', 'GRP001', '123456', 'PCN001', '2024-01-01'), (2, 'Cigna', 'MEM002', 'GRP002', '234567', 'PCN002', '2024-01-01'),
(3, 'Blue Shield', 'MEM003', 'GRP003', '345678', 'PCN003', '2024-01-01'), (4, 'United Health', 'MEM004', 'GRP004', '456789', 'PCN004', '2024-01-01'),
(5, 'Kaiser Permanente', 'MEM005', 'GRP005', '567890', 'PCN005', '2024-01-01'), (6, 'Anthem Blue Cross', 'MEM006', 'GRP006', '678901', 'PCN006', '2024-01-01'),
(7, 'Humana', 'MEM007', 'GRP007', '789012', 'PCN007', '2024-01-01'), (8, 'Medicare', 'MEM008', 'GRP008', '890123', 'PCN008', '2024-01-01'),
(9, 'Medicaid', 'MEM009', 'GRP009', '901234', 'PCN009', '2024-01-01'), (10, 'Tricare', 'MEM010', 'GRP010', '012345', 'PCN010', '2024-01-01'),
(11, 'Aetna', 'MEM011', 'GRP011', '123456', 'PCN011', '2024-01-01'), (12, 'Cigna', 'MEM012', 'GRP012', '234567', 'PCN012', '2024-01-01'),
(13, 'Blue Shield', 'MEM013', 'GRP013', '345678', 'PCN013', '2024-01-01'), (14, 'United Health', 'MEM014', 'GRP014', '456789', 'PCN014', '2024-01-01'),
(15, 'Kaiser Permanente', 'MEM015', 'GRP015', '567890', 'PCN015', '2024-01-01'), (16, 'Anthem Blue Cross', 'MEM016', 'GRP016', '678901', 'PCN016', '2024-01-01'),
(17, 'Humana', 'MEM017', 'GRP017', '789012', 'PCN017', '2024-01-01'), (18, 'Medicare', 'MEM018', 'GRP018', '890123', 'PCN018', '2024-01-01'),
(19, 'Medicaid', 'MEM019', 'GRP019', '901234', 'PCN019', '2024-01-01'), (20, 'Tricare', 'MEM020', 'GRP020', '012345', 'PCN020', '2024-01-01'),
(21, 'Aetna', 'MEM021', 'GRP021', '123456', 'PCN021', '2024-01-01'), (22, 'Cigna', 'MEM022', 'GRP022', '234567', 'PCN022', '2024-01-01'),
(23, 'Blue Shield', 'MEM023', 'GRP023', '345678', 'PCN023', '2024-01-01'), (24, 'United Health', 'MEM024', 'GRP024', '456789', 'PCN024', '2024-01-01'),
(25, 'Kaiser Permanente', 'MEM025', 'GRP025', '567890', 'PCN025', '2024-01-01'), (26, 'Anthem Blue Cross', 'MEM026', 'GRP026', '678901', 'PCN026', '2024-01-01'),
(27, 'Humana', 'MEM027', 'GRP027', '789012', 'PCN027', '2024-01-01'), (28, 'Medicare', 'MEM028', 'GRP028', '890123', 'PCN028', '2024-01-01'),
(29, 'Medicaid', 'MEM029', 'GRP029', '901234', 'PCN029', '2024-01-01'), (30, 'Tricare', 'MEM030', 'GRP030', '012345', 'PCN030', '2024-01-01'),
(31, 'Aetna', 'MEM031', 'GRP031', '123456', 'PCN031', '2024-01-01'), (32, 'Cigna', 'MEM032', 'GRP032', '234567', 'PCN032', '2024-01-01'),
(33, 'Blue Shield', 'MEM033', 'GRP033', '345678', 'PCN033', '2024-01-01'), (34, 'United Health', 'MEM034', 'GRP034', '456789', 'PCN034', '2024-01-01'),
(35, 'Kaiser Permanente', 'MEM035', 'GRP035', '567890', 'PCN035', '2024-01-01'), (36, 'Anthem Blue Cross', 'MEM036', 'GRP036', '678901', 'PCN036', '2024-01-01'),
(37, 'Humana', 'MEM037', 'GRP037', '789012', 'PCN037', '2024-01-01'), (38, 'Medicare', 'MEM038', 'GRP038', '890123', 'PCN038', '2024-01-01'),
(39, 'Medicaid', 'MEM039', 'GRP039', '901234', 'PCN039', '2024-01-01'), (40, 'Tricare', 'MEM040', 'GRP040', '012345', 'PCN040', '2024-01-01'),
(41, 'Aetna', 'MEM041', 'GRP041', '123456', 'PCN041', '2024-01-01'), (42, 'Cigna', 'MEM042', 'GRP042', '234567', 'PCN042', '2024-01-01'),
(43, 'Blue Shield', 'MEM043', 'GRP043', '345678', 'PCN043', '2024-01-01'), (44, 'United Health', 'MEM044', 'GRP044', '456789', 'PCN044', '2024-01-01'),
(45, 'Kaiser Permanente', 'MEM045', 'GRP045', '567890', 'PCN045', '2024-01-01'), (46, 'Anthem Blue Cross', 'MEM046', 'GRP046', '678901', 'PCN046', '2024-01-01'),
(47, 'Humana', 'MEM047', 'GRP047', '789012', 'PCN047', '2024-01-01'), (48, 'Medicare', 'MEM048', 'GRP048', '890123', 'PCN048', '2024-01-01'),
(49, 'Medicaid', 'MEM049', 'GRP049', '901234', 'PCN049', '2024-01-01'), (50, 'Tricare', 'MEM050', 'GRP050', '012345', 'PCN050', '2024-01-01');
