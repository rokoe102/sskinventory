package de.robinkoesters.sskinventory.dialogs;

import javafx.scene.control.Alert;

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
        alert.show();
    }
}
