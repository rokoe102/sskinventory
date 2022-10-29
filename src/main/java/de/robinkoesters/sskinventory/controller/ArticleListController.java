package de.robinkoesters.sskinventory.controller;

import de.robinkoesters.sskinventory.dialogs.InventoryDialog;
import de.robinkoesters.sskinventory.entity.Article;
import de.robinkoesters.sskinventory.export.ComponentExcelExport;
import de.robinkoesters.sskinventory.repository.ArticleRepository;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseEvent;

import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.util.ResourceBundle;

public class ArticleListController implements Initializable {

    private MainViewController mainViewController;
    private ArticleRepository repo;

    @FXML private TableView<Article> articleTable;
    @FXML private TableColumn<Article, String> idCol;
    @FXML private Button addButton;
    @FXML private Button deleteButton;
    @FXML private Button searchButton;
    @FXML private TextField searchField;

    public void setMainViewController(MainViewController mainViewController) {
        this.mainViewController = mainViewController;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        repo = new ArticleRepository();
        defineDoubleClickEvent();
        updateView();

        idCol.setCellValueFactory(new PropertyValueFactory<>("identifier"));
    }

    public void updateView() {
        repo = new ArticleRepository();
        if (searchField.getText() != null && !searchField.getText().equals("")) {
            try {
                articleTable.getItems().setAll(repo.findArticlesWithFilter(searchField.getText()));
            } catch (SQLException e) {
                InventoryDialog dialog = new InventoryDialog("Fehler", e.getMessage());
                dialog.showInformation();
            }
        } else {
            try {
                articleTable.getItems().setAll(repo.findAllArticles());
            } catch (SQLException e) {
                InventoryDialog dialog = new InventoryDialog("Fehler", e.getMessage());
                dialog.showInformation();
            }
        }
    }

    private void defineDoubleClickEvent() {
        articleTable.setOnMouseClicked(click -> {
            if (click.getClickCount() == 2) {
                Article current = articleTable.getSelectionModel().getSelectedItem();
                if (current != null && !mainViewController.isTabAlreadyOpen(current.getIdentifier())) {
                    try {
                        mainViewController.createArticleDetailTab(current);
                    } catch (IOException e) {
                        InventoryDialog dialog = new InventoryDialog("Fehler", e.getMessage());
                        dialog.showInformation();
                    }
                }
            }
        });
    }

    @FXML
    public void onAddition() throws IOException {
        mainViewController.createNewArticleDetailTab();
    }

    @FXML
    public void onDeletion() {
        Article selection = articleTable.getSelectionModel().getSelectedItem();
        if (selection != null) {
            try {
                repo.deleteArticle(selection);
                updateView();
            } catch (SQLException e) {
                InventoryDialog dialog = new InventoryDialog("Fehler", "Löschen fehlgeschlagen", e.getMessage());
                dialog.showInformation();
            }
        }
    }

    @FXML
    public void onSearch() {
        updateView();
    }
}
