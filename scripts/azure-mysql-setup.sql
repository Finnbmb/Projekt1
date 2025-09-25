# Bearbeite diese Datei:
notepad setup-azure-mysql.ps1

# Ändere diese Zeilen mit deinen Azure-Werten:
$AZURE_SERVER_NAME = "dbotk25"        # Ohne .mysql.database.azure.com
$DATABASE_NAME = "dbotk25"               # Oder dein DB-Name
$ADMIN_USERNAME = "fbmb16"                       # Dein Azure Admin User
$ADMIN_PASSWORD = "PalKauf91"   # Dein Azure Passwort-- Azure MySQL Datenbank Setup für Terminkalender
-- Führe dieses Script in der Azure MySQL Datenbank aus

-- ============================================
-- DATABASE SETUP
-- ============================================

-- Erstelle Datenbank falls nicht existiert (normalerweise schon durch Azure erstellt)
CREATE DATABASE IF NOT EXISTS terminkalender 
  CHARACTER SET utf8mb4 
  COLLATE utf8mb4_unicode_ci;

-- Verwende die Datenbank
USE terminkalender;

-- ============================================
-- TABELLEN WERDEN AUTOMATISCH ERSTELLT
-- ============================================
-- Spring Boot mit Hibernate erstellt automatisch alle Tabellen
-- beim ersten Start mit der Einstellung: spring.jpa.hibernate.ddl-auto=update

-- ============================================
-- OPTIONAL: TESTDATEN EINFÜGEN
-- ============================================

-- Admin-Benutzer erstellen (Passwort: admin123)
INSERT IGNORE INTO users (username, email, name, password_hash, role, is_active, created_at, updated_at) 
VALUES ('admin', 'admin@example.com', 'Administrator', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM5lIX.P9ng1utDBTXvm', 'ADMIN', true, NOW(), NOW());

-- Test-Benutzer erstellen (Passwort: user123)
INSERT IGNORE INTO users (username, email, name, password_hash, role, is_active, created_at, updated_at) 
VALUES ('testuser', 'test@example.com', 'Test User', '$2a$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2uheWG/igi.', 'USER', true, NOW(), NOW());

-- Beispiel-Termin erstellen
INSERT IGNORE INTO appointments (title, description, start_date_time, end_date_time, location, priority, user_id, created_at, updated_at)
VALUES ('Azure Test Meeting', 'Test-Termin nach Azure Migration', '2025-09-24 14:00:00', '2025-09-24 15:00:00', 'Online', 'HOCH', 1, NOW(), NOW());

-- ============================================
-- BERECHTIGUNGEN PRÜFEN
-- ============================================

-- Zeige aktuelle Benutzer-Berechtigungen
SELECT User, Host FROM mysql.user WHERE User LIKE '%admin%';

-- Zeige Datenbank-Informationen
SELECT SCHEMA_NAME FROM INFORMATION_SCHEMA.SCHEMATA WHERE SCHEMA_NAME = 'terminkalender';