package de.robinkoesters.sskinventory.controller;

import de.robinkoesters.sskinventory.dialogs.InventoryDialog;
import de.robinkoesters.sskinventory.entity.Article;
import de.robinkoesters.sskinventory.entity.Component;
import de.robinkoesters.sskinventory.entity.ComponentAssignment;
import de.robinkoesters.sskinventory.export.ComponentExcelExport;
import de.robinkoesters.sskinventory.repository.ArticleRepository;
import de.robinkoesters.sskinventory.repository.ComponentRepository;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;

import java.io.IOException;
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
    @FXML private Button excelExportButton;

    public void setMainViewController(MainViewController mainViewController) {
        this.mainViewController = mainViewController;
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        defineDoubleClickEvent();
        this.componentRepository = new ComponentRepository();
        this.articleRepository = new ArticleRepository();

        saveArticleButton.setVisible(false);
        componentBox.setVisible(false);
        amountField.setVisible(false);
        saveButton.setVisible(false);
    }

    private void defineDoubleClickEvent() {
        assignmentList.setOnMouseClicked(click -> {
            if (click.getClickCount() == 2) {
                String currentComponent = assignmentList.getSelectionModel().getSelectedItem().getComponent();
                Component current = null;
                try {
                    current = componentRepository.findComponentByIdentifier(currentComponent);
                } catch (SQLException sql) {
                    InventoryDialog dialog = new InventoryDialog(InventoryDialog.ERROR, "Fehler beim Laden", sql.getMessage());
                    dialog.showError();
                }
                if (current != null && !mainViewController.isTabAlreadyOpen(current.getIdentifier())) {
                    try {
                        mainViewController.createComponentDetailTab(current);
                    } catch (IOException e) {
                        InventoryDialog dialog = new InventoryDialog(InventoryDialog.ERROR, "Fehler beim Öffnen", e.getMessage());
                        dialog.showError();
                    }
                }
            }
        });
    }

    public void updateView(Article updated) {
        if (updated != null) {
            this.article = updated;
        }
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
                if (amount < 1) {
                    throw new IllegalArgumentException();
                }
                componentRepository.createNewAssignment(article, componentBox.getSelectionModel().getSelectedItem(), amount);
                updateView(this.article);
                hideSaveDialog();
            } catch (IllegalArgumentException nfe) {
                InventoryDialog dialog = new InventoryDialog(InventoryDialog.ERROR, "Bitte eine natürliche Zahl größer 0 als Menge eingeben!");
                dialog.showError();
            } catch (SQLException sql) {
                InventoryDialog dialog = new InventoryDialog(InventoryDialog.ERROR, "Speichern fehlgeschlagen", sql.getMessage());
                dialog.showError();
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
                InventoryDialog dialog = new InventoryDialog("Fehler", "", e.getMessage());
                dialog.showError();
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
                    InventoryDialog dialog = new InventoryDialog("Fehler", "Einfügen fehlgeschlagen", sql.getMessage());
                    dialog.showError();
                }
            } else if (!article.isNewEntity()) {
                try {
                    article = articleRepository.updateArticle(article, ident);
                    saveArticleButton.setVisible(false);
                    updateView(this.article);
                } catch (SQLException sql) {
                    InventoryDialog dialog = new InventoryDialog("Fehler", "Änderung fehlgeschlagen", sql.getMessage());
                    dialog.showError();
                }

            }
        }


    }

    public boolean validateFields() {
        boolean success = true;
        if (identifierField.getText().equals("") || identifierField.getText() == null) {
            InventoryDialog dialog = new InventoryDialog("Fehler", "Pflichtfeld nicht gefüllt", "Bitte Bezeichnung (Pflichtfeld) eingeben!");
            dialog.showError();
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

    @FXML
    public void onExcelExportPerformed() {
        if (!assignmentList.getItems().isEmpty()) {
            try {
                ComponentExcelExport.exportComponentsForArticle(article);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
    }

}
