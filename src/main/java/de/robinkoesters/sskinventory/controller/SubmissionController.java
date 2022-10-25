package de.robinkoesters.sskinventory.controller;

import de.robinkoesters.sskinventory.dialogs.InventoryDialog;
import de.robinkoesters.sskinventory.entity.Article;
import de.robinkoesters.sskinventory.repository.ArticleRepository;
import de.robinkoesters.sskinventory.repository.AvailabilityRepository;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URL;
import java.sql.SQLException;
import java.util.ResourceBundle;

public class SubmissionController implements Initializable {

    private static final Logger LOGGER = LoggerFactory.getLogger(SubmissionController.class);

    private AvailabilityRepository availabilityRepository;

    @FXML private ComboBox<Article> articleBox;
    @FXML private TextField amountField;
    @FXML private Button queryButton;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        updateView();
    }

    public void updateView() {
        try {
            ArticleRepository articleRepository = new ArticleRepository();
            availabilityRepository = new AvailabilityRepository();
            articleBox.getItems().setAll(articleRepository.findAllArticles());
            queryButton.setDisable(true);
        } catch (SQLException e) {
            LOGGER.error(e.getMessage(), e);
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
        if (articleBox.getSelectionModel().getSelectedItem() != null) {
            queryButton.setDisable(false);
        }
    }

    @FXML
    public void onQueryStarted() {
        boolean success = validateFields();
        if (success) {
            try {
                Article article = articleBox.getSelectionModel().getSelectedItem();
                int amount = Integer.parseInt(amountField.getText());

                String info = availabilityRepository.getSubmissionInfo(article, amount);

                InventoryDialog dialog = new InventoryDialog("Ergebnis", info);
                dialog.showResult();
            } catch (SQLException e) {
                LOGGER.error(e.getMessage(), e);
                InventoryDialog dialog = new InventoryDialog("Fehler", e.getMessage());
                dialog.showError();
            }
        }
    }

    private boolean validateFields() {
        boolean success = true;
        try {
            Integer.parseInt(amountField.getText());
        } catch (NumberFormatException nfe) {
            success = false;
            InventoryDialog dialog = new InventoryDialog("Fehler", "Bitte natürliche Zahl für Menge eingeben!");
            dialog.showError();
        }
        return success;
    }
}
