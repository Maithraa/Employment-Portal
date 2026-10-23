<?php
header('Content-Type: application/json; charset=utf-8');
require_once '../config.php';
if ($_SERVER['REQUEST_METHOD'] === 'GET') {
    $keyword = trim($_GET['q'] ?? '');
    $location = trim($_GET['location'] ?? '');
    $sql = "SELECT id, title, company, location, salary, job_type AS type, experience, description, DATE_FORMAT(created_at, '%d %b %Y') AS posted FROM jobs WHERE 1=1";
    $params = [];
    if ($keyword !== '') {
        $sql .= " AND (title LIKE ? OR company LIKE ? OR description LIKE ?)";
        $like = "%$keyword%";
        array_push($params, $like, $like, $like);
    }
    if ($location !== '') {
        $sql .= " AND location LIKE ?";
        $params[] = "%$location%";
    }
    $sql .= " ORDER BY created_at DESC, id DESC";
    $stmt = $pdo->prepare($sql);
    $stmt->execute($params);
    echo json_encode($stmt->fetchAll());
    exit;
}
http_response_code(405);
echo json_encode(['error' => 'Method not allowed']);
?>