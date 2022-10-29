package de.robinkoesters.sskinventory.controller;

import de.robinkoesters.sskinventory.dialogs.InventoryDialog;
import de.robinkoesters.sskinventory.entity.Article;
import de.robinkoesters.sskinventory.entity.Component;
import de.robinkoesters.sskinventory.entity.ComponentAssignment;
import de.robinkoesters.sskinventory.repository.ArticleRepository;
import de.robinkoesters.sskinventory.repository.ComponentRepository;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;

import java.net.URL;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.ResourceBundle;

public class ComponentDetailController implements Initializable {

    private MainViewController mainViewController;
    private ComponentRepository componentRepository;

    private Component component;
    @FXML
    private TextField identifierField;
    @FXML private TextField numberField;
    @FXML private Button saveComponentButton;
    @FXML private TableView<ComponentAssignment> assignmentTable;
    @FXML private TableColumn<ComponentAssignment, String> idCol;
    @FXML private TableColumn<ComponentAssignment, Integer> amountCol;
    @FXML private TextField amountField;

    public void setMainViewController(MainViewController mainViewController) {
        this.mainViewController = mainViewController;
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        this.componentRepository = new ComponentRepository();
        saveComponentButton.setVisible(false);
        idCol.setCellValueFactory(new PropertyValueFactory<>("article"));
        amountCol.setCellValueFactory(new PropertyValueFactory<>("needed"));
    }

    public void updateView(Component component) {
        this.component = component;

        this.identifierField.setText(component.getIdentifier());
        this.identifierField.setStyle("-fx-text-fill: white;");

        this.numberField.setText(String.valueOf(component.getNumber()));
        this.numberField.setStyle("-fx-text-fill: white;");

        this.amountField.setText(String.valueOf(component.getAmount()));
        this.amountField.setStyle("-fx-text-fill: white;");

        assignmentTable.getItems().setAll(componentRepository.findComponentAssignmentsFor(component));
        ObservableList<String> components = componentRepository.findAllComponentRefs();
        for (ComponentAssignment ca : assignmentTable.getItems()) {
            components.remove(ca.getComponent());
        }
    }

    @FXML
    public void handleIdentifierTyping() {
        saveComponentButton.setVisible(true);
        identifierField.setStyle("-fx-text-fill: aqua;");
    }

    @FXML
    public void handleArticleNoTyping() {
        saveComponentButton.setVisible(true);
        numberField.setStyle("-fx-text-fill: aqua;");
    }

    @FXML
    public void handleAmountTyping() {
        saveComponentButton.setVisible(true);
        amountField.setStyle("-fx-text-fill: aqua;");
    }

    @FXML
    public void handleComponentUpdate() {
        boolean success = validateFields();
        if (success) {
            String ident = identifierField.getText();
            int number = Integer.parseInt(numberField.getText());
            int amount = Integer.parseInt(amountField.getText());

            if (component.isNewEntity() && !identifierField.getText().equals("")) {
                try {
                    component = componentRepository.createNewComponent(new Component(ident, number, amount));
                    saveComponentButton.setVisible(false);
                    updateView(this.component);
                    mainViewController.renameTab("Neue Komponente", component.getIdentifier());
                } catch (SQLException sql) {
                    InventoryDialog dialog = new InventoryDialog(InventoryDialog.ERROR, "Einfügen fehlgeschlagen", sql.getMessage());
                    dialog.showError();
                }
            } else if (component.isExistingEntity()) {
                try {
                    String oldCaption = this.component.getIdentifier();
                    component = componentRepository.updateComponent(component, ident, number, amount);
                    saveComponentButton.setVisible(false);
                    mainViewController.renameTab(oldCaption, component.getIdentifier());
                    updateView(this.component);
                } catch (SQLException sql) {
                    InventoryDialog dialog = new InventoryDialog(InventoryDialog.ERROR, "Änderung fehlgeschlagen", sql.getMessage());
                    dialog.showError();
                }
            }

        }
    }

    public boolean validateFields() {
        boolean success = true;
        if (identifierField.getText().equals("") || identifierField.getText() == null) {
            InventoryDialog dialog = new InventoryDialog(InventoryDialog.ERROR, "Bitte Bezeichnung (Pflichtfeld) eingeben!");
            dialog.showError();
            success = false;
        }

        try {
            int number = Integer.parseInt(numberField.getText());
            if (number < 0) {
                InventoryDialog dialog = new InventoryDialog(InventoryDialog.ERROR, "Bitte natürliche Zahl für Artikelnummer eingeben!");
                dialog.showError();
                success = false;
            }
        } catch (NumberFormatException nfe) {
            InventoryDialog dialog = new InventoryDialog(InventoryDialog.ERROR, "Bitte natürliche Zahl für Artikelnummer eingeben!");
            dialog.showError();
            success = false;
        }

        try {
            int amount = Integer.parseInt(amountField.getText());
            if (amount < 0) {
                InventoryDialog dialog = new InventoryDialog(InventoryDialog.ERROR, "Bitte natürliche Zahl für Menge eingeben!");
                dialog.showError();
                success = false;
            }
        } catch (NumberFormatException nfe) {
            InventoryDialog dialog = new InventoryDialog(InventoryDialog.ERROR, "Bitte natürliche Zahl für Menge eingeben!");
            dialog.showError();
            success = false;
        }
        return success;
    }

}
