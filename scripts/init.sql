-- Initialisierung der MySQL Datenbank für Terminkalender
-- Wird automatisch bei erstem Start des MySQL Containers ausgeführt

-- Erstelle Datenbank falls nicht existiert
CREATE DATABASE IF NOT EXISTS terminkalender 
  CHARACTER SET utf8mb4 
  COLLATE utf8mb4_unicode_ci;

-- Verwende die Datenbank
USE terminkalender;

-- Erstelle einen dedizierten Benutzer für die Anwendung
CREATE USER IF NOT EXISTS 'terminkalender_user'@'%' IDENTIFIED BY 'terminkalender_password';

-- Gewähre alle Rechte auf die Terminkalender-Datenbank
GRANT ALL PRIVILEGES ON terminkalender.* TO 'terminkalender_user'@'%';

-- Privilegien anwenden
FLUSH PRIVILEGES;

-- Log der Initialisierung
SELECT 'Terminkalender Datenbank erfolgreich initialisiert' AS status;