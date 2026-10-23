<?php
session_start();
header('Content-Type: application/json; charset=utf-8');
require_once '../config.php';
$user = $_SESSION['user'] ?? null;
if (!$user) { http_response_code(401); echo json_encode(['error'=>'Please login before applying.']); exit; }
if ($user['role'] !== 'seeker') { http_response_code(403); echo json_encode(['error'=>'Only job seeker accounts can apply.']); exit; }
$d = json_decode(file_get_contents('php://input'), true) ?? [];
$jobId = filter_var($d['job_id'] ?? null, FILTER_VALIDATE_INT);
if (!$jobId) { http_response_code(422); echo json_encode(['error'=>'Invalid job.']); exit; }
try {
  $stmt = $pdo->prepare('INSERT INTO applications (user_id,job_id) VALUES (?,?)');
  $stmt->execute([$user['id'],$jobId]);
  echo json_encode(['success'=>true]);
} catch (PDOException $e) {
  if ($e->getCode() === '23000') { http_response_code(409); echo json_encode(['error'=>'You have already applied for this job, or the job no longer exists.']); }
  else { http_response_code(500); echo json_encode(['error'=>'Could not save application.']); }
}
?>