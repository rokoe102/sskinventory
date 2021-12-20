package de.robinkoesters.sskinventory.controller;

import de.robinkoesters.sskinventory.entity.Article;
import de.robinkoesters.sskinventory.entity.Component;
import javafx.collections.ObservableList;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;

import java.io.IOException;

public class MainViewController {

    @FXML
    public TabPane tabPane;

    public void prepareTabs() {
        try {
            createWelcomingTab();
            createArticleListTab();
            createComponentListTab();
            createAvailabilityTab();
            createSubmissionTab();
            createSettingsTab();
        }

        catch(IOException ioe) {
            System.out.println(ioe.getMessage());
        }
    }

    private void createWelcomingTab() throws IOException {
        Tab tab = new Tab("Willkommen");
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/Welcoming.fxml"));
        Parent root = loader.load();

        tab.setContent(root);
        tab.setClosable(true);
        tabPane.getTabs().add(tab);
    }

    private void createArticleListTab() throws IOException {
        Tab tab = new Tab("Artikel");
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/ArticleList.fxml"));
        Parent root = loader.load();

        ArticleListController article = loader.getController();
        article.setMainViewController(this);

        tab.setOnSelectionChanged(new EventHandler<Event>() {
            @Override
            public void handle(Event t) {
                if (tab.isSelected()) {
                    article.updateView();
                }
            }
        });

        tab.setContent(root);
        tab.setClosable(false);
        tabPane.getTabs().add(tab);
    }

    public void createArticleDetailTab(Article article) throws IOException {
        Tab tab = new Tab(article.getIdentifier());
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/ArticleDetail.fxml"));
        Parent root = loader.load();

        ArticleDetailController articleDetailController = loader.getController();
        articleDetailController.setMainViewController(this);
        articleDetailController.updateView(article);

        tab.setOnSelectionChanged(new EventHandler<Event>() {
            @Override
            public void handle(Event t) {
                if (tab.isSelected()) {
                    articleDetailController.updateView(null);
                }
            }
        });

        tab.setContent(root);
        tab.setClosable(true);
        tabPane.getTabs().add(tab);
        selectTab(article.getIdentifier());
    }

    public void createNewArticleDetailTab() throws IOException {
        Tab tab = new Tab("Neuer Artikel");
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/ArticleDetail.fxml"));
        Parent root = loader.load();

        ArticleDetailController articleDetailController = loader.getController();
        articleDetailController.setMainViewController(this);
        articleDetailController.updateView(new Article(""));

        tab.setContent(root);
        tab.setClosable(true);
        tabPane.getTabs().add(tab);
        selectTab("Neuer Artikel");
    }

    private void createComponentListTab() throws IOException {
        Tab tab = new Tab("Komponenten");
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/ComponentList.fxml"));
        Parent root = loader.load();

        ComponentListController componentListController = loader.getController();
        componentListController.setMainViewController(this);

        tab.setOnSelectionChanged(new EventHandler<Event>() {
            @Override
            public void handle(Event t) {
                if (tab.isSelected()) {
                    componentListController.updateView();
                }
            }
        });

        tab.setContent(root);
        tab.setClosable(false);
        tabPane.getTabs().add(tab);
    }

    public void createComponentDetailTab(Component component) throws IOException {
        Tab tab = new Tab(component.getIdentifier());
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/ComponentDetail.fxml"));
        Parent root = loader.load();

        ComponentDetailController componentDetailController = loader.getController();
        componentDetailController.setMainViewController(this);
        componentDetailController.updateView(component);

        tab.setContent(root);
        tab.setClosable(true);
        tabPane.getTabs().add(tab);
        selectTab(component.getIdentifier());
    }

    public void createNewComponentDetailTab() throws IOException {
        Tab tab = new Tab("Neue Komponente");
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/ComponentDetail.fxml"));
        Parent root = loader.load();

        ComponentDetailController componentDetailController = loader.getController();
        componentDetailController.setMainViewController(this);
        componentDetailController.updateView(new Component("", 0));

        tab.setContent(root);
        tab.setClosable(true);
        tabPane.getTabs().add(tab);
        selectTab("Neue Komponente");
    }

    public void createAvailabilityTab() throws IOException {
        Tab tab = new Tab("Verfügbarkeit");
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/Availability.fxml"));
        Parent root = loader.load();

        AvailabilityController availabilityController = loader.getController();
        availabilityController.setMainViewController(this);

        tab.setOnSelectionChanged(new EventHandler<Event>() {
            @Override
            public void handle(Event t) {
                if (tab.isSelected()) {
                    availabilityController.updateView();
                }
            }
        });

        tab.setContent(root);
        tab.setClosable(false);
        tabPane.getTabs().add(tab);
    }

    public void createSubmissionTab() throws IOException {
        Tab tab = new Tab("Änderungen");
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/Submission.fxml"));
        Parent root = loader.load();

        SubmissionController submissionController = loader.getController();
        submissionController.setMainViewController(this);

        tab.setOnSelectionChanged(new EventHandler<Event>() {
            @Override
            public void handle(Event t) {
                if (tab.isSelected()) {
                    submissionController.updateView();
                }
            }
        });

        tab.setContent(root);
        tab.setClosable(false);
        tabPane.getTabs().add(tab);
    }

    public void createSettingsTab() {
        Tab tab = new Tab("Einstellungen");
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/Settings.fxml"));
        Parent root = null;
        try {
            root = loader.load();
        } catch (IOException e) {
            e.printStackTrace();
        }

        SettingsController settingsController = loader.getController();
        settingsController.setMainViewController(this);

        tab.setOnSelectionChanged(new EventHandler<Event>() {
            @Override
            public void handle(Event t) {
                if (tab.isSelected()) {
                    settingsController.updateView();
                }
            }
        });

        tab.setContent(root);
        tab.setClosable(false);
        tabPane.getTabs().add(tab);
    }

    public boolean isTabAlreadyOpen(String name) {
        ObservableList<Tab> tabs = tabPane.getTabs();
        for(Tab tab : tabs) {
            if(tab.getText().equals(name)) {
                return true;
            }
        }
        return false;
    }

    public void selectTab(String caption) {
        int index = 0;
        for(Tab tab : tabPane.getTabs()) {
            if(tab.getText().equals(caption)) {
                index = tabPane.getTabs().indexOf(tab);
                break;
            }
        }

        tabPane.getSelectionModel().select(index);
    }

    public void renameTab(String oldCaption, String newCaption) {
        for(Tab tab : tabPane.getTabs()) {
            if(tab.getText().equals(oldCaption)) {
                tab.setText(newCaption);
            }
        }
    }
}
