package de.robinkoesters.sskinventory.controller;

import de.robinkoesters.sskinventory.entity.Article;
import de.robinkoesters.sskinventory.entity.Component;
import de.robinkoesters.sskinventory.entity.ComponentAssignment;
import de.robinkoesters.sskinventory.repository.ArticleRepository;
import de.robinkoesters.sskinventory.repository.ComponentRepository;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;

import java.net.URL;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.ResourceBundle;

public class ComponentDetailController implements Initializable {

    private MainViewController mainViewController;
    private ComponentRepository componentRepository;
    private ArticleRepository articleRepository;

    private Component component;
    @FXML
    private TextField identifierField;
    @FXML private TextField numberField;
    @FXML private Button saveComponentButton;
    @FXML private ListView<ComponentAssignment> assignmentList;
    @FXML private TextField amountField;
    @FXML private TextField errorField;

    public void setMainViewController(MainViewController mainViewController) {
        this.mainViewController = mainViewController;
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        this.componentRepository = new ComponentRepository();
        this.articleRepository = new ArticleRepository();

        saveComponentButton.setVisible(false);
    }

    public void updateView(Component component) {
        this.component = component;
        this.identifierField.setText(component.getIdentifier());
        this.numberField.setText(String.valueOf(component.getNumber()));
        System.out.println("updateView: " + component.getAmount());
        this.amountField.setText(String.valueOf(component.getAmount()));

        assignmentList.getItems().setAll(componentRepository.findComponentAssignmentsFor(component));
        ObservableList<String> components = componentRepository.findAllComponentRefs();
        for (ComponentAssignment ca : assignmentList.getItems()) {
            components.remove(ca.getComponent());
        }

        errorField.setText("");
    }

    @FXML
    public void handleTyping() {
        saveComponentButton.setVisible(true);
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
                    errorField.setStyle("-fx-text-fill: greenyellow; -fx-font-size: 14px;");
                    errorField.setText("Einfügen erfolgreich! (" + LocalDateTime.now() + ")");
                } catch (SQLException sql) {
                    errorField.setStyle("-fx-text-fill: red; -fx-font-size: 14px;");
                    errorField.setText("Einfügen fehlgeschlagen: " + sql.getMessage());
                }
            } else if (component.isExistingEntity()) {
                try {
                    component = componentRepository.updateComponent(component, ident, number, amount);
                    saveComponentButton.setVisible(false);
                    updateView(this.component);
                    errorField.setStyle("-fx-text-fill: greenyellow; -fx-font-size: 14px;");
                    errorField.setText("Änderung erfolgreich! (" + LocalDateTime.now() + ")" );
                } catch (SQLException sql) {
                    errorField.setStyle("-fx-text-fill: red; -fx-font-size: 14px;");
                    errorField.setText("Änderung fehlgeschlagen: " + sql.getMessage());
                }
            }

        }
    }

    public boolean validateFields() {
        boolean success = true;
        errorField.setStyle("-fx-text-fill: red; -fx-font-size: 14px;");
        if (identifierField.getText().equals("") || identifierField.getText() == null) {
            errorField.setText("Bitte Bezeichnung (Pflichtfeld) eingeben!");
            success = false;
        }

        try {
            int number = Integer.parseInt(numberField.getText());
            if (number < 0) {
                errorField.setText("Bitte natürliche Zahl für Artikelnummer eingeben!");
                success = false;
            }
        } catch (NumberFormatException nfe) {
            errorField.setText("Bitte natürliche Zahl für Artikelnummer eingeben!");
            success = false;
        }

        try {
            int amount = Integer.parseInt(amountField.getText());
            if (amount < 0) {
                errorField.setText("Bitte natürliche Zahl für Menge eingeben!");
                success = false;
            }
        } catch (NumberFormatException nfe) {
            errorField.setText("Bitte natürliche Zahl für Menge eingeben!");
            success = false;
        }
        return success;
    }

}
