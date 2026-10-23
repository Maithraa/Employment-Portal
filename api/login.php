<?php
session_start();
header('Content-Type: application/json; charset=utf-8');
require_once '../config.php';
if ($_SERVER['REQUEST_METHOD'] !== 'POST') { http_response_code(405); echo json_encode(['error'=>'Method not allowed']); exit; }
$data = json_decode(file_get_contents('php://input'), true) ?? [];
$email = strtolower(trim($data['email'] ?? ''));
$password = $data['password'] ?? '';
$stmt = $pdo->prepare('SELECT id,name,email,password_hash,role FROM users WHERE email = ?');
$stmt->execute([$email]);
$user = $stmt->fetch();
if (!$user || !password_verify($password, $user['password_hash'])) {
  http_response_code(401); echo json_encode(['error'=>'Email or password is incorrect.']); exit;
}
session_regenerate_id(true);
unset($user['password_hash']);
$user['id'] = (int)$user['id'];
$_SESSION['user'] = $user;
echo json_encode(['user'=>$user]);
?>