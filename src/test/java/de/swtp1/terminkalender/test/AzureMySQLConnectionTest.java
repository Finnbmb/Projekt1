package de.swtp1.terminkalender.test;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class AzureMySQLConnectionTest {
    public static void main(String[] args) {
        // Azure MySQL Connection Details - Testing different formats
        String url1 = "jdbc:mysql://dbotk25.mysql.database.azure.com:3306/?useSSL=true&requireSSL=true&serverTimezone=UTC&allowPublicKeyRetrieval=true";
        String username1 = "fbmb16";
        String username2 = "fbmb16@dbotk25";
        String password = "PalKauf91";
        
        System.out.println("🔍 Testing Azure MySQL Connection...");
        System.out.println("Server: dbotk25.mysql.database.azure.com");
        System.out.println("Testing different username formats");
        System.out.println("----------------------------------------");
        
        try {
            // Load MySQL JDBC Driver
            Class.forName("com.mysql.cj.jdbc.Driver");
            System.out.println("✅ MySQL JDBC Driver loaded successfully");
            
            // Test connection with first username format
            System.out.println("🔗 Testing with username: " + username1);
            Connection connection = null;
            
            try {
                connection = DriverManager.getConnection(url1, username1, password);
            } catch (SQLException e1) {
                System.out.println("❌ Failed with username format 1: " + e1.getMessage());
                System.out.println("🔗 Testing with username: " + username2);
                try {
                    connection = DriverManager.getConnection(url1, username2, password);
                } catch (SQLException e2) {
                    System.out.println("❌ Failed with username format 2: " + e2.getMessage());
                    throw e2; // Re-throw the last exception
                }
            }
            
            if (connection != null && !connection.isClosed()) {
                System.out.println("✅ SUCCESS: Connected to Azure MySQL!");
                System.out.println("📊 Connection Info:");
                System.out.println("   - Database Product: " + connection.getMetaData().getDatabaseProductName());
                System.out.println("   - Database Version: " + connection.getMetaData().getDatabaseProductVersion());
                System.out.println("   - Driver Version: " + connection.getMetaData().getDriverVersion());
                
                // Test a simple query
                var statement = connection.createStatement();
                var resultSet = statement.executeQuery("SELECT 1 as test_value");
                if (resultSet.next()) {
                    System.out.println("✅ Database query test successful!");
                }
                
                connection.close();
                System.out.println("✅ Connection closed successfully");
            }
            
        } catch (ClassNotFoundException e) {
            System.err.println("❌ ERROR: MySQL JDBC Driver not found!");
            System.err.println("   Make sure mysql-connector-j is in your classpath");
            e.printStackTrace();
        } catch (SQLException e) {
            System.err.println("❌ ERROR: Failed to connect to Azure MySQL!");
            System.err.println("   SQL State: " + e.getSQLState());
            System.err.println("   Error Code: " + e.getErrorCode());
            System.err.println("   Message: " + e.getMessage());
            
            // Common error diagnostics
            if (e.getMessage().contains("Access denied")) {
                System.err.println("💡 TIP: Check username and password");
            } else if (e.getMessage().contains("timeout") || e.getMessage().contains("refused")) {
                System.err.println("💡 TIP: Check if Azure MySQL server allows connections from your IP");
            } else if (e.getMessage().contains("SSL")) {
                System.err.println("💡 TIP: SSL connection issue - check SSL configuration");
            }
        } catch (Exception e) {
            System.err.println("❌ UNEXPECTED ERROR:");
            e.printStackTrace();
        }
    }
}