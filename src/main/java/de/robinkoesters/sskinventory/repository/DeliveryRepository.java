package de.robinkoesters.sskinventory.repository;

import de.robinkoesters.sskinventory.entity.Component;

import java.sql.PreparedStatement;
import java.sql.SQLException;

public class DeliveryRepository extends Repository {

    public DeliveryRepository() {
        super();
    }

    public String submitDelivery(Component component, int amount) throws SQLException {
        PreparedStatement stmnt = conn.prepareStatement("UPDATE COMPONENT SET amount = amount + ? WHERE identifier = ?");
        stmnt.setInt(1, amount);
        stmnt.setString(2, component.getIdentifier());

        stmnt.executeUpdate();
        return "Anlieferung erfolgreich übermittelt!";
    }
}
