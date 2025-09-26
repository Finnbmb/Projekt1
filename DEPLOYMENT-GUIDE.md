# 🚀 Terminkalender - Deployment-Handbuch

> **Ergänzung zur Installations-Anleitung**  
> **Fokus**: Produktive Bereitstellung & Umgebungs-spezifische Setups

---

## 📊 Deployment-Übersicht

| Umgebung | Komplexität | Empfohlen für | Setup-Zeit |
|----------|-------------|---------------|------------|
| **Lokale Entwicklung** | ⭐ | Entwickler | 5 min |
| **Docker Lokal** | ⭐⭐ | Tests, Demos | 10 min |
| **Server (VM/Bare Metal)** | ⭐⭐⭐ | Kleine Teams | 30 min |
| **Azure Cloud** | ⭐⭐⭐⭐ | Produktion | 60 min |
| **Kubernetes** | ⭐⭐⭐⭐⭐ | Enterprise | 120 min |

---

## 🏠 Lokale Entwicklung (Standard)

### **Schnellster Start**
```bash
git clone https://github.com/Finnbmb/Projekt1.git
cd Projekt1
mvn spring-boot:run
```

### **Features in dieser Umgebung:**
- ✅ H2-Datenbank (datei-basiert)
- ✅ Alle Admin-Features
- ✅ Deutsche Feiertage vorinitialisiert
- ✅ Hot-Reload bei Code-Änderungen
- ✅ Debug-Interfaces verfügbar

### **Konfiguration:**
```yaml
# Automatisch aktiv in application.yml
spring:
  profiles:
    active: prod  # Verwendet Azure MySQL
  h2:
    console:
      enabled: true  # H2-Console verfügbar
```

---

## 🐳 Docker-Deployment

### **Option A: Einfacher Docker-Container**

#### **1. JAR erstellen**
```bash
mvn clean package -DskipTests
```

#### **2. Dockerfile erstellen**
```dockerfile
FROM openjdk:17-jre-slim

# Arbeitsverzeichnis
WORKDIR /app

# JAR kopieren
COPY target/terminkalender-*.jar app.jar

# Port freigeben
EXPOSE 8080

# Health-Check
HEALTHCHECK --interval=30s --timeout=10s --start-period=60s --retries=3 \
  CMD curl -f http://localhost:8080/actuator/health || exit 1

# Umgebungsvariablen
ENV SPRING_PROFILES_ACTIVE=prod
ENV JAVA_OPTS="-Xmx2g -Xms1g"

# Startbefehl
CMD java $JAVA_OPTS -jar app.jar
```

#### **3. Image bauen und starten**
```bash
# Image bauen
docker build -t terminkalender:latest .

# Container starten
docker run -d \
  --name terminkalender \
  -p 8080:8080 \
  -e DB_USERNAME=your_db_user \
  -e DB_PASSWORD=your_db_password \
  -e JWT_SECRET=your_jwt_secret \
  terminkalender:latest
```

### **Option B: Docker Compose (mit MySQL)**

#### **docker-compose.yml**
```yaml
version: '3.8'

services:
  app:
    build: .
    ports:
      - "8080:8080"
    environment:
      - SPRING_PROFILES_ACTIVE=prod
      - DB_USERNAME=terminkalender
      - DB_PASSWORD=password123
      - SPRING_DATASOURCE_URL=jdbc:mysql://mysql:3306/terminkalender?serverTimezone=Europe/Berlin
    depends_on:
      mysql:
        condition: service_healthy
    healthcheck:
      test: ["CMD", "curl", "-f", "http://localhost:8080/actuator/health"]
      interval: 30s
      timeout: 10s
      retries: 3

  mysql:
    image: mysql:8.0
    environment:
      - MYSQL_ROOT_PASSWORD=rootpassword
      - MYSQL_DATABASE=terminkalender
      - MYSQL_USER=terminkalender
      - MYSQL_PASSWORD=password123
    ports:
      - "3306:3306"
    volumes:
      - mysql_data:/var/lib/mysql
      - ./scripts/init.sql:/docker-entrypoint-initdb.d/init.sql
    healthcheck:
      test: ["CMD", "mysqladmin", "ping", "-h", "localhost"]
      interval: 10s
      timeout: 5s
      retries: 5

volumes:
  mysql_data:
```

#### **Starten**
```bash
docker-compose up -d

# Logs verfolgen
docker-compose logs -f app

# Stoppen
docker-compose down
```

---

## 🖥️ Server-Deployment (VM/Bare Metal)

### **Umgebung vorbereiten**

#### **Ubuntu/Debian Server**
```bash
# System aktualisieren
sudo apt update && sudo apt upgrade -y

# Java 17 installieren
sudo apt install openjdk-17-jre-headless -y

# Benutzer für App erstellen
sudo useradd -r -s /bin/false terminkalender
sudo mkdir -p /opt/terminkalender
sudo mkdir -p /var/log/terminkalender
sudo chown terminkalender:terminkalender /opt/terminkalender /var/log/terminkalender
```

#### **CentOS/RHEL Server**
```bash
# System aktualisieren
sudo yum update -y

# Java 17 installieren
sudo yum install java-17-openjdk-headless -y

# Benutzer erstellen
sudo useradd -r -s /bin/false terminkalender
sudo mkdir -p /opt/terminkalender /var/log/terminkalender
sudo chown terminkalender:terminkalender /opt/terminkalender /var/log/terminkalender
```

### **Anwendung bereitstellen**

#### **1. JAR-Datei übertragen**
```bash
# Lokal bauen
mvn clean package -DskipTests

# Auf Server kopieren
scp target/terminkalender-*.jar user@server:/tmp/
sudo mv /tmp/terminkalender-*.jar /opt/terminkalender/terminkalender.jar
sudo chown terminkalender:terminkalender /opt/terminkalender/terminkalender.jar
```

#### **2. Konfigurationsdatei erstellen**
```bash
sudo cat > /opt/terminkalender/application-prod.properties << EOF
# Database Configuration
spring.datasource.url=jdbc:mysql://localhost:3306/terminkalender?serverTimezone=Europe/Berlin
spring.datasource.username=terminkalender
spring.datasource.password=SecurePassword123!
spring.datasource.driver-class-name=com.mysql.cj.jdbc.Driver

# JPA Configuration
spring.jpa.hibernate.ddl-auto=update
spring.jpa.database-platform=org.hibernate.dialect.MySQL8Dialect
spring.jpa.show-sql=false

# Logging
logging.file.name=/var/log/terminkalender/application.log
logging.level.de.swtp1.terminkalender=INFO
logging.pattern.file=%d{yyyy-MM-dd HH:mm:ss} [%thread] %-5level %logger{36} - %msg%n

# Management
management.endpoints.web.exposure.include=health,info,metrics
management.endpoint.health.show-details=when-authorized
EOF
```

#### **3. Systemd Service erstellen**
```bash
sudo cat > /etc/systemd/system/terminkalender.service << EOF
[Unit]
Description=Terminkalender Spring Boot Application
After=network.target mysql.service
Wants=mysql.service

[Service]
Type=simple
User=terminkalender
Group=terminkalender
WorkingDirectory=/opt/terminkalender

ExecStart=/usr/bin/java \\
  -Xmx2g -Xms1g \\
  -Dspring.profiles.active=prod \\
  -Dspring.config.location=classpath:/application.yml,/opt/terminkalender/application-prod.properties \\
  -jar terminkalender.jar

Restart=always
RestartSec=10
StandardOutput=journal
StandardError=journal
SyslogIdentifier=terminkalender

Environment=LANG=de_DE.UTF-8
Environment=TZ=Europe/Berlin

# Security
NoNewPrivileges=yes
PrivateTmp=yes
ProtectSystem=strict
ProtectHome=yes
ReadWritePaths=/var/log/terminkalender

[Install]
WantedBy=multi-user.target
EOF
```

#### **4. Service aktivieren und starten**
```bash
sudo systemctl daemon-reload
sudo systemctl enable terminkalender
sudo systemctl start terminkalender

# Status prüfen
sudo systemctl status terminkalender

# Logs verfolgen
sudo journalctl -u terminkalender -f
```

### **Reverse Proxy (Nginx)**

#### **Nginx installieren**
```bash
sudo apt install nginx -y  # Ubuntu/Debian
sudo yum install nginx -y  # CentOS/RHEL
```

#### **Konfiguration**
```bash
sudo cat > /etc/nginx/sites-available/terminkalender << EOF
server {
    listen 80;
    server_name your-domain.com;

    location / {
        proxy_pass http://localhost:8080;
        proxy_set_header Host \$host;
        proxy_set_header X-Real-IP \$remote_addr;
        proxy_set_header X-Forwarded-For \$proxy_add_x_forwarded_for;
        proxy_set_header X-Forwarded-Proto \$scheme;
        
        # WebSocket support
        proxy_http_version 1.1;
        proxy_set_header Upgrade \$http_upgrade;
        proxy_set_header Connection "upgrade";
        
        # Timeouts
        proxy_connect_timeout 30s;
        proxy_send_timeout 30s;
        proxy_read_timeout 30s;
    }

    # Health check endpoint
    location /actuator/health {
        proxy_pass http://localhost:8080/actuator/health;
        access_log off;
    }

    # Static files caching
    location ~* \.(css|js|png|jpg|jpeg|gif|ico|svg)$ {
        proxy_pass http://localhost:8080;
        expires 1y;
        add_header Cache-Control "public, immutable";
    }
}
EOF

# Site aktivieren
sudo ln -s /etc/nginx/sites-available/terminkalender /etc/nginx/sites-enabled/
sudo nginx -t
sudo systemctl restart nginx
```

---

## ☁️ Azure Cloud Deployment

### **Option A: Azure App Service**

#### **1. Azure CLI Setup**
```bash
# Azure CLI installieren (falls nicht vorhanden)
curl -sL https://aka.ms/InstallAzureCLIDeb | sudo bash

# Anmelden
az login

# Resource Group erstellen
az group create --name terminkalender-rg --location "West Europe"
```

#### **2. App Service Plan erstellen**
```bash
az appservice plan create \
  --name terminkalender-plan \
  --resource-group terminkalender-rg \
  --sku B1 \
  --is-linux
```

#### **3. Web App erstellen**
```bash
az webapp create \
  --name terminkalender-app-unique \
  --resource-group terminkalender-rg \
  --plan terminkalender-plan \
  --runtime "JAVA:17-java17"
```

#### **4. JAR-Deployment**
```bash
# JAR bauen
mvn clean package -DskipTests

# Deployment
az webapp deploy \
  --resource-group terminkalender-rg \
  --name terminkalender-app-unique \
  --src-path target/terminkalender-*.jar \
  --type jar
```

#### **5. App-Einstellungen konfigurieren**
```bash
az webapp config appsettings set \
  --resource-group terminkalender-rg \
  --name terminkalender-app-unique \
  --settings \
    SPRING_PROFILES_ACTIVE=prod \
    DB_USERNAME=dbadmin \
    DB_PASSWORD=SecurePassword123! \
    WEBSITES_PORT=8080
```

### **Option B: Azure Container Instances**

#### **1. Container Registry erstellen**
```bash
az acr create \
  --resource-group terminkalender-rg \
  --name terminkalenderacr \
  --sku Basic \
  --admin-enabled true
```

#### **2. Image bauen und pushen**
```bash
# Registry-Login
az acr login --name terminkalenderacr

# Image bauen
docker build -t terminkalenderacr.azurecr.io/terminkalender:latest .

# Image pushen
docker push terminkalenderacr.azurecr.io/terminkalender:latest
```

#### **3. Container Instance erstellen**
```bash
az container create \
  --resource-group terminkalender-rg \
  --name terminkalender-ci \
  --image terminkalenderacr.azurecr.io/terminkalender:latest \
  --cpu 1 \
  --memory 2 \
  --registry-login-server terminkalenderacr.azurecr.io \
  --registry-username $(az acr credential show --name terminkalenderacr --query username -o tsv) \
  --registry-password $(az acr credential show --name terminkalenderacr --query passwords[0].value -o tsv) \
  --dns-name-label terminkalender-unique \
  --ports 8080 \
  --environment-variables \
    SPRING_PROFILES_ACTIVE=prod \
    DB_USERNAME=dbadmin \
    DB_PASSWORD=SecurePassword123!
```

---

## ☸️ Kubernetes Deployment

### **Kubernetes Manifests**

#### **1. Namespace**
```yaml
# k8s/namespace.yaml
apiVersion: v1
kind: Namespace
metadata:
  name: terminkalender
```

#### **2. ConfigMap**
```yaml
# k8s/configmap.yaml
apiVersion: v1
kind: ConfigMap
metadata:
  name: terminkalender-config
  namespace: terminkalender
data:
  application-prod.properties: |
    spring.profiles.active=prod
    spring.datasource.url=jdbc:mysql://mysql-service:3306/terminkalender?serverTimezone=Europe/Berlin
    spring.datasource.username=terminkalender
    spring.datasource.password=password123
    management.endpoints.web.exposure.include=health,info,metrics
    logging.level.de.swtp1.terminkalender=INFO
```

#### **3. Secret**
```yaml
# k8s/secret.yaml
apiVersion: v1
kind: Secret
metadata:
  name: terminkalender-secret
  namespace: terminkalender
type: Opaque
data:
  db-username: dGVybWlua2FsZW5kZXI=  # terminkalender (base64)
  db-password: cGFzc3dvcmQxMjM=      # password123 (base64)
  jwt-secret: bXlfc2VjcmV0X2p3dF9rZXk=  # my_secret_jwt_key (base64)
```

#### **4. Deployment**
```yaml
# k8s/deployment.yaml
apiVersion: apps/v1
kind: Deployment
metadata:
  name: terminkalender
  namespace: terminkalender
  labels:
    app: terminkalender
spec:
  replicas: 2
  selector:
    matchLabels:
      app: terminkalender
  template:
    metadata:
      labels:
        app: terminkalender
    spec:
      containers:
      - name: terminkalender
        image: terminkalender:latest
        ports:
        - containerPort: 8080
        env:
        - name: SPRING_PROFILES_ACTIVE
          value: "prod"
        - name: DB_USERNAME
          valueFrom:
            secretKeyRef:
              name: terminkalender-secret
              key: db-username
        - name: DB_PASSWORD
          valueFrom:
            secretKeyRef:
              name: terminkalender-secret
              key: db-password
        - name: JWT_SECRET
          valueFrom:
            secretKeyRef:
              name: terminkalender-secret
              key: jwt-secret
        volumeMounts:
        - name: config-volume
          mountPath: /app/config
        livenessProbe:
          httpGet:
            path: /actuator/health
            port: 8080
          initialDelaySeconds: 60
          periodSeconds: 30
        readinessProbe:
          httpGet:
            path: /actuator/health
            port: 8080
          initialDelaySeconds: 30
          periodSeconds: 10
        resources:
          requests:
            memory: "1Gi"
            cpu: "500m"
          limits:
            memory: "2Gi"
            cpu: "1000m"
      volumes:
      - name: config-volume
        configMap:
          name: terminkalender-config
```

#### **5. Service**
```yaml
# k8s/service.yaml
apiVersion: v1
kind: Service
metadata:
  name: terminkalender-service
  namespace: terminkalender
spec:
  selector:
    app: terminkalender
  ports:
  - protocol: TCP
    port: 80
    targetPort: 8080
  type: LoadBalancer
```

#### **6. Ingress (optional)**
```yaml
# k8s/ingress.yaml
apiVersion: networking.k8s.io/v1
kind: Ingress
metadata:
  name: terminkalender-ingress
  namespace: terminkalender
  annotations:
    nginx.ingress.kubernetes.io/rewrite-target: /
spec:
  rules:
  - host: terminkalender.your-domain.com
    http:
      paths:
      - path: /
        pathType: Prefix
        backend:
          service:
            name: terminkalender-service
            port:
              number: 80
```

### **Deployment ausführen**
```bash
# Alle Manifests anwenden
kubectl apply -f k8s/

# Status prüfen
kubectl get all -n terminkalender

# Logs anzeigen
kubectl logs -f deployment/terminkalender -n terminkalender

# Service testen
kubectl port-forward svc/terminkalender-service 8080:80 -n terminkalender
```

---

## 📊 Monitoring & Observability

### **Prometheus & Grafana**

#### **Prometheus Konfiguration**
```yaml
# prometheus.yml
global:
  scrape_interval: 15s

scrape_configs:
  - job_name: 'terminkalender'
    static_configs:
      - targets: ['localhost:8080']
    metrics_path: '/actuator/prometheus'
    scrape_interval: 5s
```

#### **Grafana Dashboard**
- **Import Dashboard**: Spring Boot 2.1 System & JVM Metrics (ID: 11378)
- **Custom Metrics**: HTTP Request Rate, Database Connections, Error Rate

### **ELK Stack (Elasticsearch, Logstash, Kibana)**

#### **Logback-Konfiguration**
```xml
<!-- logback-spring.xml -->
<configuration>
    <appender name="STDOUT" class="ch.qos.logback.core.ConsoleAppender">
        <encoder class="net.logstash.logback.encoder.LoggingEventCompositeJsonEncoder">
            <providers>
                <timestamp/>
                <logLevel/>
                <loggerName/>
                <message/>
                <mdc/>
                <arguments/>
                <stackTrace/>
            </providers>
        </encoder>
    </appender>
    
    <root level="INFO">
        <appender-ref ref="STDOUT"/>
    </root>
</configuration>
```

---

## 🔐 Sicherheits-Härtung

### **Produktions-Sicherheit**

#### **JWT-Secret externalisieren**
```bash
# Sicheren JWT-Secret generieren
openssl rand -base64 64

# Als Umgebungsvariable setzen
export JWT_SECRET="your-generated-secret-here"
```

#### **Database-Verbindung sichern**
```properties
# SSL-Verbindung erzwingen
spring.datasource.url=jdbc:mysql://server:3306/terminkalender?useSSL=true&requireSSL=true&serverTimezone=Europe/Berlin

# Connection Pool Limits
spring.datasource.hikari.maximum-pool-size=10
spring.datasource.hikari.minimum-idle=5
spring.datasource.hikari.connection-timeout=20000
```

#### **Actuator-Endpunkte sichern**
```yaml
management:
  endpoints:
    web:
      exposure:
        include: health,info,metrics
      base-path: /actuator
  endpoint:
    health:
      show-details: when-authorized
  security:
    enabled: true
```

---

## 📈 Performance-Optimierung

### **JVM-Tuning**
```bash
java -Xmx4g -Xms2g \
  -XX:+UseG1GC \
  -XX:MaxGCPauseMillis=200 \
  -XX:+UseStringDeduplication \
  -XX:+OptimizeStringConcat \
  -jar terminkalender.jar
```

### **Database-Optimierung**
```sql
-- Index für häufige Queries
CREATE INDEX idx_appointment_user_date ON appointments(user_id, start_date_time);
CREATE INDEX idx_appointment_date_range ON appointments(start_date_time, end_date_time);
CREATE INDEX idx_holiday_date ON holidays(date);

-- Partitionierung für große Datenmengen (optional)
ALTER TABLE appointments 
PARTITION BY RANGE (YEAR(start_date_time)) (
    PARTITION p2024 VALUES LESS THAN (2025),
    PARTITION p2025 VALUES LESS THAN (2026),
    PARTITION p2026 VALUES LESS THAN (2027)
);
```

---

## ✅ Deployment-Checkliste

### **Pre-Deployment**
- [ ] Tests erfolgreich (`mvn test`)
- [ ] JAR-Build erfolgreich (`mvn clean package`)
- [ ] Konfiguration für Zielumgebung angepasst
- [ ] Database-Migration-Scripts vorbereitet
- [ ] Backup-Strategie definiert
- [ ] Monitoring-Setup vorbereitet

### **Deployment**
- [ ] JAR in Zielumgebung übertragen
- [ ] Umgebungsvariablen gesetzt
- [ ] Service gestartet
- [ ] Health-Check erfolgreich
- [ ] Funktionstest durchgeführt
- [ ] Logs auf Fehler geprüft

### **Post-Deployment**
- [ ] Monitoring-Dashboard konfiguriert
- [ ] Alerting eingerichtet
- [ ] Backup-Jobs geplant
- [ ] Load-Test durchgeführt (optional)
- [ ] Rollback-Plan dokumentiert
- [ ] Team über Deployment informiert

---

**Damit ist Ihr Terminkalender bereit für jede Umgebung - von lokaler Entwicklung bis zur Enterprise-Produktion!** 🚀