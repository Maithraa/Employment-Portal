import java.sql.Connection;
import java.sql.SQLException;
import java.util.Locale;
import java.util.Scanner;

/**
 * =====================================================================
 * KONGU ENGINEERING COLLEGE (AUTONOMOUS) - REGULATION 2024
 * DEPARTMENT OF INFORMATION TECHNOLOGY
 * 24ITT32 - PROGRAMMING IN JAVA (INDIVIDUAL MINI PROJECT)
 *
 * PROJECT TITLE : EMPLOYMENT & CAMPUS PLACEMENT MANAGEMENT SYSTEM
 * ARCHITECTURE  : JAVA JDBC (MariaDB / MySQL) + DAO DESIGN PATTERN
 * =====================================================================
 */
public class MainApp {
    private static final Scanner SC = new Scanner(System.in);

    private static final JobSeekerDAO seekerDAO = new JobSeekerDAO();
    private static final EmployerDAO employerDAO = new EmployerDAO();
    private static final JobDAO jobDAO = new JobDAO();
    private static final ApplicationDAO appDAO = new ApplicationDAO();
    private static final InterviewDAO interviewDAO = new InterviewDAO();
    private static final AnalyticsDAO analyticsDAO = new AnalyticsDAO();

    public static void main(String[] args) {
        System.out.println("=====================================================================");
        System.out.println("    KEC EMPLOYMENT & CAMPUS PLACEMENT MANAGEMENT SYSTEM (JAVA JDBC)   ");
        System.out.println("=====================================================================");

        try (Connection ignored = DBConnection.getConnection()) {
            System.out.println("[SUCCESS] Database connected successfully to employment_portal.");
        } catch (SQLException e) {
            System.out.println("[ERROR] Database connection failed: " + e.getMessage());
            System.out.println("Please check MariaDB/MySQL service and settings in README.md.");
            return;
        }

        int choice;
        do {
            printMenu();
            choice = readInt("Enter your choice (0-14): ", 0, 14);
            try {
                switch (choice) {
                    case 1 -> seekerDAO.displayAllJobSeekers();
                    case 2 -> registerJobSeeker();
                    case 3 -> updateJobSeeker();
                    case 4 -> deleteJobSeeker();
                    case 5 -> employerDAO.displayAllEmployers();
                    case 6 -> registerEmployer();
                    case 7 -> postJobVacancy();
                    case 8 -> searchAndFilterJobs();
                    case 9 -> applyForJob();
                    case 10 -> manageApplications();
                    case 11 -> recordInterviewEvaluation();
                    case 12 -> candidateScoreCard();
                    case 13 -> issuePlacementOffer();
                    case 14 -> analyticsDAO.displayIndustrySummary();
                    case 0 -> System.out.println("Exiting Employment Portal. Thank you!");
                    default -> System.out.println("Invalid option. Please choose between 0 and 14.");
                }
            } catch (SQLException e) {
                System.out.println("[DB ERROR] Operation failed: " + e.getMessage());
            }
        } while (choice != 0);
    }

    private static void printMenu() {
        System.out.println("\n=========================== MAIN MENU ===========================");
        System.out.println("  1.  Display All Registered Job Seekers / Candidates");
        System.out.println("  2.  Register New Candidate Profile");
        System.out.println("  3.  Update Candidate Profile");
        System.out.println("  4.  Delete Candidate Record");
        System.out.println("  5.  Display All Recruiting Companies / Employers");
        System.out.println("  6.  Register New Employer / Company");
        System.out.println("  7.  Post a New Job Vacancy");
        System.out.println("  8.  Browse & Filter Job Openings (by Skill / Location)");
        System.out.println("  9.  Submit Job Application (Candidate Enrollment)");
        System.out.println("  10. View Applications & Update Status (Shortlist/Reject)");
        System.out.println("  11. Record Interview Evaluation (Continuous Assessment Scores)");
        System.out.println("  12. Generate Candidate Marksheet & Evaluation Report");
        System.out.println("  13. Issue Placement Offer / Appointment Letter");
        System.out.println("  14. Industry-wise Hiring & Placement Performance Summary");
        System.out.println("  0.  Exit");
        System.out.println("=================================================================");
    }

    private static void registerJobSeeker() throws SQLException {
        System.out.println("\n--- Register Candidate ---");
        String id = readText("Candidate ID (e.g., JS106): ").toUpperCase(Locale.ROOT);
        if (seekerDAO.seekerExists(id)) {
            System.out.println("Error: Candidate ID already exists.");
            return;
        }
        String name = readText("Full Name: ");
        String email = readText("Email Address: ");
        System.out.print("Phone Number (optional): ");
        String phone = SC.nextLine().trim();
        String qualification = readText("Highest Qualification (e.g. B.Tech IT, B.E CSE, MCA): ");
        String skills = readText("Key Technical Skills (comma separated): ");
        double exp = readDouble("Years of Experience (0 for fresher): ", 0.0, 50.0);
        double expCtc = readDouble("Expected Salary / CTC (in LPA, e.g. 6.5): ", 0.0, 200.0);

        if (seekerDAO.registerJobSeeker(id, name, email, phone, qualification, skills, exp, expCtc)) {
            System.out.println("Candidate registered successfully!");
        } else {
            System.out.println("Failed to register candidate.");
        }
    }

    private static void updateJobSeeker() throws SQLException {
        System.out.println("\n--- Update Candidate Profile ---");
        String id = readText("Enter Candidate ID to update: ").toUpperCase(Locale.ROOT);
        if (!seekerDAO.seekerExists(id)) {
            System.out.println("No candidate found with ID: " + id);
            return;
        }
        String name = readText("Updated Name: ");
        String email = readText("Updated Email: ");
        System.out.print("Updated Phone (optional): ");
        String phone = SC.nextLine().trim();
        String qualification = readText("Updated Qualification: ");
        String skills = readText("Updated Technical Skills: ");
        double exp = readDouble("Updated Experience (years): ", 0.0, 50.0);
        double expCtc = readDouble("Updated Expected Salary (LPA): ", 0.0, 200.0);

        if (seekerDAO.updateJobSeeker(id, name, email, phone, qualification, skills, exp, expCtc)) {
            System.out.println("Candidate profile updated successfully!");
        } else {
            System.out.println("Update failed.");
        }
    }

    private static void deleteJobSeeker() throws SQLException {
        System.out.println("\n--- Delete Candidate Profile ---");
        String id = readText("Enter Candidate ID to delete: ").toUpperCase(Locale.ROOT);
        if (!seekerDAO.seekerExists(id)) {
            System.out.println("No candidate found with ID: " + id);
            return;
        }
        if (readYesNo("Are you sure you want to permanently delete candidate " + id + "?")) {
            if (seekerDAO.deleteJobSeeker(id)) {
                System.out.println("Candidate deleted successfully.");
            } else {
                System.out.println("Could not delete candidate.");
            }
        } else {
            System.out.println("Deletion canceled.");
        }
    }

    private static void registerEmployer() throws SQLException {
        System.out.println("\n--- Register Employer / Company ---");
        String id = readText("Employer ID (e.g., EMP106): ").toUpperCase(Locale.ROOT);
        if (employerDAO.employerExists(id)) {
            System.out.println("Error: Employer ID already exists.");
            return;
        }
        String name = readText("Company Name: ");
        String industry = readText("Industry Sector (e.g. IT, FinTech, E-Commerce): ");
        String location = readText("Headquarters / Base Location: ");
        String email = readText("Official HR Email: ");
        System.out.print("Contact Phone (optional): ");
        String phone = SC.nextLine().trim();
        System.out.print("Company Website (optional): ");
        String website = SC.nextLine().trim();

        if (employerDAO.registerEmployer(id, name, industry, location, email, phone, website)) {
            System.out.println("Company registered successfully!");
        } else {
            System.out.println("Failed to register employer.");
        }
    }

    private static void postJobVacancy() throws SQLException {
        System.out.println("\n--- Post New Job Opening ---");
        String jobId = readText("Job ID (e.g., JOB207): ").toUpperCase(Locale.ROOT);
        if (jobDAO.jobExists(jobId)) {
            System.out.println("Error: Job ID already exists.");
            return;
        }
        String empId = readText("Employer ID (e.g. EMP101): ").toUpperCase(Locale.ROOT);
        if (!employerDAO.employerExists(empId)) {
            System.out.println("Error: Employer ID " + empId + " not found. Register employer first.");
            return;
        }
        String title = readText("Job Title / Designation: ");
        String dept = readText("Department / Domain: ");

        String type;
        while (true) {
            type = readText("Job Type (FULL_TIME / PART_TIME / INTERNSHIP / REMOTE / CONTRACT): ").toUpperCase(Locale.ROOT);
            if (type.equals("FULL_TIME") || type.equals("PART_TIME") || type.equals("INTERNSHIP")
                    || type.equals("REMOTE") || type.equals("CONTRACT")) {
                break;
            }
            System.out.println("Invalid job type. Choose one of: FULL_TIME, PART_TIME, INTERNSHIP, REMOTE, CONTRACT.");
        }

        double minExp = readDouble("Minimum Experience Required (years): ", 0.0, 30.0);
        double salary = readDouble("Offered CTC (in LPA): ", 0.5, 150.0);
        String skills = readText("Required Skills: ");
        String location = readText("Job Location: ");
        int vacancies = readInt("Number of Vacancies: ", 1, 500);

        if (jobDAO.postJob(jobId, empId, title, dept, type, minExp, salary, skills, location, vacancies)) {
            System.out.println("Job opening posted successfully!");
        } else {
            System.out.println("Failed to post job.");
        }
    }

    private static void searchAndFilterJobs() throws SQLException {
        System.out.println("\n--- Browse & Search Jobs ---");
        System.out.println("1. View all available jobs");
        System.out.println("2. Filter jobs by keyword & location");
        int sub = readInt("Choice: ", 1, 2);
        if (sub == 1) {
            jobDAO.displayAllJobs();
        } else {
            System.out.print("Enter skill or title keyword (press Enter for any): ");
            String kw = SC.nextLine().trim();
            System.out.print("Enter location (press Enter for any): ");
            String loc = SC.nextLine().trim();
            jobDAO.filterJobs(kw, loc);
        }
    }

    private static void applyForJob() throws SQLException {
        System.out.println("\n--- Candidate Job Application ---");
        String seekerId = readText("Candidate ID: ").toUpperCase(Locale.ROOT);
        if (!seekerDAO.seekerExists(seekerId)) {
            System.out.println("Candidate ID not registered. Please register first.");
            return;
        }
        String jobId = readText("Job ID to apply: ").toUpperCase(Locale.ROOT);
        if (!jobDAO.jobExists(jobId)) {
            System.out.println("Job ID not found.");
            return;
        }
        if (appDAO.findApplicationId(seekerId, jobId) != -1) {
            System.out.println("Notice: Candidate has already applied for this job.");
            return;
        }
        System.out.print("Cover Note / Profile Statement (optional): ");
        String note = SC.nextLine().trim();

        if (appDAO.applyForJob(seekerId, jobId, note)) {
            System.out.println("Application submitted successfully! Candidate profile queued for review.");
        } else {
            System.out.println("Application failed.");
        }
    }

    private static void manageApplications() throws SQLException {
        System.out.println("\n--- Application Management ---");
        appDAO.displayAllApplications();

        if (readYesNo("Would you like to update an application status?")) {
            int appId = readInt("Enter Application ID: ", 1, Integer.MAX_VALUE);
            System.out.println("Available statuses: APPLIED, SHORTLISTED, INTERVIEW_SCHEDULED, SELECTED, REJECTED");
            String status = readText("New Status: ").toUpperCase(Locale.ROOT);
            if (appDAO.updateApplicationStatus(appId, status)) {
                System.out.println("Application status updated to: " + status);
            } else {
                System.out.println("Application ID not found.");
            }
        }
    }

    private static void recordInterviewEvaluation() throws SQLException {
        System.out.println("\n--- Record Interview Evaluation Scores ---");
        interviewDAO.displayInterviewReport();

        int appId = readInt("Enter Application ID to evaluate: ", 1, Integer.MAX_VALUE);
        double tech = readDouble("Technical Evaluation Score (0-100): ", 0.0, 100.0);
        double code = readDouble("Coding / Problem Solving Score (0-100): ", 0.0, 100.0);
        double comm = readDouble("Communication & Soft Skills Score (0-100): ", 0.0, 100.0);
        double hr = readDouble("HR & Behavioral Score (0-100): ", 0.0, 100.0);
        String remarks = readText("Interviewer Remarks & Observations: ");

        String result;
        while (true) {
            result = readText("Result (QUALIFIED / PENDING / NOT_QUALIFIED): ").toUpperCase(Locale.ROOT);
            if (result.equals("QUALIFIED") || result.equals("PENDING") || result.equals("NOT_QUALIFIED")) {
                break;
            }
            System.out.println("Invalid result. Enter QUALIFIED, PENDING, or NOT_QUALIFIED.");
        }

        if (interviewDAO.recordEvaluation(appId, tech, code, comm, hr, remarks, result)) {
            System.out.println("Interview evaluation recorded successfully!");
        } else {
            System.out.println("Failed to record evaluation.");
        }
    }

    private static void candidateScoreCard() throws SQLException {
        System.out.println("\n--- Candidate Marksheet & Evaluation Report ---");
        String seekerId = readText("Enter Candidate ID: ").toUpperCase(Locale.ROOT);
        analyticsDAO.displayCandidateScoreCard(seekerId);
    }

    private static void issuePlacementOffer() throws SQLException {
        System.out.println("\n--- Issue Placement Offer ---");
        int appId = readInt("Enter Application ID: ", 1, Integer.MAX_VALUE);
        double ctc = readDouble("Offered Annual CTC (LPA): ", 1.0, 150.0);
        String date = readText("Joining Date (YYYY-MM-DD): ");

        if (analyticsDAO.issuePlacementOffer(appId, ctc, date)) {
            System.out.println("Placement offer issued and candidate marked as SELECTED!");
        } else {
            System.out.println("Failed to issue offer.");
        }
    }

    // =====================================================================
    // INPUT VALIDATION & CLI UTILITIES
    // =====================================================================

    private static int readInt(String prompt, int min, int max) {
        while (true) {
            System.out.print(prompt);
            String input = SC.nextLine().trim();
            try {
                int value = Integer.parseInt(input);
                if (value >= min && value <= max) return value;
            } catch (NumberFormatException ignored) {}
            System.out.printf("Please enter a valid whole number between %d and %d.%n", min, max);
        }
    }

    private static double readDouble(String prompt, double min, double max) {
        while (true) {
            System.out.print(prompt);
            String input = SC.nextLine().trim();
            try {
                double value = Double.parseDouble(input);
                if (Double.isFinite(value) && value >= min && value <= max) return value;
            } catch (NumberFormatException ignored) {}
            System.out.printf("Please enter a valid decimal number between %.2f and %.2f.%n", min, max);
        }
    }

    private static String readText(String prompt) {
        while (true) {
            System.out.print(prompt);
            String text = SC.nextLine().trim();
            if (!text.isEmpty()) return text;
            System.out.println("Input cannot be empty. Please enter a valid value.");
        }
    }

    private static boolean readYesNo(String prompt) {
        while (true) {
            System.out.print(prompt + " (y/n): ");
            String input = SC.nextLine().trim().toLowerCase(Locale.ROOT);
            if (input.equals("y") || input.equals("yes")) return true;
            if (input.equals("n") || input.equals("no")) return false;
            System.out.println("Please enter 'y' for yes or 'n' for no.");
        }
    }
}
