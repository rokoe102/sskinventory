package de.robinkoesters.sskinventory.controller;

import de.robinkoesters.sskinventory.entity.Component;
import de.robinkoesters.sskinventory.repository.ComponentRepository;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;

import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.util.ResourceBundle;

public class ComponentListController implements Initializable {

    private MainViewController mainViewController;
    private ComponentRepository repo;

    @FXML private ListView<Component> componentList;
    @FXML private Button addButton;
    @FXML private Button deleteButton;
    @FXML private Button searchButton;
    @FXML private TextField errorField;
    @FXML private TextField searchField;

    public void setMainViewController(MainViewController mainViewController) {
        this.mainViewController = mainViewController;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        updateView();
        defineDoubleClickEvent();
    }

    public void updateView() {
        repo = new ComponentRepository();
        if (searchField.getText() != null && !searchField.getText().equals("")) {
            try {
                componentList.getItems().setAll(repo.findComponentsWithFilter(searchField.getText()));
            } catch (SQLException e) {
                errorField.setStyle("-fx-text-fill: red; -fx-font-size: 14px;");
                errorField.setText(e.getMessage());
            }
        } else {
            try {
                componentList.getItems().setAll(repo.findAllComponents());
            } catch (SQLException e) {
                errorField.setStyle("-fx-text-fill: red; -fx-font-size: 14px;");
                errorField.setText(e.getMessage());
            }
        }
    }

    private void defineDoubleClickEvent() {
        componentList.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent click) {
                if (click.getClickCount() == 2) {
                    Component current = componentList.getSelectionModel().getSelectedItem();
                    if (current != null && !mainViewController.isTabAlreadyOpen(current.getIdentifier())) {
                        try {
                            mainViewController.createComponentDetailTab(current);
                        } catch (IOException e) {
                            errorField.setStyle("-fx-text-fill: red; -fx-font-size: 14px;");
                            errorField.setText(e.getMessage());
                        }
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
        Component selection = componentList.getSelectionModel().getSelectedItem();
        if (selection != null) {
            try {
                repo.deleteComponent(selection);
                updateView();
            } catch (SQLException e) {
                errorField.setText(e.getMessage());
            }
        }
    }

    @FXML
    public void onSearch() {
        updateView();
    }
}
