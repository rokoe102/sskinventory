package de.robinkoesters.sskinventory.controller;

import de.robinkoesters.sskinventory.dialogs.InventoryDialog;
import de.robinkoesters.sskinventory.entity.Article;
import de.robinkoesters.sskinventory.repository.ArticleRepository;
import de.robinkoesters.sskinventory.repository.AvailabilityRepository;
import de.robinkoesters.sskinventory.repository.ComponentRepository;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;

import java.net.URL;
import java.sql.SQLException;
import java.util.ResourceBundle;

public class AvailabilityController implements Initializable {

    private ArticleRepository articleRepository;
    private AvailabilityRepository availabilityRepository;

    @FXML private ComboBox<Article> articleBox;
    @FXML private Button queryButton;

    public void setMainViewController(MainViewController mainViewController) {
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        updateView();
    }

    public void updateView() {
        try {
            articleRepository = new ArticleRepository();
            availabilityRepository = new AvailabilityRepository();
            articleBox.getItems().setAll(articleRepository.findAllArticles());
            queryButton.setDisable(true);
        } catch (SQLException e) {
            InventoryDialog dialog = new InventoryDialog("Fehler", e.getMessage());
            dialog.showError();
        }

    }

    @FXML
    public void onSelection() {
        queryButton.setDisable(false);
    }

    @FXML
    public void onQueryStarted() {
        try {
            String info = availabilityRepository.getAvailabilityInfo(articleBox.getSelectionModel().getSelectedItem());
            InventoryDialog dialog = new InventoryDialog("Ergebnis", info);
            dialog.showResult();
        } catch (SQLException e) {
            InventoryDialog dialog = new InventoryDialog("Fehler", e.getMessage());
            dialog.showError();
        }
    }
}
