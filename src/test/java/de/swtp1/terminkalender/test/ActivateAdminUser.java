package de.swtp1.terminkalender.test;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;

public class ActivateAdminUser {
    public static void main(String[] args) {
        String url = "jdbc:mysql://dbotk25.mysql.database.azure.com:3306/terminkalender?useSSL=true&requireSSL=false&serverTimezone=UTC";
        String username = "fbmb16";
        String password = "PalKauf91";
        
        try (Connection conn = DriverManager.getConnection(url, username, password);
             Statement stmt = conn.createStatement()) {
            
            // Admin-User aktivieren
            int result = stmt.executeUpdate("UPDATE users SET is_active = 1 WHERE username = 'admin'");
            System.out.println("✅ Admin-User wurde aktiviert! Affected rows: " + result);
            
        } catch (Exception e) {
            System.err.println("❌ Fehler beim Aktivieren des Admin-Users: " + e.getMessage());
            e.printStackTrace();
        }
    }
}