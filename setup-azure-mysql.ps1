# Azure MySQL Setup Script
# Setze diese Umgebungsvariablen für die Azure Database for MySQL

# ============================================
# AZURE MYSQL KONFIGURATION
# ============================================

# Deine Azure-Details:

# Server Name aus Azure Portal (ohne .mysql.database.azure.com)
$AZURE_SERVER_NAME = "dbotk25"

# Database Name (z.B. terminkalender)
$DATABASE_NAME = "terminkalender"

# Admin Username aus Azure Portal
$ADMIN_USERNAME = "fbmb16"

# Password das du bei der Erstellung gesetzt hast
$ADMIN_PASSWORD = "PaulKauf91"

# ============================================
# AUTOMATISCHE KONFIGURATION
# ============================================

# Vollständige JDBC URL für Azure MySQL
$DATABASE_URL = "jdbc:mysql://$AZURE_SERVER_NAME.mysql.database.azure.com:3306/$DATABASE_NAME?useSSL=true&requireSSL=true&serverTimezone=UTC&allowPublicKeyRetrieval=true"

# Username im Azure-Format
$DATABASE_USERNAME = "$ADMIN_USERNAME@$AZURE_SERVER_NAME"

# ============================================
# UMGEBUNGSVARIABLEN SETZEN
# ============================================

Write-Host "🔧 Setze Azure MySQL Umgebungsvariablen..."

# Für aktuelle PowerShell Session
$env:DATABASE_URL = $DATABASE_URL
$env:DATABASE_USERNAME = $DATABASE_USERNAME  
$env:DATABASE_PASSWORD = $ADMIN_PASSWORD

# Für System (dauerhaft) - OPTIONAL
# [Environment]::SetEnvironmentVariable("DATABASE_URL", $DATABASE_URL, "User")
# [Environment]::SetEnvironmentVariable("DATABASE_USERNAME", $DATABASE_USERNAME, "User")
# [Environment]::SetEnvironmentVariable("DATABASE_PASSWORD", $ADMIN_PASSWORD, "User")

Write-Host "✅ Umgebungsvariablen gesetzt:"
Write-Host "   DATABASE_URL: $DATABASE_URL"
Write-Host "   DATABASE_USERNAME: $DATABASE_USERNAME"
Write-Host "   DATABASE_PASSWORD: ******"

Write-Host ""
Write-Host "🚀 Starte jetzt die Anwendung mit:"
Write-Host "   mvn spring-boot:run -Dspring.profiles.active=prod"
Write-Host ""
Write-Host "🔍 Oder teste die Verbindung mit:"
Write-Host "   mvn spring-boot:run -Dspring.profiles.active=prod -Dlogging.level.org.springframework.jdbc=DEBUG"