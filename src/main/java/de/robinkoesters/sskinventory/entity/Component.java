package de.robinkoesters.sskinventory.entity;

import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;

public class Component {

    private SimpleStringProperty identifier;
    private SimpleIntegerProperty number;
    private SimpleIntegerProperty amount;

    public Component(String identifier, int number) {
        this.identifier = new SimpleStringProperty(identifier);
        this.number = new SimpleIntegerProperty(number);
        this.amount = new SimpleIntegerProperty(0);
    }

    public Component(String identifier, int number, int amount) {
        this.identifier = new SimpleStringProperty(identifier);
        this.number = new SimpleIntegerProperty(number);
        this.amount = new SimpleIntegerProperty(amount);
    }

    public String getIdentifier() {
        return identifier.get();
    }

    public void setIdentifier(String identifier) {
        this.identifier.set(identifier);
    }

    public int getNumber() {
        return number.get();
    }

    public void setNumber(int number) {
        this.number.set(number);
    }

    public int getAmount() {
        return amount.get();
    }

    public void setAmount(int amount) {
        this.amount.set(amount);
    }

    public boolean isNewEntity() {
        return identifier.get().equals("");
    }

    public boolean isExistingEntity() {
        return !isNewEntity();
    }

    @Override
    public String toString() {
        return this.getIdentifier();
    }
}
