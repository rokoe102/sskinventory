package de.robinkoesters.sskinventory.controller;

import de.robinkoesters.sskinventory.entity.Article;
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
import java.util.ResourceBundle;

public class ArticleDetailController implements Initializable {

    private MainViewController mainViewController;
    private ComponentRepository componentRepository;
    private ArticleRepository articleRepository;

    private Article article;
    @FXML private TextField identifierField;
    @FXML private Button saveArticleButton;
    @FXML private ListView<ComponentAssignment> assignmentList;
    @FXML private ComboBox<String> componentBox;
    @FXML private TextField amountField;
    @FXML private Button addButton;
    @FXML private Button deleteButton;
    @FXML private Button saveButton;
    @FXML private TextField errorField;

    public void setMainViewController(MainViewController mainViewController) {
        this.mainViewController = mainViewController;
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        this.componentRepository = new ComponentRepository();
        this.articleRepository = new ArticleRepository();

        saveArticleButton.setVisible(false);
        componentBox.setVisible(false);
        amountField.setVisible(false);
        saveButton.setVisible(false);
    }

    public void updateView(Article article) {
        this.article = article;
        this.identifierField.setText(article.getIdentifier());

        if (article.isNewEntity()) {
            assignmentList.setDisable(true);
            addButton.setDisable(true);
            deleteButton.setDisable(true);
        } else {
            assignmentList.setDisable(false);
            assignmentList.getItems().setAll(componentRepository.findComponentAssignmentsFor(article));
            addButton.setDisable(false);
            deleteButton.setDisable(false);
            ObservableList<String> components = componentRepository.findAllComponentRefs();
            for (ComponentAssignment ca : assignmentList.getItems()) {
                components.remove(ca.getComponent());
            }
            componentBox.getItems().setAll(components);
        }

        errorField.setText("");
    }

    @FXML
    public void add() {
        if (componentBox.isVisible()) {
            hideSaveDialog();
        } else {
            showSaveDialog();
        }

    }

    @FXML
    public void handleAssignmentSave() {
        if (componentBox.getSelectionModel().getSelectedItem() != null) {
            try {
                int amount = Integer.parseInt(amountField.getText());
                componentRepository.createNewAssignment(article, componentBox.getSelectionModel().getSelectedItem(), amount);
                updateView(this.article);
                hideSaveDialog();
            } catch (NumberFormatException nfe) {
                errorField.setText("Bitte eine natürliche Zahl als Menge eingeben!");
            } catch (SQLException sql) {
                errorField.setText("Speichern fehlgeschlagen: " + sql.getMessage());
            }
        }
    }

    @FXML
    public void handleDeletion() {
        ComponentAssignment selection = assignmentList.getSelectionModel().getSelectedItem();
        if (selection != null) {
            try {
                componentRepository.deleteAssignment(selection);
                updateView(this.article);
            } catch (Exception e) {
                errorField.setText(e.getMessage());
            }
        }
    }

    @FXML
    public void handleTyping() {
        saveArticleButton.setVisible(true);
    }

    @FXML
    public void handleArticleUpdate() {
        boolean success = validateFields();
        if (success) {
            String ident = identifierField.getText();
            if (article.isNewEntity() && !identifierField.getText().equals("")) {
                try {
                    this.article = articleRepository.createNewArticle(new Article(ident));
                    saveArticleButton.setVisible(false);
                    updateView(this.article);
                    mainViewController.renameTab("Neuer Artikel", article.getIdentifier());
                } catch (SQLException sql) {
                    errorField.setStyle("-fx-text-fill: red; -fx-font-size: 14px;");
                    errorField.setText("Einfügen fehlgeschlagen: " + sql.getMessage());
                }
            } else if (!article.isNewEntity()) {
                try {
                    article = articleRepository.updateArticle(article, ident);
                    saveArticleButton.setVisible(false);
                    updateView(this.article);

                    errorField.setStyle("-fx-text-fill: green; -fx-font-size: 14px;");
                    errorField.setText("Änderung erfolreich!");
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
        return success;
    }

    public void showSaveDialog() {
        componentBox.setVisible(true);
        amountField.setVisible(true);
        saveButton.setVisible(true);
    }

    public void hideSaveDialog() {
        componentBox.setVisible(false);
        amountField.setVisible(false);
        saveButton.setVisible(false);
    }

}
