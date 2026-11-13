-- =====================================================================
-- Kongu Engineering College (Autonomous) - Regulation 2024
-- Department of Information Technology | 24ITT32 Programming in Java
-- Database Schema: Employment & Placement Management System (MariaDB / MySQL)
-- =====================================================================

CREATE DATABASE IF NOT EXISTS employment_portal CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE employment_portal;

-- 1. EMPLOYERS / RECRUITING COMPANIES
CREATE TABLE IF NOT EXISTS employers (
    employer_id VARCHAR(15) PRIMARY KEY,
    company_name VARCHAR(120) NOT NULL UNIQUE,
    industry VARCHAR(60) NOT NULL,
    location VARCHAR(80) NOT NULL,
    contact_email VARCHAR(100) NOT NULL,
    contact_phone VARCHAR(20),
    website VARCHAR(120)
) ENGINE=InnoDB;

-- 2. JOB SEEKERS / REGISTERED CANDIDATES
CREATE TABLE IF NOT EXISTS job_seekers (
    seeker_id VARCHAR(15) PRIMARY KEY,
    full_name VARCHAR(100) NOT NULL,
    email VARCHAR(100) NOT NULL UNIQUE,
    phone VARCHAR(20),
    qualification VARCHAR(60) NOT NULL,
    skills VARCHAR(255) NOT NULL,
    experience_years DECIMAL(3,1) NOT NULL DEFAULT 0.0,
    expected_salary_lpa DECIMAL(5,2) NOT NULL DEFAULT 0.0,
    CONSTRAINT chk_exp CHECK (experience_years >= 0),
    CONSTRAINT chk_salary CHECK (expected_salary_lpa >= 0)
) ENGINE=InnoDB;

-- 3. JOB VACANCIES / OPENINGS
CREATE TABLE IF NOT EXISTS jobs (
    job_id VARCHAR(15) PRIMARY KEY,
    employer_id VARCHAR(15) NOT NULL,
    job_title VARCHAR(100) NOT NULL,
    department VARCHAR(60) NOT NULL,
    job_type ENUM('FULL_TIME', 'PART_TIME', 'INTERNSHIP', 'REMOTE', 'CONTRACT') NOT NULL DEFAULT 'FULL_TIME',
    min_experience DECIMAL(3,1) NOT NULL DEFAULT 0.0,
    offered_salary_lpa DECIMAL(5,2) NOT NULL,
    required_skills VARCHAR(255) NOT NULL,
    location VARCHAR(80) NOT NULL,
    vacancies INT NOT NULL DEFAULT 1,
    status ENUM('OPEN', 'CLOSED', 'ON_HOLD') NOT NULL DEFAULT 'OPEN',
    posted_date DATE NOT NULL DEFAULT (CURRENT_DATE),
    CONSTRAINT fk_jobs_employer FOREIGN KEY (employer_id) REFERENCES employers(employer_id) ON DELETE CASCADE,
    CONSTRAINT chk_job_vacancies CHECK (vacancies >= 0),
    CONSTRAINT chk_job_salary CHECK (offered_salary_lpa >= 0)
) ENGINE=InnoDB;

-- 4. JOB APPLICATIONS
CREATE TABLE IF NOT EXISTS applications (
    application_id INT AUTO_INCREMENT PRIMARY KEY,
    seeker_id VARCHAR(15) NOT NULL,
    job_id VARCHAR(15) NOT NULL,
    applied_date DATE NOT NULL DEFAULT (CURRENT_DATE),
    cover_note VARCHAR(255),
    application_status ENUM('APPLIED', 'SHORTLISTED', 'INTERVIEW_SCHEDULED', 'SELECTED', 'REJECTED') NOT NULL DEFAULT 'APPLIED',
    CONSTRAINT fk_app_seeker FOREIGN KEY (seeker_id) REFERENCES job_seekers(seeker_id) ON DELETE CASCADE,
    CONSTRAINT fk_app_job FOREIGN KEY (job_id) REFERENCES jobs(job_id) ON DELETE CASCADE,
    CONSTRAINT uq_seeker_job UNIQUE (seeker_id, job_id)
) ENGINE=InnoDB;

-- 5. INTERVIEW EVALUATIONS & TECHNICAL MARKS
CREATE TABLE IF NOT EXISTS interview_evaluations (
    interview_id INT AUTO_INCREMENT PRIMARY KEY,
    application_id INT NOT NULL UNIQUE,
    technical_score DECIMAL(5,2) NOT NULL DEFAULT 0.0,
    coding_score DECIMAL(5,2) NOT NULL DEFAULT 0.0,
    communication_score DECIMAL(5,2) NOT NULL DEFAULT 0.0,
    hr_score DECIMAL(5,2) NOT NULL DEFAULT 0.0,
    interviewer_remarks VARCHAR(255),
    interview_result ENUM('PENDING', 'QUALIFIED', 'NOT_QUALIFIED') NOT NULL DEFAULT 'PENDING',
    CONSTRAINT fk_eval_app FOREIGN KEY (application_id) REFERENCES applications(application_id) ON DELETE CASCADE,
    CONSTRAINT chk_tech CHECK (technical_score BETWEEN 0 AND 100),
    CONSTRAINT chk_code CHECK (coding_score BETWEEN 0 AND 100),
    CONSTRAINT chk_comm CHECK (communication_score BETWEEN 0 AND 100),
    CONSTRAINT chk_hr CHECK (hr_score BETWEEN 0 AND 100)
) ENGINE=InnoDB;

-- 6. PLACEMENT OFFERS / APPOINTMENTS
CREATE TABLE IF NOT EXISTS placement_offers (
    offer_id INT AUTO_INCREMENT PRIMARY KEY,
    application_id INT NOT NULL UNIQUE,
    offered_ctc_lpa DECIMAL(5,2) NOT NULL,
    joining_date DATE,
    offer_status ENUM('OFFERED', 'ACCEPTED', 'DECLINED') NOT NULL DEFAULT 'OFFERED',
    CONSTRAINT fk_offer_app FOREIGN KEY (application_id) REFERENCES applications(application_id) ON DELETE CASCADE,
    CONSTRAINT chk_offer_ctc CHECK (offered_ctc_lpa >= 0)
) ENGINE=InnoDB;

-- =====================================================================
-- IDEMPOTENT SAMPLE DATA INSERTION (SAFE SETUP)
-- =====================================================================

-- Insert Employers
INSERT IGNORE INTO employers (employer_id, company_name, industry, location, contact_email, contact_phone, website) VALUES
('EMP101', 'Zoho Corporation', 'Software Development', 'Chennai', 'hr@zohocorp.com', '9840112233', 'https://www.zoho.com'),
('EMP102', 'Freshworks Inc', 'Cloud SaaS', 'Chennai', 'careers@freshworks.com', '9840223344', 'https://www.freshworks.com'),
('EMP103', 'Cognizant Technology', 'IT Consulting', 'Coimbatore', 'campus@cognizant.com', '9840334455', 'https://www.cognizant.com'),
('EMP104', 'TCS NextGen Labs', 'Enterprise Services', 'Bengaluru', 'recruitment@tcs.com', '9840445566', 'https://www.tcs.com'),
('EMP105', 'Kaar Technologies', 'ERP & SAP Consulting', 'Chennai', 'talent@kaartech.com', '9840556677', 'https://www.kaartech.com');

-- Insert Job Seekers
INSERT IGNORE INTO job_seekers (seeker_id, full_name, email, phone, qualification, skills, experience_years, expected_salary_lpa) VALUES
('JS101', 'Maithraan K', 'maithraan.25it@kongu.edu', '9876543210', 'B.Tech IT', 'Java, Spring Boot, MySQL, REST API', 1.0, 7.50),
('JS102', 'Swetha D', 'swetha.it@kongu.edu', '9876543211', 'B.Tech IT', 'Java, Python, MariaDB, React', 0.5, 6.50),
('JS103', 'Karthik Rajan', 'karthik.cse@example.com', '9876543212', 'B.E CSE', 'C++, Java, Data Structures, SQL', 0.0, 6.00),
('JS104', 'Priya Dharshini', 'priya.dev@example.com', '9876543213', 'MCA', 'Python, Machine Learning, SQL, Django', 1.5, 8.00),
('JS105', 'Arun Varma', 'arun.varma@example.com', '9876543214', 'B.E ECE', 'Embedded C, Java, IoT, SQL', 0.0, 5.50);

-- Insert Jobs
INSERT IGNORE INTO jobs (job_id, employer_id, job_title, department, job_type, min_experience, offered_salary_lpa, required_skills, location, vacancies, status) VALUES
('JOB201', 'EMP101', 'Junior Java Backend Developer', 'Engineering', 'FULL_TIME', 0.0, 6.50, 'Java, JDBC, SQL, Git', 'Chennai', 5, 'OPEN'),
('JOB202', 'EMP101', 'Cloud Software Trainee', 'Cloud Infrastructure', 'FULL_TIME', 0.0, 7.00, 'Java, Linux, MySQL, Docker', 'Chennai', 3, 'OPEN'),
('JOB203', 'EMP102', 'SaaS Product Engineer', 'Product Engineering', 'FULL_TIME', 1.0, 8.50, 'Java, REST APIs, Microservices', 'Chennai', 4, 'OPEN'),
('JOB204', 'EMP103', 'Graduate Trainee Engineer', 'Digital Technology', 'FULL_TIME', 0.0, 5.00, 'Java, SQL, Problem Solving', 'Coimbatore', 10, 'OPEN'),
('JOB205', 'EMP104', 'Associate Systems Engineer', 'Enterprise Systems', 'FULL_TIME', 0.0, 5.50, 'Java, Python, Database Management', 'Bengaluru', 8, 'OPEN'),
('JOB206', 'EMP105', 'SAP & Java Technical Consultant', 'Enterprise Solutions', 'INTERNSHIP', 0.0, 4.50, 'Core Java, SQL, Analytical Skills', 'Chennai', 2, 'OPEN');

-- Insert Applications
INSERT IGNORE INTO applications (application_id, seeker_id, job_id, applied_date, cover_note, application_status) VALUES
(1, 'JS101', 'JOB201', '2026-10-15', 'Strong interest in core backend Java engineering and database optimization.', 'SELECTED'),
(2, 'JS101', 'JOB203', '2026-10-16', 'Excited about microservices and scalable cloud architecture.', 'INTERVIEW_SCHEDULED'),
(3, 'JS102', 'JOB201', '2026-10-18', 'Passionate about JDBC database applications and Spring framework.', 'SHORTLISTED'),
(4, 'JS103', 'JOB204', '2026-10-20', 'Good knowledge of core data structures and relational queries.', 'SELECTED'),
(5, 'JS104', 'JOB202', '2026-10-22', 'Eager to leverage analytical skills and backend programming.', 'APPLIED'),
(6, 'JS105', 'JOB205', '2026-10-25', 'Keen on systems programming and enterprise solutions.', 'REJECTED');

-- Insert Interview Evaluations
INSERT IGNORE INTO interview_evaluations (interview_id, application_id, technical_score, coding_score, communication_score, hr_score, interviewer_remarks, interview_result) VALUES
(1, 1, 88.00, 92.00, 85.00, 90.00, 'Excellent command over Java multithreading, JDBC connectivity, and SQL optimization.', 'QUALIFIED'),
(2, 2, 78.00, 80.00, 82.00, 75.00, 'Sound fundamentals in Java OOP and APIs; round 2 scheduled.', 'PENDING'),
(3, 4, 82.00, 84.00, 80.00, 85.00, 'Good logic, clean code, cleared all technical hurdles.', 'QUALIFIED'),
(4, 6, 45.00, 40.00, 60.00, 55.00, 'Needs further improvement in Java database design and core queries.', 'NOT_QUALIFIED');

-- Insert Placement Offers
INSERT IGNORE INTO placement_offers (offer_id, application_id, offered_ctc_lpa, joining_date, offer_status) VALUES
(1, 1, 7.20, '2026-12-01', 'ACCEPTED'),
(2, 4, 5.25, '2026-12-15', 'OFFERED');
