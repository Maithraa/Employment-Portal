# KEC Employment & Placement Management System (Java JDBC + MariaDB/MySQL)

> **Kongu Engineering College (Autonomous) - Regulation 2024**  
> **Department of Information Technology**  
> **Course:** 24ITT32 - Programming in Java  
> **Project:** Individual Mini Project on Employment & Campus Recruitment Portal  

---

## 📌 Project Overview
The **Employment & Campus Placement Management System** is an enterprise-grade, database-driven Java application built using **Core Java, JDBC (Java Database Connectivity), and MariaDB / MySQL**. 

It simulates an end-to-end recruitment lifecycle:
1. Candidate profile registration with skills and qualifications.
2. Employer/Company on-boarding with industry classification and vacancy tracking.
3. Job posting and multifaceted filtering (by skill keyword, experience, location, CTC).
4. Direct application workflow with duplicate prevention.
5. Continuous multi-round interview evaluations (Technical, Coding, Communication, HR).
6. Comprehensive candidate evaluation scorecard with automated letter grade and CPI calculation.
7. Placement offer issuance, appointment tracking, and industry-level recruitment analytics.

---

## 🌟 Key Features
- **Job Seeker Management:** Register, update, search, and delete candidates. Captures education, skills, experience, and expected salary.
- **Employer / Company Registry:** Manage recruiting firms, industry domains, headquarters, contact points, and total openings.
- **Job Posting & Intelligent Search:** Post positions with experience thresholds and salary ranges. Search vacancies by skills and job location.
- **Application Processing:** Candidate job application with duplicate submission validation and initial status queueing.
- **Continuous Interview Evaluation:** Multi-criteria weighted assessment model:
  - Technical Evaluation (40%)
  - Coding & Problem Solving (30%)
  - Communication & Soft Skills (15%)
  - HR & Behavioral Assessment (15%)
- **Performance Marksheet & SGPA/CPI Rating:** Automated grading scale (O, A+, A, B+, B, C, RA) based on cumulative performance.
- **Placement & Offer Management:** Issue formal job offers with package (CTC in LPA) and joining dates.
- **Industry & Institutional Analytics:** Aggregates placement ratios, total openings, applicant volumes, and average CTC per sector.
- **Security & Integrity:** Parameterized SQL queries (`PreparedStatement`) to prevent SQL injection, JDBC transaction management (`commit`/`rollback`), and robust try-with-resources.

---

## 🛠️ System Requirements
- **JDK:** Java Development Kit (JDK 17 or JDK 21 recommended)
- **Database:** MariaDB Server (10.x+) or MySQL Server (8.0+)
- **JDBC Driver:** Included `lib/mariadb-java-client-3.5.11.jar` (also compatible with MySQL)
- **Terminal:** Windows PowerShell, Command Prompt, or Linux/macOS Shell

---

## 🚀 Setup & Execution Guide

### 1. Database Setup
1. Start your local MariaDB or MySQL service (e.g., via XAMPP, Laragon, or standalone service).
2. Open HeidiSQL, phpMyAdmin, DBeaver, or the MariaDB/MySQL CLI.
3. Import the database schema and sample data:
   ```sql
   SOURCE employment_schema.sql;
   ```
   *(The schema creates the database `employment_portal` with `CREATE TABLE IF NOT EXISTS` and uses `INSERT IGNORE` for idempotent seed data).*

### 2. Configure Database Credentials (Optional)
By default, the application connects to:
- **URL:** `jdbc:mariadb://localhost:3306/employment_portal`
- **User:** `root`
- **Password:** *(empty)*

If your database configuration differs, set the environment variables before running:
```powershell
# PowerShell
$env:EMP_DB_URL="jdbc:mariadb://localhost:3306/employment_portal"
$env:EMP_DB_USER="root"
$env:EMP_DB_PASSWORD="your_password"
```
Or in Command Prompt:
```cmd
set EMP_DB_URL=jdbc:mariadb://localhost:3306/employment_portal
set EMP_DB_USER=root
set EMP_DB_PASSWORD=your_password
```

### 3. Compilation
Open PowerShell in the project directory (`Employment-Portal`):
```powershell
javac -cp "lib/*" -d out src\*.java
```

### 4. Run the Application
Execute the compiled application:
```powershell
java -cp "out;lib/*" MainApp
```
*(On Linux/macOS use colon separator: `java -cp "out:lib/*" MainApp`)*

---

## 🧪 Recommended Testing Sequence
Follow this step-by-step sequence to verify all modules:

1. **Option 1:** View preloaded registered candidates (verify JS101 - JS105).
2. **Option 5:** View recruiting employers and industry classifications.
3. **Option 8:** View all job openings, then test keyword search (e.g. search "Java").
4. **Option 2:** Register a new test candidate (e.g., `JS106`, your name, degree, skills).
5. **Option 3:** Update candidate details (verify profile modification).
6. **Option 6:** Register a new recruiting company (e.g., `EMP106`, "Google Cloud").
7. **Option 7:** Post a new job vacancy for `EMP106` (e.g., `JOB207`).
8. **Option 9:** Submit job application: enroll candidate `JS106` for `JOB207`.
9. **Option 10:** View applications: verify that the new application is listed, then change status to `SHORTLISTED`.
10. **Option 11:** Record multi-round interview marks (Technical, Coding, Communication, HR scores).
11. **Option 12:** Display Candidate Marksheet: inspect the weighted percentage, letter grade, and evaluation breakdown.
12. **Option 13:** Issue Placement Offer: enter offered CTC and joining date; verify candidate status updates to `SELECTED`.
13. **Option 14:** Verify Industry & Placement Analytics: review company counts, applicant volume, and average CTC.
14. **Option 4:** Delete test candidate when finished to test deletion cascade.

---

## 📊 Evaluation & Grading Model
The continuous assessment system computes the overall interview score using a weighted distribution:
$$\text{Weighted Score} = (\text{Technical} \times 0.40) + (\text{Coding} \times 0.30) + (\text{Communication} \times 0.15) + (\text{HR} \times 0.15)$$

### Grade Boundaries:
- **O (Outstanding):** $\ge 90\%$ (Direct Placement Recommended)
- **A+ (Excellent):** $80\% - 89.9\%$
- **A (Very Good):** $70\% - 79.9\%$
- **B+ (Good):** $60\% - 69.9\%$ (Review / Conditional Offer)
- **B (Above Average):** $50\% - 59.9\%$
- **C (Pass):** $40\% - 49.9\%$
- **RA (Reappear):** $< 40\%$ (Needs Retraining / Not Qualified)

---

## 📁 Repository Structure
```
Employment-Portal/
├── lib/
│   └── mariadb-java-client-3.5.11.jar   # JDBC connector library
├── out/                                 # Compiled Java bytecode (.class)
│   ├── AnalyticsDAO.class
│   ├── ApplicationDAO.class
│   ├── DBConnection.class
│   ├── EmployerDAO.class
│   ├── InterviewDAO.class
│   ├── JobDAO.class
│   ├── JobSeekerDAO.class
│   └── MainApp.class
├── src/                                 # Java Source Code
│   ├── AnalyticsDAO.java
│   ├── ApplicationDAO.java
│   ├── DBConnection.java
│   ├── EmployerDAO.java
│   ├── InterviewDAO.java
│   ├── JobDAO.java
│   ├── JobSeekerDAO.java
│   └── MainApp.java
├── web/                                 # Previous web portal interface
│   ├── api/
│   ├── config.php
│   ├── database.sql
│   ├── index.html
│   ├── README.txt
│   ├── script.js
│   └── style.css
├── employment_schema.sql                # Complete relational DB schema with sample data
├── KEC-24ITT32-Java-Report-Employment-Portal.docx # Formal Project Report
├── REPORT.md                            # Comprehensive Project Documentation
└── README.md                            # Main project documentation
```

---

## 👨‍💻 Author & Academic Credits
- **Course:** 24ITT32 - Programming in Java
- **Institution:** Kongu Engineering College (Autonomous), Perundurai, Erode
- **Regulation:** R2024
