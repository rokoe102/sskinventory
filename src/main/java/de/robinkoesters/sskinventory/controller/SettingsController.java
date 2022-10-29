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
import java.util.Optional;
import java.util.ResourceBundle;

public class SettingsController implements Initializable {

    private MainViewController mainViewController;
    private SettingsRepository settingsRepository;

    @FXML private Button dbResetDialogButton;

    public void setMainViewController(MainViewController mainViewController) {
        this.mainViewController = mainViewController;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        settingsRepository = new SettingsRepository();
        updateView();
    }

    public void updateView() {
    }

    @FXML
    public void onDbReset() {
        try {
            InventoryDialog dialog = new InventoryDialog(InventoryDialog.INFO,  "", "Sind Sie sicher?");
            Optional<ButtonType> answer = dialog.showConfirmation();
            if (answer.isPresent() && answer.get().getButtonData().isDefaultButton()) {
                settingsRepository.resetDatabase();

                InventoryDialog info = new InventoryDialog(InventoryDialog.INFO, "Datenbank erfolgreich zurückgesetzt!");
                info.showInformation();
            }
        } catch (SQLException e) {
            InventoryDialog dialog = new InventoryDialog(InventoryDialog.ERROR, "Fehler beim Zurücksetzen", e.getMessage());
            dialog.showError();
        }
    }
}
