package de.robinkoesters.sskinventory.controller;

import de.robinkoesters.sskinventory.dialogs.InventoryDialog;
import de.robinkoesters.sskinventory.entity.Component;
import de.robinkoesters.sskinventory.repository.ComponentRepository;
import de.robinkoesters.sskinventory.repository.DeliveryRepository;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;

import java.net.URL;
import java.sql.SQLException;
import java.util.ResourceBundle;

public class DeliveryController implements Initializable {

    private ComponentRepository componentRepository;
    private DeliveryRepository deliveryRepository;

    @FXML
    private ComboBox<Component> componentBox;
    @FXML private TextField amountField;
    @FXML private Button queryButton;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        updateView();
    }

    public void updateView() {
        try {
            componentRepository = new ComponentRepository();
            deliveryRepository = new DeliveryRepository();
            componentBox.getItems().setAll(componentRepository.findAllComponentsForListView());
            queryButton.setDisable(true);
        } catch (SQLException e) {
            InventoryDialog dialog = new InventoryDialog("Fehler", e.getMessage());
            dialog.showError();
        }

    }

    @FXML
    public void onSelection() {
        if (!amountField.getText().equals("")) {
            queryButton.setDisable(false);
        }
    }

    @FXML
    public void onTyped() {
        if (componentBox.getSelectionModel().getSelectedItem() != null) {
            queryButton.setDisable(false);
        }
    }

    @FXML
    public void onQueryStarted() {
        boolean success = validateFields();
        if (success) {
            try {
                Component component = componentBox.getSelectionModel().getSelectedItem();
                int amount = Integer.parseInt(amountField.getText());

                InventoryDialog dialog = new InventoryDialog("Information", deliveryRepository.submitDelivery(component, amount));
                dialog.showInformation();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

    }

    private boolean validateFields() {
        boolean success = true;
        try {
            int amount = Integer.parseInt(amountField.getText());
        } catch (NumberFormatException nfe) {
            success = false;
            InventoryDialog dialog = new InventoryDialog("Fehler", "Bitte natürliche Zahl für Menge eingeben!");
            dialog.showError();
        }
        return success;
    }
}
