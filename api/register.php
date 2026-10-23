<?php
session_start();
header('Content-Type: application/json; charset=utf-8');
require_once '../config.php';
if ($_SERVER['REQUEST_METHOD'] !== 'POST') { http_response_code(405); echo json_encode(['error'=>'Method not allowed']); exit; }
$data = json_decode(file_get_contents('php://input'), true) ?? [];
$name = trim($data['name'] ?? '');
$email = strtolower(trim($data['email'] ?? ''));
$password = $data['password'] ?? '';
$role = $data['role'] ?? 'seeker';
if (mb_strlen($name) < 2 || !filter_var($email, FILTER_VALIDATE_EMAIL) || strlen($password) < 6 || !in_array($role, ['seeker','employer'], true)) {
  http_response_code(422); echo json_encode(['error'=>'Enter a valid name, email, password (6+ characters) and account type.']); exit;
}
try {
  $stmt = $pdo->prepare('INSERT INTO users (name,email,password_hash,role) VALUES (?,?,?,?)');
  $stmt->execute([$name,$email,password_hash($password, PASSWORD_DEFAULT),$role]);
  $_SESSION['user'] = ['id'=>(int)$pdo->lastInsertId(),'name'=>$name,'email'=>$email,'role'=>$role];
  echo json_encode(['user'=>$_SESSION['user']]);
} catch (PDOException $e) {
  if ($e->getCode() === '23000') { http_response_code(409); echo json_encode(['error'=>'This email is already registered.']); }
  else { http_response_code(500); echo json_encode(['error'=>'Could not create account.']); }
}
?>