package de.robinkoesters.sskinventory.controller;

import de.robinkoesters.sskinventory.SSKInventory;
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
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseEvent;

import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;

public class ArticleDetailController implements Initializable {

    private MainViewController mainViewController;
    private ComponentRepository componentRepository;
    private ArticleRepository articleRepository;

    private Article article;
    @FXML private TextField identifierField;
    @FXML private Button saveArticleButton;
    @FXML private TableView<ComponentAssignment> assignmentTable;
    @FXML private TableColumn<ComponentAssignment, String> idCol;
    @FXML private TableColumn<ComponentAssignment, Integer> amountCol;
    @FXML private Button addButton;
    @FXML private Button deleteButton;
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

        idCol.setCellValueFactory(new PropertyValueFactory<>("component"));
        amountCol.setCellValueFactory(new PropertyValueFactory<>("needed"));
    }

    private void defineDoubleClickEvent() {
        assignmentTable.setOnMouseClicked(click -> {
            if (click.getClickCount() == 2) {
                String currentComponent = assignmentTable.getSelectionModel().getSelectedItem().getComponent();
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
        this.identifierField.setStyle("-fx-text-fill: white;");

        if (article.isNewEntity()) {
            assignmentTable.setDisable(true);
            addButton.setDisable(true);
            deleteButton.setDisable(true);
        } else {
            assignmentTable.setDisable(false);
            assignmentTable.getItems().setAll(componentRepository.findComponentAssignmentsFor(article));
            addButton.setDisable(false);
            deleteButton.setDisable(false);
            ObservableList<String> components = componentRepository.findAllComponentRefs();
            for (ComponentAssignment ca : assignmentTable.getItems()) {
                components.remove(ca.getComponent());
            }
        }
    }

    @FXML
    public void onAddition() {
        List<String> components = componentRepository.findAllComponentRefs();
        ChoiceDialog<String> dialog = new ChoiceDialog<>(components.get(0), components);
        dialog.setTitle("Zuordnung");
        dialog.setHeaderText("Wählen Sie eine Komponente aus:");
        dialog.initOwner(SSKInventory.getMainStage());
        Optional<String> component = dialog.showAndWait();

        if (component.isPresent() && !component.get().equals("")) {
            TextInputDialog amountDialog = new TextInputDialog("1");
            amountDialog.setTitle("Zuordnung");
            amountDialog.setHeaderText("Bitte geben Sie die Stückzahl ein:");
            amountDialog.initOwner(SSKInventory.getMainStage());

            Optional<String> amountString = amountDialog.showAndWait();
            if (amountString.isPresent() && !amountString.get().equals("")) {
                int amount = Integer.parseInt(amountString.get());

                try {
                    componentRepository.createNewAssignment(this.article, component.get(), amount);
                    updateView(this.article);
                } catch (SQLException e) {
                    InventoryDialog errorDialog = new InventoryDialog(InventoryDialog.ERROR, e.getMessage());
                    errorDialog.showError();
                }
            }
        }
    }

    @FXML
    public void onDeletion() {
        ComponentAssignment selection = assignmentTable.getSelectionModel().getSelectedItem();
        if (selection != null) {
            try {
                componentRepository.deleteAssignment(selection);
                updateView(this.article);
            } catch (Exception e) {
                InventoryDialog dialog = new InventoryDialog(InventoryDialog.ERROR, "", e.getMessage());
                dialog.showError();
            }
        }
    }

    @FXML
    public void handleTyping() {
        saveArticleButton.setVisible(true);
        identifierField.setStyle("-fx-text-fill: aqua;");
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
                    InventoryDialog dialog = new InventoryDialog(InventoryDialog.ERROR, "Einfügen fehlgeschlagen", sql.getMessage());
                    dialog.showError();
                }
            } else if (!article.isNewEntity()) {
                try {
                    String oldCaption = this.article.getIdentifier();
                    article = articleRepository.updateArticle(article, ident);
                    saveArticleButton.setVisible(false);
                    updateView(this.article);
                    mainViewController.renameTab(oldCaption, article.getIdentifier());
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
            InventoryDialog dialog = new InventoryDialog(InventoryDialog.ERROR, "Pflichtfeld nicht gefüllt", "Bitte Bezeichnung (Pflichtfeld) eingeben!");
            dialog.showError();
            success = false;
        }
        return success;
    }

    @FXML
    public void onExcelExportPerformed() {
        if (!assignmentTable.getItems().isEmpty()) {
            try {
                ComponentExcelExport.exportComponentsForArticle(article);
            } catch (Exception e) {
                InventoryDialog errorDialog = new InventoryDialog(InventoryDialog.ERROR, e.getMessage());
                errorDialog.showError();
            }
        }
    }

}
