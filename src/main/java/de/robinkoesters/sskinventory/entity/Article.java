package de.robinkoesters.sskinventory.entity;

import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.ObservableList;

public class Article {

    private SimpleStringProperty identifier;

    private ObservableList<Component> componentList;

    public Article(String identifier) {
        this.identifier = new SimpleStringProperty(identifier);
    }

    public String getIdentifier() {
        return identifier.get();
    }

    public void setIdentifier(String identifier) {
        this.identifier.set(identifier);
    }

    public ObservableList<Component> getComponentList() {
        return componentList;
    }

    public void setComponentList(ObservableList<Component> componentList) {
        this.componentList = componentList;
    }

    public boolean isNewEntity() {
        return identifier.get().equals("");
    }

    @Override
    public String toString() {
        return identifier.get();
    }
}
