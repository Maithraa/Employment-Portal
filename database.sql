CREATE DATABASE IF NOT EXISTS employment_portal CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE employment_portal;

CREATE TABLE IF NOT EXISTS users (
  id INT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
  name VARCHAR(120) NOT NULL,
  email VARCHAR(190) NOT NULL UNIQUE,
  password_hash VARCHAR(255) NOT NULL,
  role ENUM('seeker','employer') NOT NULL DEFAULT 'seeker',
  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS jobs (
  id INT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
  title VARCHAR(160) NOT NULL,
  company VARCHAR(160) NOT NULL,
  location VARCHAR(120) NOT NULL,
  salary VARCHAR(100) NOT NULL,
  job_type VARCHAR(50) NOT NULL,
  experience VARCHAR(50) NOT NULL,
  description TEXT NOT NULL,
  posted_by INT UNSIGNED NULL,
  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  CONSTRAINT fk_jobs_user FOREIGN KEY (posted_by) REFERENCES users(id) ON DELETE SET NULL
);

CREATE TABLE IF NOT EXISTS applications (
  id INT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
  user_id INT UNSIGNED NOT NULL,
  job_id INT UNSIGNED NOT NULL,
  applied_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  UNIQUE KEY unique_application (user_id, job_id),
  CONSTRAINT fk_app_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
  CONSTRAINT fk_app_job FOREIGN KEY (job_id) REFERENCES jobs(id) ON DELETE CASCADE
);

INSERT INTO jobs (title, company, location, salary, job_type, experience, description) VALUES
('Junior Web Developer','Bright Solutions','Chennai','₹3–5 LPA','Full-time','Fresher','Build and maintain responsive websites. HTML, CSS and JavaScript basics are useful.'),
('UI/UX Design Intern','Pixel Studio','Bengaluru','₹12,000/month','Internship','Fresher','Help create wireframes, prototypes and user-friendly designs.'),
('Customer Support Executive','Northstar Services','Coimbatore','₹2.5–3.5 LPA','Full-time','0–2 years','Assist customers, answer questions and record support requests.'),
('Data Analyst Trainee','Insight Labs','Remote','₹3–4 LPA','Remote','Fresher','Prepare reports using spreadsheets. Basic Excel and SQL are useful.'),
('Python Developer','CodeCraft Technologies','Chennai','₹4–7 LPA','Full-time','0–2 years','Develop Python applications, write tests and work with APIs.'),
('Java Developer Trainee','NextGen Systems','Madurai','₹3–5 LPA','Full-time','Fresher','Learn Java development and support application maintenance.'),
('Frontend Developer','BlueWave Digital','Bengaluru','₹4–8 LPA','Full-time','0–2 years','Create responsive interfaces using HTML, CSS and JavaScript.'),
('PHP Developer','WebNest Solutions','Coimbatore','₹3–6 LPA','Full-time','0–2 years','Build and maintain PHP websites and connect them to MySQL.'),
('Software Testing Intern','QualityFirst Labs','Chennai','₹10,000/month','Internship','Fresher','Test web applications, document issues and verify fixes.'),
('Digital Marketing Executive','MarketSpark','Salem','₹2.5–4 LPA','Full-time','Fresher','Support social media campaigns, content planning and performance reports.'),
('HR Recruiter','PeopleBridge HR','Chennai','₹2.8–4 LPA','Full-time','0–2 years','Coordinate interviews and help with candidate sourcing.'),
('Graphic Designer','Creative Canvas','Tiruchirappalli','₹3–5 LPA','Full-time','0–2 years','Create digital graphics and marketing materials.'),
('Business Analyst Intern','FinSight Consulting','Remote','₹15,000/month','Internship','Fresher','Support requirement gathering, documentation and spreadsheet analysis.'),
('SQL Database Trainee','DataBridge Systems','Bengaluru','₹3–5 LPA','Full-time','Fresher','Write SQL queries and help maintain relational databases.'),
('Mobile App Developer','AppOrbit','Chennai','₹4–8 LPA','Full-time','0–2 years','Assist with Android or cross-platform application development.'),
('Network Support Engineer','ConnectIT Services','Madurai','₹2.8–4.5 LPA','Full-time','0–2 years','Troubleshoot network connectivity and support IT infrastructure.'),
('Content Writer','WordWorks Media','Remote','₹2.5–4 LPA','Remote','Fresher','Research and write clear articles, web content and product copy.'),
('Accounts Assistant','PrimeLedger Finance','Coimbatore','₹2.2–3.2 LPA','Full-time','Fresher','Support invoice entry, reconciliations and basic accounting tasks.'),
('Cloud Support Associate','SkyStack Technologies','Bengaluru','₹4–7 LPA','Full-time','0–2 years','Assist with cloud service monitoring and technical support.'),
('Data Entry Operator','Accurate Office Solutions','Erode','₹1.8–2.8 LPA','Full-time','Fresher','Enter and verify records with accuracy and confidentiality.'),
('Project Coordinator','BuildPath Projects','Chennai','₹3.5–5.5 LPA','Full-time','0–2 years','Track project tasks, update reports and coordinate with team members.'),
('IT Help Desk Intern','CampusTech Support','Vellore','₹8,000/month','Internship','Fresher','Help users with common software, account and hardware issues.');
