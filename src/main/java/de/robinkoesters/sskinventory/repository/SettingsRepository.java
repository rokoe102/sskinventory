package de.robinkoesters.sskinventory.repository;

import java.sql.PreparedStatement;
import java.sql.SQLException;

public class SettingsRepository extends Repository {

    public SettingsRepository() {
        super();
    }

    public void resetDatabase() throws SQLException {
        String article = "DELETE FROM ARTICLE";
        String component = "DELETE FROM COMPONENT";
        String assignment = "DELETE FROM ASSIGNMENT";

        PreparedStatement stmnt = conn.prepareStatement(article);
        stmnt.executeUpdate();

        stmnt = conn.prepareStatement(component);
        stmnt.executeUpdate();

        stmnt = conn.prepareStatement(assignment);
        stmnt.executeUpdate();
    }

}
