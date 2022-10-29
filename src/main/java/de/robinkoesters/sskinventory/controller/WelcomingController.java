package de.robinkoesters.sskinventory.controller;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.control.Label;

import java.net.URL;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ResourceBundle;

public class WelcomingController implements Initializable {

    @FXML private Label dateLabel;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        dateLabel.setText(LocalDate.now().format(DateTimeFormatter.ofPattern("EEE, dd.MM.yyyy")));
        dateLabel.setAlignment(Pos.CENTER);
    }
}
