package de.swtp1.terminkalender.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Utility-Klasse um alle Benutzer in der Azure MySQL Datenbank zu aktivieren
 */
public class ActivateAllUsers {
    
    private static final String DB_URL = "jdbc:mysql://dbotk25.mysql.database.azure.com:3306/terminkalender?useSSL=true&requireSSL=false&serverTimezone=UTC";
    private static final String DB_USER = "fbmb16";
    private static final String DB_PASSWORD = "PalKauf91";
    
    public static void main(String[] args) {
        System.out.println("🚀 Azure MySQL Benutzer-Aktivierung");
        System.out.println("========================================");
        
        try {
            // MySQL JDBC Treiber laden
            Class.forName("com.mysql.cj.jdbc.Driver");
            
            // Verbindung zur Datenbank herstellen
            try (Connection connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD)) {
                System.out.println("✅ Verbindung zur Azure MySQL hergestellt");
                
                // Alle Benutzer aktivieren
                String updateSQL = "UPDATE users SET is_active = TRUE";
                try (PreparedStatement updateStmt = connection.prepareStatement(updateSQL)) {
                    int updatedRows = updateStmt.executeUpdate();
                    System.out.println("🔄 " + updatedRows + " Benutzer wurden aktiviert");
                }
                
                // Aktuellen Status der Benutzer anzeigen
                String selectSQL = "SELECT id, username, email, is_active FROM users";
                try (PreparedStatement selectStmt = connection.prepareStatement(selectSQL);
                     ResultSet rs = selectStmt.executeQuery()) {
                    
                    System.out.println("\n📋 Benutzer-Status:");
                    System.out.println("ID | Username | Email | Aktiv");
                    System.out.println("---|----------|-------|------");
                    
                    while (rs.next()) {
                        int id = rs.getInt("id");
                        String username = rs.getString("username");
                        String email = rs.getString("email");
                        boolean isActive = rs.getBoolean("is_active");
                        
                        System.out.printf("%2d | %-8s | %-20s | %s%n", 
                            id, username, email, isActive ? "✅" : "❌");
                    }
                }
                
                System.out.println("\n✅ Alle Benutzer wurden erfolgreich aktiviert!");
                System.out.println("   Du kannst dich jetzt mit 'admin' / 'admin123' anmelden.");
                
            }
            
        } catch (ClassNotFoundException e) {
            System.err.println("❌ MySQL JDBC Treiber nicht gefunden: " + e.getMessage());
        } catch (SQLException e) {
            System.err.println("❌ Datenbankfehler: " + e.getMessage());
            e.printStackTrace();
        }
    }
}