package de.robinkoesters.sskinventory.controller;

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

    private MainViewController mainViewController;
    private ComponentRepository componentRepository;
    private DeliveryRepository deliveryRepository;

    @FXML
    private ComboBox<Component> componentBox;
    @FXML private TextField amountField;
    @FXML private Button queryButton;
    @FXML private TextField errorField;

    public void setMainViewController(MainViewController mainViewController) {
        this.mainViewController = mainViewController;
    }

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
            errorField.setStyle("-fx-text-fill: red; -fx-font-size: 14px;");
            errorField.setText(e.getMessage());
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
        errorField.setText("");
        boolean success = validateFields();
        if (success) {
            try {
                Component component = componentBox.getSelectionModel().getSelectedItem();
                int amount = Integer.parseInt(amountField.getText());

                errorField.setStyle("-fx-text-fill: greenyellow; -fx-font-size: 14px;");
                errorField.setText(deliveryRepository.submitDelivery(component, amount));
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
            errorField.setStyle("-fx-text-fill: red; -fx-font-size: 14px;");
            errorField.setText("Bitte natürliche Zahl für Menge eingeben!");
        }
        return success;
    }
}
