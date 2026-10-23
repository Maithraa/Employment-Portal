<?php
// Update these values if your MySQL username/password is different.
$host = 'localhost';
$dbname = 'employment_portal';
$username = 'root';
$password = ''; // Default XAMPP root password is usually blank on local installs.

try {
    $pdo = new PDO(
        "mysql:host=$host;dbname=$dbname;charset=utf8mb4",
        $username,
        $password,
        [
            PDO::ATTR_ERRMODE => PDO::ERRMODE_EXCEPTION,
            PDO::ATTR_DEFAULT_FETCH_MODE => PDO::FETCH_ASSOC,
            PDO::ATTR_EMULATE_PREPARES => false
        ]
    );
} catch (PDOException $e) {
    http_response_code(500);
    header('Content-Type: application/json; charset=utf-8');
    echo json_encode(['error' => 'Database connection failed. Check config.php and make sure MySQL is running.']);
    exit;
}
?>