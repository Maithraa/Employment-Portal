EMPLOYMENT PORTAL — PHP + MYSQL COLLEGE PROJECT
================================================

FEATURES
- 22 sample job listings in MySQL
- Job search by keyword/company and location
- Job seeker/employer registration
- Login/logout using PHP sessions
- Passwords hashed with PHP password_hash()
- Employers can post jobs
- Job seekers can apply to jobs
- Applications are stored in MySQL
- Duplicate applications are prevented

REQUIREMENTS
- XAMPP (Apache + MySQL/MariaDB + PHP)
- VS Code
- Browser

INSTALLATION STEPS
1. Install and open XAMPP.
2. Start Apache and MySQL from the XAMPP Control Panel.
3. Copy the entire EmploymentPortal_MySQL folder into:
   C:\xampp\htdocs\
   The final path should be:
   C:\xampp\htdocs\EmploymentPortal_MySQL\
4. Open this address in your browser:
   http://localhost/phpmyadmin
5. Select the Import tab.
6. Choose database.sql from the project folder and click Import/Go.
   The SQL script creates the employment_portal database, its 3 tables,
   and inserts 22 sample jobs.
7. If your MySQL root password is not blank, open config.php and update
   $password with your local MySQL password.
8. Open:
   http://localhost/EmploymentPortal_MySQL/
   Do NOT open index.html by double-clicking it; PHP API needs Apache.
9. Register a Job Seeker account and apply for a job.
10. Register another account as Employer and post a job.

DATABASE TABLES
- users: account details and password hashes
- jobs: job listing details
- applications: job seeker applications and applied date

TROUBLESHOOTING
- "Database connection failed": ensure MySQL is running and config.php
  credentials match your local setup; import database.sql.
- "Not Found": confirm the folder is inside C:\xampp\htdocs and the URL
  folder name matches.
- PHP pages show as text or download: use http://localhost/... with Apache,
  not file:// or VS Code's Live Server.
- If port 80 is in use, configure Apache's port or resolve the conflict.

SECURITY NOTE
This is a local educational prototype. Before public deployment, configure
HTTPS, CSRF protection, stricter rate limiting, production error handling,
and server security settings. Never use a demo setup with real user data.
