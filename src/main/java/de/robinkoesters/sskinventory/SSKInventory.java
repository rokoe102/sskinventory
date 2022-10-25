package de.robinkoesters.sskinventory;

import de.robinkoesters.sskinventory.controller.MainViewController;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URI;
import java.util.Arrays;

public class SSKInventory extends Application {

    private static Stage mainStage;

    @Override
    public void start(Stage stage) throws Exception {
        setMainStage(stage);
        stage.setTitle("SSKInventory");
        stage.getIcons().add(new Image("file:icon.png"));
        FXMLLoader mainLoader = new FXMLLoader(getClass().getResource("/fxml/MainView.fxml"));
        Parent mainPane = mainLoader.load();
        Scene mainScene = new Scene(mainPane, 600, 450);

        MainViewController mvcontroller = mainLoader.getController();
        mvcontroller.prepareTabs();

        stage.setScene(mainScene);
        stage.show();
    }

    public static Stage getMainStage() {
        return mainStage;
    }

    public static void setMainStage(Stage mainStage) {
        SSKInventory.mainStage = mainStage;
    }
}
