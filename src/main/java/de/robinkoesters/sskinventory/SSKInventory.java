package de.robinkoesters.sskinventory;

import de.robinkoesters.sskinventory.controller.MainViewController;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

public class SSKInventory extends Application {

    private Parent parent;

    @Override
    public void start(Stage stage) throws Exception {
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

}
