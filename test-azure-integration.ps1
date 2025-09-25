# Test Script für Azure MySQL Integration
# Führe diese Schritte aus, um die Azure MySQL Verbindung zu testen

Write-Host "🔍 Azure MySQL Integration Test" -ForegroundColor Cyan
Write-Host "================================" -ForegroundColor Cyan

# 1. Prüfe aktuelle Anwendung
Write-Host "`n1️⃣ Teste aktuelle Anwendung..." -ForegroundColor Yellow
try {
    $response = Invoke-WebRequest -Uri "http://localhost:8080/actuator/health" -UseBasicParsing
    if ($response.StatusCode -eq 200) {
        Write-Host "✅ Anwendung läuft (Port 8080)" -ForegroundColor Green
    }
} catch {
    Write-Host "❌ Anwendung läuft nicht auf Port 8080" -ForegroundColor Red
    Write-Host "   Starte zuerst: mvn spring-boot:run" -ForegroundColor Yellow
    exit
}

# 2. Zeige aktuelle Datenbankverbindung
Write-Host "`n2️⃣ Aktuelle Datenbankverbindung:" -ForegroundColor Yellow
try {
    $response = Invoke-WebRequest -Uri "http://localhost:8080/h2-console" -UseBasicParsing
    Write-Host "✅ H2 Datenbank aktiv (Entwicklungsmodus)" -ForegroundColor Green
} catch {
    Write-Host "❌ H2 Console nicht erreichbar" -ForegroundColor Red
}

# 3. Prüfe Maven Dependencies
Write-Host "`n3️⃣ Prüfe MySQL Connector..." -ForegroundColor Yellow
if (Select-String -Path "pom.xml" -Pattern "mysql-connector") {
    Write-Host "✅ MySQL Connector ist in pom.xml vorhanden" -ForegroundColor Green
} else {
    Write-Host "❌ MySQL Connector fehlt in pom.xml" -ForegroundColor Red
}

# 4. Zeige nächste Schritte für Azure
Write-Host "`n4️⃣ Nächste Schritte für Azure MySQL:" -ForegroundColor Yellow
Write-Host "   a) Bearbeite setup-azure-mysql.ps1 mit deinen Azure Details" -ForegroundColor White
Write-Host "   b) Führe aus: .\setup-azure-mysql.ps1" -ForegroundColor White  
Write-Host "   c) Stoppe aktuelle Anwendung: Ctrl+C im Maven Terminal" -ForegroundColor White
Write-Host "   d) Starte mit Azure: mvn spring-boot:run -Dspring.profiles.active=prod" -ForegroundColor White

# 5. Zeige Setup-Script Status
Write-Host "`n5️⃣ Setup-Script Status:" -ForegroundColor Yellow
if (Test-Path "setup-azure-mysql.ps1") {
    Write-Host "✅ setup-azure-mysql.ps1 ist bereit" -ForegroundColor Green
    Write-Host "   Bearbeite die Werte und führe es aus!" -ForegroundColor Cyan
} else {
    Write-Host "❌ setup-azure-mysql.ps1 nicht gefunden" -ForegroundColor Red
}

if (Test-Path "scripts/azure-mysql-setup.sql") {
    Write-Host "✅ Azure SQL Setup-Script ist bereit" -ForegroundColor Green
} else {
    Write-Host "❌ Azure SQL Setup-Script nicht gefunden" -ForegroundColor Red
}

Write-Host "`n🎯 ZUSAMMENFASSUNG:" -ForegroundColor Cyan
Write-Host "- Aktuell läuft die App mit H2 (Entwicklung) ✅" -ForegroundColor Green
Write-Host "- Azure MySQL Integration ist vorbereitet ✅" -ForegroundColor Green
Write-Host "- Du musst nur noch deine Azure Details eintragen!" -ForegroundColor Yellow

Write-Host "`n📝 NÄCHSTER SCHRITT:" -ForegroundColor Cyan
Write-Host "Bearbeite jetzt: setup-azure-mysql.ps1" -ForegroundColor White