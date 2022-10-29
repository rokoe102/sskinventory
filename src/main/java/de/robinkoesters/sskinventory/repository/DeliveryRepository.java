package de.robinkoesters.sskinventory.repository;

import de.robinkoesters.sskinventory.entity.Component;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.PreparedStatement;
import java.sql.SQLException;

public class DeliveryRepository extends Repository {

    private static final Logger LOGGER = LoggerFactory.getLogger(DeliveryRepository.class);

    public DeliveryRepository() {
        super();
    }

    public String submitDelivery(String component, int amount) throws SQLException {
        PreparedStatement stmnt = conn.prepareStatement("UPDATE COMPONENT SET amount = amount + ? WHERE identifier = ?");
        stmnt.setInt(1, amount);
        stmnt.setString(2, component);

        stmnt.executeUpdate();

        LOGGER.info("increased amount of " + component + " by " + amount);

        return "Anlieferung erfolgreich übermittelt!";
    }
}
