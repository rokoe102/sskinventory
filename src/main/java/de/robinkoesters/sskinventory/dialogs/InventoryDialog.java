package de.robinkoesters.sskinventory.dialogs;

import de.robinkoesters.sskinventory.SSKInventory;
import javafx.scene.control.Alert;
import javafx.scene.control.TextArea;
import javafx.scene.layout.GridPane;

public class InventoryDialog {

    private String title;
    private String header;
    private String content;

    public InventoryDialog(String title, String header, String content) {
        this.title = title;
        this.header = header;
        this.content = content;
    }

    public InventoryDialog(String title, String content) {
        this.title = title;
        this.content = content;
    }

    public void showResult() {
        TextArea textArea = new TextArea(content);
        textArea.setEditable(false);
        textArea.setWrapText(true);
        GridPane gridPane = new GridPane();
        gridPane.setMaxWidth(Double.MAX_VALUE);
        gridPane.add(textArea, 0, 0);

        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.getDialogPane().setContent(gridPane);
        alert.initOwner(SSKInventory.getMainStage());
        alert.showAndWait();
    }

    public void showError() {
        show(Alert.AlertType.ERROR);
    }

    public void showInformation() {
        show(Alert.AlertType.INFORMATION);
    }

    private void show(Alert.AlertType type) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(header);
        alert.setContentText(content);
        alert.initOwner(SSKInventory.getMainStage());
        alert.show();
    }
}
