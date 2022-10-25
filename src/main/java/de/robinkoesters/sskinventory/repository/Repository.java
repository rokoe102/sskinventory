package de.robinkoesters.sskinventory.repository;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.sqlite.SQLiteConfig;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class Repository {

    private static final Logger PARENTLOGGER = LoggerFactory.getLogger(Repository.class);

    Connection conn;

    public Repository() {

        SQLiteConfig config = new SQLiteConfig();
        config.enforceForeignKeys(true);
        config.setJournalMode(SQLiteConfig.JournalMode.WAL);

        try {
            conn = DriverManager.getConnection("jdbc:sqlite:inventory.db", config.toProperties());
        } catch (SQLException e) {
            PARENTLOGGER.error(e.getMessage(), e);
        }
    }

    public void closeConnection() throws SQLException {
        conn.close();
    }
}
