# Azure MySQL Benutzer-Aktivierung
# Aktiviert alle Benutzer in der Azure MySQL Datenbank

import mysql.connector
import sys

def activate_all_users():
    try:
        # Verbindung zur Azure MySQL Datenbank
        connection = mysql.connector.connect(
            host='dbotk25.mysql.database.azure.com',
            user='fbmb16',
            password='PalKauf91',
            database='terminkalender',
            port=3306,
            ssl_disabled=False,
            auth_plugin='mysql_native_password'
        )
        
        cursor = connection.cursor()
        
        # Alle Benutzer aktivieren
        print("🔄 Aktiviere alle Benutzer...")
        update_query = "UPDATE users SET is_active = TRUE"
        cursor.execute(update_query)
        connection.commit()
        
        # Prüfe den Status
        cursor.execute("SELECT id, username, email, is_active FROM users")
        users = cursor.fetchall()
        
        print("✅ Benutzer-Status nach Update:")
        for user in users:
            print(f"   ID: {user[0]}, Username: {user[1]}, Email: {user[2]}, Aktiv: {user[3]}")
        
        print(f"✅ {cursor.rowcount} Benutzer wurden aktiviert!")
        
    except mysql.connector.Error as error:
        print(f"❌ Fehler bei der Datenbankverbindung: {error}")
        return False
    
    finally:
        if connection.is_connected():
            cursor.close()
            connection.close()
    
    return True

if __name__ == "__main__":
    print("🚀 Azure MySQL Benutzer-Aktivierung")
    print("=" * 40)
    
    success = activate_all_users()
    
    if success:
        print("\n✅ Alle Benutzer wurden erfolgreich aktiviert!")
        print("   Du kannst dich jetzt mit 'admin' / 'admin123' anmelden.")
    else:
        print("\n❌ Fehler beim Aktivieren der Benutzer!")
        sys.exit(1)