<?php
session_start();
header('Content-Type: application/json; charset=utf-8');
require_once '../config.php';
if (($_SESSION['user']['role'] ?? '') !== 'employer') { http_response_code(403); echo json_encode(['error'=>'Login with an employer account to post jobs.']); exit; }
if ($_SERVER['REQUEST_METHOD'] !== 'POST') { http_response_code(405); echo json_encode(['error'=>'Method not allowed']); exit; }
$d = json_decode(file_get_contents('php://input'), true) ?? [];
$fields = ['title','company','location','salary','type','experience','description'];
foreach ($fields as $field) { if (trim($d[$field] ?? '') === '') { http_response_code(422); echo json_encode(['error'=>'Please complete every job field.']); exit; } }
$stmt = $pdo->prepare('INSERT INTO jobs (title,company,location,salary,job_type,experience,description,posted_by) VALUES (?,?,?,?,?,?,?,?)');
$stmt->execute([trim($d['title']),trim($d['company']),trim($d['location']),trim($d['salary']),trim($d['type']),trim($d['experience']),trim($d['description']),$_SESSION['user']['id']]);
echo json_encode(['success'=>true,'id'=>(int)$pdo->lastInsertId()]);
?>