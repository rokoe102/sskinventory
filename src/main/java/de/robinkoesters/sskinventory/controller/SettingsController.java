package de.robinkoesters.sskinventory.controller;

import de.robinkoesters.sskinventory.dialogs.InventoryDialog;
import de.robinkoesters.sskinventory.entity.Article;
import de.robinkoesters.sskinventory.repository.ArticleRepository;
import de.robinkoesters.sskinventory.repository.AvailabilityRepository;
import de.robinkoesters.sskinventory.repository.SettingsRepository;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;

import java.net.URL;
import java.sql.SQLException;
import java.util.ResourceBundle;

public class SettingsController implements Initializable {

    private MainViewController mainViewController;
    private SettingsRepository settingsRepository;

    @FXML private Button dbResetDialogButton;
    @FXML private Label question;
    @FXML private Button dbResetButton;
    @FXML private Button abortButton;

    public void setMainViewController(MainViewController mainViewController) {
        this.mainViewController = mainViewController;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        settingsRepository = new SettingsRepository();
        question.setVisible(false);
        dbResetButton.setVisible(false);
        abortButton.setVisible(false);
        updateView();
    }

    public void updateView() {
    }

    @FXML
    public void onDbResetDialogTriggered() {
        question.setVisible(true);
        dbResetButton.setVisible(true);
        abortButton.setVisible(true);
    }

    @FXML
    public void onDbReset() {
        try {
            settingsRepository.resetDatabase();
            question.setVisible(false);
            dbResetButton.setVisible(false);
            abortButton.setVisible(false);
        } catch (SQLException e) {
            InventoryDialog dialog = new InventoryDialog("Fehler", "Fehler beim Zurücksetzen", e.getMessage());
            dialog.showError();
        }

        InventoryDialog dialog = new InventoryDialog("Information", "Datenbank erfolgreich zurückgesetzt!");
        dialog.showInformation();
    }

    @FXML
    public void onAbort() {
        question.setVisible(false);
        dbResetButton.setVisible(false);
        abortButton.setVisible(false);
    }
}
