# Azure MySQL Deployment Guide

## 🎯 Übersicht
Schritt-für-Schritt Anleitung für die Migration von H2 zu Azure MySQL.

## 📋 Voraussetzungen
- Azure MySQL Flexible Server bereits erstellt ✅
- Connection Details verfügbar ✅
- Spring Boot Anwendung lokal funktionsfähig ✅

## 🔥 Identifizierte Probleme & Lösungen

### 1. Firewall-Problem (KRITISCH)
**Problem:** `TcpTestSucceeded: False`
```powershell
Test-NetConnection -ComputerName "dbotk25.mysql.database.azure.com" -Port 3306
# Ergebnis: Connection failed
```

**Lösung:**
1. Azure Portal → MySQL Server → Networking
2. Firewall Rules → Add current client IP
3. Oder: Allow access to Azure services ✅

### 2. SSL/TLS Konfiguration
**Aktuelle Config:**
```properties
useSSL=true&requireSSL=true&serverTimezone=UTC&allowPublicKeyRetrieval=true
```

**Mögliche Probleme:**
- Azure erfordert `sslMode=REQUIRED`
- Certificate validation issues

**Verbesserte Connection String:**
```properties
spring.datasource.url=jdbc:mysql://dbotk25.mysql.database.azure.com:3306/terminkalender?useSSL=true&requireSSL=true&sslMode=REQUIRED&serverTimezone=UTC&allowPublicKeyRetrieval=true&useUnicode=true&characterEncoding=UTF-8
```

### 3. Schema Migration H2 → MySQL
**Automatisch durch Hibernate:**
```properties
spring.jpa.hibernate.ddl-auto=update
```

**Manuell (sicherer):**
```sql
-- Alle Tabellen werden automatisch erstellt
-- User-Daten müssen manuell migriert werden
```

## 🚀 Deployment-Schritte

### Schritt 1: Firewall konfigurieren
```bash
# Azure CLI
az mysql flexible-server firewall-rule create \
  --resource-group myResourceGroup \
  --server-name dbotk25 \
  --rule-name AllowMyIP \
  --start-ip-address YOUR_IP \
  --end-ip-address YOUR_IP
```

### Schritt 2: Connection String testen
```java
// Test-Klasse bereits vorhanden:
// src/test/java/.../test/AzureMySQLConnectionTest.java
mvn test-compile exec:java -Dexec.mainClass=de.swtp1.terminkalender.test.AzureMySQLConnectionTest -Dexec.classpathScope=test
```

### Schritt 3: Produktions-Profile aktivieren
```powershell
# Environment Variables setzen
$env:DATABASE_URL = "jdbc:mysql://dbotk25.mysql.database.azure.com:3306/terminkalender?useSSL=true&requireSSL=true&sslMode=REQUIRED&serverTimezone=UTC&allowPublicKeyRetrieval=true"
$env:DATABASE_USERNAME = "fbmb16@dbotk25"
$env:DATABASE_PASSWORD = "PalKauf91"

# App mit Produktions-Profil starten
mvn spring-boot:run -Dspring-boot.run.profiles=prod
```

### Schritt 4: Daten-Migration
```sql
-- H2 Daten exportieren (optional)
-- Automatische Schema-Erstellung durch Hibernate
-- Test-User werden durch DataInitializer erstellt
```

## ⚠️ Häufige Probleme

### Problem: "Communications link failure"
**Ursache:** Firewall blockiert Verbindung
**Lösung:** IP-Adresse zur Firewall-Whitelist hinzufügen

### Problem: "Access denied for user"
**Ursache:** Falsche Credentials oder User-Format
**Lösung:** Username format: `username@servername`

### Problem: "SSL connection required"
**Ursache:** Azure erfordert SSL
**Lösung:** `sslMode=REQUIRED` in Connection String

### Problem: "Unknown database"
**Ursache:** Datenbank existiert nicht
**Lösung:** Datenbank in Azure Portal erstellen

## 🔒 Security Considerations

### Environment Variables (Produktion)
```bash
# Niemals in Code committen!
export DATABASE_URL="jdbc:mysql://..."
export DATABASE_USERNAME="username@server"
export DATABASE_PASSWORD="secure_password"
export MAIL_HOST="smtp.gmail.com"
export MAIL_USERNAME="your-email@gmail.com"
export MAIL_PASSWORD="app-password"
```

### CORS Configuration (Produktion)
```properties
cors.allowed-origins=https://yourdomain.com,https://your-frontend.com
```

## 📊 Monitoring & Health Checks

### Actuator Endpoints
```
GET /actuator/health
GET /actuator/metrics
```

### Database Health Check
```java
// Automatisch durch Spring Boot verfügbar
// Zeigt MySQL Connection Status
```

## 🎯 Next Steps

1. **Firewall konfigurieren** (KRITISCH)
2. **Connection String optimieren**
3. **Produktions-Deployment testen**
4. **Mail-Service konfigurieren**
5. **Frontend für Produktion anpassen**

## 📝 Checkliste

- [ ] Azure MySQL Firewall Rules konfiguriert
- [ ] Connection String getestet
- [ ] Environment Variables gesetzt
- [ ] Produktions-Profil funktioniert
- [ ] Schema automatisch erstellt
- [ ] Test-Daten verfügbar
- [ ] Password Reset funktioniert
- [ ] Mail-Service konfiguriert (optional)
- [ ] Health Checks funktionieren
- [ ] CORS für Frontend konfiguriert

## 🔗 Nützliche Commands

```powershell
# Connection Test
Test-NetConnection -ComputerName "dbotk25.mysql.database.azure.com" -Port 3306

# App mit Azure MySQL starten
mvn spring-boot:run -Dspring-boot.run.profiles=prod

# Build für Deployment
mvn clean package -DskipTests

# Docker Build (optional)
docker build -t terminkalender .
```