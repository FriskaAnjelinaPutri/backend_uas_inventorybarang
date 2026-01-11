<?php
// =======================
// KONEKSI SQLITE
// =======================
$dbPath = __DIR__ . '/../database/users.db';
if (!is_dir(dirname($dbPath))) {
    mkdir(dirname($dbPath), 0777, true);
}
$db = new PDO("sqlite:$dbPath");
$db->setAttribute(PDO::ATTR_ERRMODE, PDO::ERRMODE_EXCEPTION);

// =======================
// CREATE TABLE
// =======================
$db->exec("
CREATE TABLE IF NOT EXISTS users (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    nama TEXT NOT NULL,
    email TEXT UNIQUE NOT NULL,
    role TEXT DEFAULT 'staff' CHECK (role IN ('admin', 'staff', 'kurir')),
    password TEXT NOT NULL DEFAULT '',
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP
)
");

// =======================
// MIGRATION: Add columns if table exists without them
// =======================
try {
    $result = $db->query("PRAGMA table_info(users)");
    $tableInfo = $result->fetchAll(PDO::FETCH_ASSOC);
    $columns = array_column($tableInfo, 'name');
    
    if (!in_array('role', $columns)) {
        $db->exec("ALTER TABLE users ADD COLUMN role TEXT DEFAULT 'staff'");
        $db->exec("UPDATE users SET role = 'staff' WHERE role IS NULL");
    }
    if (!in_array('password', $columns)) {
        $db->exec("ALTER TABLE users ADD COLUMN password TEXT DEFAULT ''");
        $db->exec("UPDATE users SET password = '' WHERE password IS NULL");
    }
} catch (PDOException $e) {
    // ignore
}

// =======================
// HELPER FUNCTIONS
// =======================
function sendResponse($data, $statusCode = 200)
{
    http_response_code($statusCode);
    header("Content-Type: application/json");
    echo json_encode($data);
    exit;
}

function getUserById($db, $id)
{
    $stmt = $db->prepare("SELECT id, nama, email, role, created_at FROM users WHERE id = ?");
    $stmt->execute([$id]);
    return $stmt->fetch(PDO::FETCH_ASSOC);
}

function hashPassword($password)
{
    return password_hash($password, PASSWORD_DEFAULT);
}

// =======================
// ROUTING
// =======================
$method = $_SERVER['REQUEST_METHOD'];
$uri = parse_url($_SERVER['REQUEST_URI'], PHP_URL_PATH);

try {
    // =======================
    // ROOT ENDPOINT
    // =======================
    if ($method === 'GET' && $uri === '/') {
        sendResponse(["status" => "API running"]);
    }

    // =======================
    // REGISTER USER (POST /users)
    // =======================
    elseif ($method === 'POST' && $uri === '/users') {
        $data = json_decode(file_get_contents("php://input"), true);

        if (!isset($data['nama'], $data['email'], $data['role'], $data['password'])) {
            sendResponse(["error" => "Nama, email, role, dan password wajib diisi"], 400);
        }

        $allowedRoles = ['admin', 'staff', 'kurir'];
        if (!in_array($data['role'], $allowedRoles)) {
            sendResponse(["error" => "Role harus salah satu dari: admin, staff, kurir"], 400);
        }

        $hashedPassword = hashPassword($data['password']);
        $stmt = $db->prepare("INSERT INTO users (nama, email, role, password) VALUES (?, ?, ?, ?)");
        $stmt->execute([$data['nama'], $data['email'], $data['role'], $hashedPassword]);

        $userId = $db->lastInsertId();
        $newUser = getUserById($db, $userId);

        sendResponse($newUser, 201);
    }

    // =======================
    // LOGIN USER (POST /login)
    // =======================
    elseif ($method === 'POST' && $uri === '/login') {
        $data = json_decode(file_get_contents("php://input"), true);

        if (!isset($data['email'], $data['password'])) {
            sendResponse(["error" => "Email dan password harus diisi"], 400);
        }

        $stmt = $db->prepare("SELECT * FROM users WHERE email = ?");
        $stmt->execute([$data['email']]);
        $user = $stmt->fetch(PDO::FETCH_ASSOC);

        if (!$user) {
            sendResponse(["error" => "Email tidak ditemukan"], 404);
        }

        if (!password_verify($data['password'], $user['password'])) {
            sendResponse(["error" => "Password salah"], 401);
        }

        unset($user['password']);
        sendResponse($user);
    }

    // =======================
    // READ ALL USERS (GET /users)
    // =======================
    elseif ($method === 'GET' && $uri === '/users') {
        $stmt = $db->query("SELECT id, nama, email, role, created_at FROM users ORDER BY id ASC");
        $users = $stmt->fetchAll(PDO::FETCH_ASSOC);
        sendResponse($users);
    }

    // =======================
    // READ USER BY ID (GET /users/{id})
    // =======================
    elseif ($method === 'GET' && preg_match('/^\/users\/(\d+)$/', $uri, $matches)) {
        $id = $matches[1];
        $user = getUserById($db, $id);
        if (!$user) {
            sendResponse(["error" => "User dengan id $id tidak ditemukan"], 404);
        }
        sendResponse($user);
    }

    // =======================
    // UPDATE USER (PUT /users/{id})
    // =======================
    elseif ($method === 'PUT' && preg_match('/^\/users\/(\d+)$/', $uri, $matches)) {
        $id = $matches[1];
        $user = getUserById($db, $id);
        if (!$user) {
            sendResponse(["error" => "User dengan id $id tidak ditemukan"], 404);
        }

        $data = json_decode(file_get_contents("php://input"), true);
        if (!isset($data['nama'], $data['email'], $data['role'])) {
            sendResponse(["error" => "Nama, email, dan role wajib diisi"], 400);
        }

        $allowedRoles = ['admin', 'staff', 'kurir'];
        if (!in_array($data['role'], $allowedRoles)) {
            sendResponse(["error" => "Role harus salah satu dari: admin, staff, kurir"], 400);
        }

        if (!empty($data['password'])) {
            $hashedPassword = hashPassword($data['password']);
            $stmt = $db->prepare("UPDATE users SET nama=?, email=?, role=?, password=? WHERE id=?");
            $stmt->execute([$data['nama'], $data['email'], $data['role'], $hashedPassword, $id]);
        } else {
            $stmt = $db->prepare("UPDATE users SET nama=?, email=?, role=? WHERE id=?");
            $stmt->execute([$data['nama'], $data['email'], $data['role'], $id]);
        }

        $updatedUser = getUserById($db, $id);
        sendResponse($updatedUser);
    }

    // =======================
    // DELETE USER (DELETE /users/{id})
    // =======================
    elseif ($method === 'DELETE' && preg_match('/^\/users\/(\d+)$/', $uri, $matches)) {
        $id = $matches[1];
        $user = getUserById($db, $id);
        if (!$user) {
            sendResponse(["error" => "User dengan id $id tidak ditemukan"], 404);
        }
        $stmt = $db->prepare("DELETE FROM users WHERE id=?");
        $stmt->execute([$id]);
        sendResponse(["message" => "User dengan id $id berhasil dihapus"]);
    }

    // =======================
    // DEFAULT 404
    // =======================
    else {
        sendResponse(["error" => "Route tidak ditemukan"], 404);
    }
} catch (PDOException $e) {
    if (strpos($e->getMessage(), 'UNIQUE constraint failed') !== false) {
        sendResponse(["error" => "Email sudah terdaftar"], 409);
    }
    sendResponse(["error" => "Database error: " . $e->getMessage()], 500);
} catch (Exception $e) {
    sendResponse(["error" => "Server error: " . $e->getMessage()], 500);
}
