package de.robinkoesters.sskinventory.controller;

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

    private MainViewController mainViewController;
    private ArticleRepository articleRepository;
    private AvailabilityRepository availabilityRepository;

    @FXML private ComboBox<Article> articleBox;
    @FXML private Button queryButton;
    @FXML private TextArea resultField;
    @FXML private TextField errorField;

    public void setMainViewController(MainViewController mainViewController) {
        this.mainViewController = mainViewController;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        updateView();
    }

    public void updateView() {
        resultField.setText("");
        try {
            articleRepository = new ArticleRepository();
            availabilityRepository = new AvailabilityRepository();
            articleBox.getItems().setAll(articleRepository.findAllArticles());
            queryButton.setDisable(true);
        } catch (SQLException e) {
            errorField.setStyle("-fx-text-fill: red; -fx-font-size: 14px;");
            errorField.setText(e.getMessage());
        }

    }

    @FXML
    public void onSelection() {
        queryButton.setDisable(false);
    }

    @FXML
    public void onQueryStarted() {
        resultField.setText("");
        try {
            resultField.setText(availabilityRepository.getAvailabilityInfo(articleBox.getSelectionModel().getSelectedItem()));
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
