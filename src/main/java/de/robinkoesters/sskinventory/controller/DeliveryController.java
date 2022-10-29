package de.robinkoesters.sskinventory.controller;

import de.robinkoesters.sskinventory.dialogs.InventoryDialog;
import de.robinkoesters.sskinventory.entity.Component;
import de.robinkoesters.sskinventory.entity.ComponentAssignment;
import de.robinkoesters.sskinventory.entity.ComponentDelivery;
import de.robinkoesters.sskinventory.repository.ComponentRepository;
import de.robinkoesters.sskinventory.repository.DeliveryRepository;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;

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
    @FXML private TableView<ComponentDelivery> componentTable;
    @FXML private TableColumn<ComponentDelivery, String> componentCol;
    @FXML private TableColumn<ComponentDelivery, Integer> amountCol;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        updateView();

        componentCol.setCellValueFactory(new PropertyValueFactory<>("component"));
        amountCol.setCellValueFactory(new PropertyValueFactory<>("amount"));
    }

    public void updateView() {
        try {
            componentRepository = new ComponentRepository();
            deliveryRepository = new DeliveryRepository();
            componentBox.getItems().setAll(componentRepository.findAllComponentsForListView());
            queryButton.setDisable(true);
        } catch (SQLException e) {
            InventoryDialog dialog = new InventoryDialog(InventoryDialog.ERROR, e.getMessage());
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
    public void onAddition() {
        Component component = componentBox.getSelectionModel().getSelectedItem();
        if (component != null && !amountField.getText().equals("")) {
            int amount = Integer.parseInt(amountField.getText());
            if (amount > 0) {
                componentTable.getItems().add(new ComponentDelivery(component.getIdentifier(), amount));
            }
        }
    }

    @FXML
    public void onQueryStarted() {
        for (ComponentDelivery delivery : componentTable.getItems()) {
            try {
                String component = delivery.getComponent();
                int amount = delivery.getAmount();

                InventoryDialog dialog = new InventoryDialog(InventoryDialog.INFO, deliveryRepository.submitDelivery(component, amount));
                dialog.showInformation();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        componentTable.getItems().clear();
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
