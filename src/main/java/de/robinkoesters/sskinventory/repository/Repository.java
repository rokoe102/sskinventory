package de.robinkoesters.sskinventory.repository;

import org.sqlite.SQLiteConfig;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class Repository {

    Connection conn;

    public Repository() {

        SQLiteConfig config = new SQLiteConfig();
        config.enforceForeignKeys(true);
        config.setJournalMode(SQLiteConfig.JournalMode.WAL);

        try {
            conn = DriverManager.getConnection("jdbc:sqlite:inventory.db", config.toProperties());
            System.out.println("Verbindung des Repository zur Datenbank erfolgreich.");
        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println("Verbindung des Repository zur Datenbank fehlgeschlagen!");
        }
    }

    public void closeConnection() throws SQLException {
        conn.close();
    }
}
