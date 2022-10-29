package de.robinkoesters.sskinventory.controller;

import de.robinkoesters.sskinventory.dialogs.InventoryDialog;
import de.robinkoesters.sskinventory.entity.Component;
import de.robinkoesters.sskinventory.export.ComponentExcelExport;
import de.robinkoesters.sskinventory.repository.ComponentRepository;
import javafx.collections.FXCollections;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Text;

import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.util.ResourceBundle;

public class ComponentListController implements Initializable {

    private static final Logger LOGGER = LoggerFactory.getLogger(ComponentListController.class);

    private MainViewController mainViewController;
    private ComponentRepository repo;

    @FXML
    TableView<Component> componentTable;
    @FXML
    TableColumn<Component, String> idCol;
    @FXML
    TableColumn<Component, Integer> articleNoCol;
    @FXML
    TableColumn<Component, Integer> amountCol;
    @FXML private Button addButton;
    @FXML private Button deleteButton;
    @FXML private Button excelExportButton;
    @FXML private Button searchButton;
    @FXML private TextField idFilter;
    @FXML private TextField articleNoFilter;
    @FXML private TextField amountFilter;
    @FXML private ChoiceBox<String> amountOperator;


    public void setMainViewController(MainViewController mainViewController) {
        this.mainViewController = mainViewController;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        idCol.setCellValueFactory(new PropertyValueFactory<>("identifier"));
        articleNoCol.setCellValueFactory(new PropertyValueFactory<>("number"));
        amountCol.setCellValueFactory(new PropertyValueFactory<>("amount"));

        amountOperator.getItems().addAll(FXCollections.observableArrayList("=", ">", ">=", "<=", "<"));
        amountOperator.getSelectionModel().select(0);

        updateView();
        defineDoubleClickEvent();
    }

    public void updateView() {
        repo = new ComponentRepository();
        try {
            Integer articleNo = null;
            if (!articleNoFilter.getText().equals("")) {
                articleNo = Integer.parseInt(articleNoFilter.getText());
            }

            Integer amount = null;
            if (!amountFilter.getText().equals("")) {
                amount = Integer.parseInt(amountFilter.getText());
            }

            componentTable.getItems().setAll(repo.findComponentsWithFilter(idFilter.getText(),
                                                                           articleNo,
                                                                           amountOperator.getSelectionModel().getSelectedItem(),
                                                                           amount));
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            InventoryDialog dialog = new InventoryDialog(InventoryDialog.ERROR, e.getMessage());
            dialog.showError();
        }
    }

    private void defineDoubleClickEvent() {
        componentTable.setOnMouseClicked(click -> {
            if (click.getClickCount() == 2) {
                Component current = componentTable.getSelectionModel().getSelectedItem();
                if (current != null && !mainViewController.isTabAlreadyOpen(current.getIdentifier())) {
                    try {
                        mainViewController.createComponentDetailTab(current);
                    } catch (IOException e) {
                        InventoryDialog dialog = new InventoryDialog(InventoryDialog.ERROR, e.getMessage());
                        dialog.showError();
                    }
                }
            }
        });
    }

    @FXML
    public void onAddition() throws IOException {
        mainViewController.createNewComponentDetailTab();
    }

    @FXML
    public void onDeletion() {
        Component selection = componentTable.getSelectionModel().getSelectedItem();
        if (selection != null) {
            try {
                repo.deleteComponent(selection);
                updateView();
            } catch (SQLException e) {
                InventoryDialog dialog = new InventoryDialog(InventoryDialog.ERROR, e.getMessage());
                dialog.showError();
            }
        }
    }

    @FXML
    public void onSearch() {
        updateView();
    }

    @FXML
    public void onExcelExportPerformed() {
        if (!componentTable.getItems().isEmpty()) {
            try {
                ComponentExcelExport.exportAllComponents();
            } catch (Exception e) {
                InventoryDialog dialog = new InventoryDialog(InventoryDialog.ERROR, e.getMessage());
                dialog.showError();
            }
        }
    }
}
