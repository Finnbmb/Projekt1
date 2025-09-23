package de.swtp1.terminkalender.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Eigener Database Controller für direkten Datenbankzugriff
 * Umgeht Spring Security komplett
 */
@Controller
@RequestMapping("/database")
public class DatabaseController {

    @Autowired
    private DataSource dataSource;

    /**
     * Zeigt eine einfache Datenbank-Übersicht
     */
    @GetMapping("/view")
    @ResponseBody
    public String databaseView() {
        StringBuilder html = new StringBuilder();
        html.append("<!DOCTYPE html>");
        html.append("<html><head><title>Datenbank Übersicht</title>");
        html.append("<style>");
        html.append("body { font-family: Arial, sans-serif; margin: 20px; }");
        html.append("table { border-collapse: collapse; width: 100%; margin: 20px 0; }");
        html.append("th, td { border: 1px solid #ddd; padding: 8px; text-align: left; }");
        html.append("th { background-color: #f2f2f2; }");
        html.append("h2 { color: #333; }");
        html.append("</style>");
        html.append("</head><body>");
        html.append("<h1>Terminkalender Datenbank</h1>");
        html.append("<div style='margin: 20px 0; padding: 15px; background-color: #fff3cd; border: 1px solid #ffeaa7; border-radius: 5px;'>");
        html.append("<h3>🧹 Datenbank Bereinigung</h3>");
        html.append("<p>Problematische Daten entfernen:</p>");
        html.append("<a href='/database/cleanup-null-appointments' style='background-color: #dc3545; color: white; padding: 8px 16px; text-decoration: none; border-radius: 4px; margin-right: 10px;'>🗑️ NULL-Termine löschen</a>");
        html.append("<a href='/database/cleanup-duplicate-users' style='background-color: #fd7e14; color: white; padding: 8px 16px; text-decoration: none; border-radius: 4px; margin-right: 10px;'>👥 Doppelte User löschen</a>");
        html.append("<a href='/database/add-reminder-column' style='background-color: #28a745; color: white; padding: 8px 16px; text-decoration: none; border-radius: 4px;'>⏰ Erinnerungs-Spalte hinzufügen</a>");
        html.append("</div>");

        try (Connection conn = dataSource.getConnection()) {
            // Users Tabelle
            html.append("<h2>Benutzer (USERS)</h2>");
            html.append(getTableData(conn, "SELECT id, username, name, email, role, is_active FROM users"));

            // Appointments Tabelle
            html.append("<h2>Termine (APPOINTMENTS)</h2>");
            html.append(getTableData(conn, "SELECT id, title, start_date_time, end_date_time, user_id, priority, reminder_minutes FROM appointments"));

            // Holidays Tabelle
            html.append("<h2>Feiertage (HOLIDAYS)</h2>");
            html.append(getTableData(conn, "SELECT id, name, date, federal_state, holiday_type FROM holidays LIMIT 10"));

        } catch (Exception e) {
            html.append("<p style='color: red;'>Fehler beim Datenbankzugriff: ").append(e.getMessage()).append("</p>");
        }

        html.append("</body></html>");
        return html.toString();
    }

    /**
     * API für Users
     */
    @GetMapping("/api/users")
    @ResponseBody
    public List<Map<String, Object>> getUsers() {
        List<Map<String, Object>> users = new ArrayList<>();
        try (Connection conn = dataSource.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT id, username, name, email, role, is_active, created_at FROM users")) {
            
            while (rs.next()) {
                Map<String, Object> user = new HashMap<>();
                user.put("id", rs.getLong("id"));
                user.put("username", rs.getString("username"));
                user.put("name", rs.getString("name"));
                user.put("email", rs.getString("email"));
                user.put("role", rs.getString("role"));
                user.put("isActive", rs.getBoolean("is_active"));
                user.put("createdAt", rs.getTimestamp("created_at"));
                users.add(user);
            }
        } catch (Exception e) {
            Map<String, Object> error = new HashMap<>();
            error.put("error", e.getMessage());
            users.add(error);
        }
        return users;
    }

    /**
     * API für Appointments
     */
    @GetMapping("/api/appointments")
    @ResponseBody
    public List<Map<String, Object>> getAppointments() {
        List<Map<String, Object>> appointments = new ArrayList<>();
        try (Connection conn = dataSource.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT a.id, a.title, a.start_date_time, a.end_date_time, a.user_id, a.reminder_minutes, u.username FROM appointments a LEFT JOIN users u ON a.user_id = u.id")) {
            
            while (rs.next()) {
                Map<String, Object> appointment = new HashMap<>();
                appointment.put("id", rs.getLong("id"));
                appointment.put("title", rs.getString("title"));
                appointment.put("startDateTime", rs.getTimestamp("start_date_time"));
                appointment.put("endDateTime", rs.getTimestamp("end_date_time"));
                appointment.put("userId", rs.getLong("user_id"));
                appointment.put("reminderMinutes", rs.getObject("reminder_minutes"));
                appointment.put("username", rs.getString("username"));
                appointments.add(appointment);
            }
        } catch (Exception e) {
            Map<String, Object> error = new HashMap<>();
            error.put("error", e.getMessage());
            appointments.add(error);
        }
        return appointments;
    }

    private String getTableData(Connection conn, String sql) {
        StringBuilder table = new StringBuilder();
        table.append("<table>");
        
        try (Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            // Header
            table.append("<tr>");
            int columnCount = rs.getMetaData().getColumnCount();
            for (int i = 1; i <= columnCount; i++) {
                table.append("<th>").append(rs.getMetaData().getColumnName(i)).append("</th>");
            }
            table.append("</tr>");

            // Data
            while (rs.next()) {
                table.append("<tr>");
                for (int i = 1; i <= columnCount; i++) {
                    Object value = rs.getObject(i);
                    table.append("<td>").append(value != null ? value.toString() : "NULL").append("</td>");
                }
                table.append("</tr>");
            }
            
        } catch (Exception e) {
            table.append("<tr><td colspan='10' style='color: red;'>Fehler: ").append(e.getMessage()).append("</td></tr>");
        }
        
        table.append("</table>");
        return table.toString();
    }

    /**
     * Löscht alle Termine mit NULL user_id
     */
    @GetMapping("/cleanup-null-appointments")
    @ResponseBody
    public String cleanupNullAppointments() {
        try (Connection conn = dataSource.getConnection()) {
            Statement stmt = conn.createStatement();
            int deletedCount = stmt.executeUpdate("DELETE FROM appointments WHERE user_id IS NULL");
            
            return "<html><body style='font-family: Arial, sans-serif; padding: 20px;'>" +
                   "<h2>✅ Bereinigung abgeschlossen</h2>" +
                   "<p><strong>" + deletedCount + "</strong> Termine mit NULL user_id wurden gelöscht.</p>" +
                   "<a href='/database/view' style='background-color: #007bff; color: white; padding: 8px 16px; text-decoration: none; border-radius: 4px;'>🔙 Zurück zur Übersicht</a>" +
                   "</body></html>";
                   
        } catch (Exception e) {
            return "<html><body style='font-family: Arial, sans-serif; padding: 20px;'>" +
                   "<h2>❌ Fehler</h2>" +
                   "<p>Fehler beim Löschen: " + e.getMessage() + "</p>" +
                   "<a href='/database/view' style='background-color: #007bff; color: white; padding: 8px 16px; text-decoration: none; border-radius: 4px;'>🔙 Zurück zur Übersicht</a>" +
                   "</body></html>";
        }
    }

    /**
     * Löscht doppelte Test-User (behält nur testuser und admin)
     */
    @GetMapping("/cleanup-duplicate-users")
    @ResponseBody
    public String cleanupDuplicateUsers() {
        try (Connection conn = dataSource.getConnection()) {
            Statement stmt = conn.createStatement();
            int deletedCount = stmt.executeUpdate(
                "DELETE FROM users WHERE username NOT IN ('testuser', 'admin') AND username LIKE 'testuser_%'"
            );
            
            return "<html><body style='font-family: Arial, sans-serif; padding: 20px;'>" +
                   "<h2>✅ Bereinigung abgeschlossen</h2>" +
                   "<p><strong>" + deletedCount + "</strong> doppelte Test-User wurden gelöscht.</p>" +
                   "<p>Behalten: <code>testuser</code> und <code>admin</code></p>" +
                   "<a href='/database/view' style='background-color: #007bff; color: white; padding: 8px 16px; text-decoration: none; border-radius: 4px;'>🔙 Zurück zur Übersicht</a>" +
                   "</body></html>";
                   
        } catch (Exception e) {
            return "<html><body style='font-family: Arial, sans-serif; padding: 20px;'>" +
                   "<h2>❌ Fehler</h2>" +
                   "<p>Fehler beim Löschen: " + e.getMessage() + "</p>" +
                   "<a href='/database/view' style='background-color: #007bff; color: white; padding: 8px 16px; text-decoration: none; border-radius: 4px;'>🔙 Zurück zur Übersicht</a>" +
                   "</body></html>";
        }
    }

    /**
     * Fügt die reminder_minutes Spalte zur appointments Tabelle hinzu
     */
    @GetMapping("/add-reminder-column")
    @ResponseBody
    public String addReminderColumn() {
        try (Connection conn = dataSource.getConnection()) {
            Statement stmt = conn.createStatement();
            
            // Prüfe erst, ob die Spalte bereits existiert
            ResultSet rs = stmt.executeQuery(
                "SELECT COLUMN_NAME FROM INFORMATION_SCHEMA.COLUMNS WHERE TABLE_NAME = 'APPOINTMENTS' AND COLUMN_NAME = 'REMINDER_MINUTES'"
            );
            
            if (rs.next()) {
                return "<html><body style='font-family: Arial, sans-serif; padding: 20px;'>" +
                       "<h2>ℹ️ Spalte bereits vorhanden</h2>" +
                       "<p>Die Spalte <code>reminder_minutes</code> existiert bereits in der appointments Tabelle.</p>" +
                       "<a href='/database/view' style='background-color: #007bff; color: white; padding: 8px 16px; text-decoration: none; border-radius: 4px;'>🔙 Zurück zur Übersicht</a>" +
                       "</body></html>";
            }
            
            // Spalte hinzufügen
            stmt.executeUpdate("ALTER TABLE appointments ADD COLUMN reminder_minutes INTEGER DEFAULT 15");
            
            return "<html><body style='font-family: Arial, sans-serif; padding: 20px;'>" +
                   "<h2>✅ Spalte erfolgreich hinzugefügt</h2>" +
                   "<p>Die Spalte <code>reminder_minutes</code> wurde zur <code>appointments</code> Tabelle hinzugefügt.</p>" +
                   "<p>Standardwert: 15 Minuten</p>" +
                   "<a href='/database/view' style='background-color: #007bff; color: white; padding: 8px 16px; text-decoration: none; border-radius: 4px;'>🔙 Zurück zur Übersicht</a>" +
                   "</body></html>";
                   
        } catch (Exception e) {
            return "<html><body style='font-family: Arial, sans-serif; padding: 20px;'>" +
                   "<h2>❌ Fehler</h2>" +
                   "<p>Fehler beim Hinzufügen der Spalte: " + e.getMessage() + "</p>" +
                   "<a href='/database/view' style='background-color: #007bff; color: white; padding: 8px 16px; text-decoration: none; border-radius: 4px;'>🔙 Zurück zur Übersicht</a>" +
                   "</body></html>";
        }
    }
}