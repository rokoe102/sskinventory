package de.robinkoesters.sskinventory.entity;

import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;

public class ComponentDelivery {

    private SimpleStringProperty component;
    private SimpleIntegerProperty amount;

    public ComponentDelivery(String component, int amount) {
        this.component = new SimpleStringProperty(component);
        this.amount = new SimpleIntegerProperty(amount);
    }

    public String getComponent() {
        return component.get();
    }

    public void setComponent(String component) {
        this.component.set(component);
    }

    public int getAmount() {
        return amount.get();
    }

    public void setAmount(int amount) {
        this.amount.set(amount);
    }
}
