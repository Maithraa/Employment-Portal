<?php
session_start();
header('Content-Type: application/json; charset=utf-8');
require_once '../config.php';
$user = $_SESSION['user'] ?? null;
if (!$user) { http_response_code(401); echo json_encode(['error'=>'Please login to view applications.']); exit; }
$stmt = $pdo->prepare("SELECT a.id, j.id AS job_id, j.title, j.company, j.location, j.salary, j.job_type AS type, DATE_FORMAT(a.applied_at, '%d %b %Y') AS applied_at FROM applications a JOIN jobs j ON j.id=a.job_id WHERE a.user_id=? ORDER BY a.applied_at DESC");
$stmt->execute([$user['id']]);
echo json_encode($stmt->fetchAll());
?>