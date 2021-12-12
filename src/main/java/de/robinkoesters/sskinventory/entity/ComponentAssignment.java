package de.robinkoesters.sskinventory.entity;

import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;

public class ComponentAssignment {

    private SimpleStringProperty component;
    private Article article;
    private SimpleIntegerProperty needed;

    private int remaining;

    private boolean forArticleView;

    public ComponentAssignment(Article article, String component, int needed) {
        this.article = article;
        this.component = new SimpleStringProperty(component);
        this.needed = new SimpleIntegerProperty(needed);
        forArticleView = true;
    }

    public ComponentAssignment(String article, String component, int needed) {
        this.article = new Article(article);
        this.component = new SimpleStringProperty(component);
        this.needed = new SimpleIntegerProperty(needed);
        forArticleView = false;
    }

    public String getComponent() {
        return component.get();
    }

    public void setComponent(String component) {
        this.component.set(component);
    }

    public Article getArticle() {
        return article;
    }

    public void setArticle(Article article) {
        this.article = article;
    }

    public int getNeeded() {
        return needed.get();
    }

    public SimpleIntegerProperty neededProperty() {
        return needed;
    }

    public void setNeeded(int needed) {
        this.needed.set(needed);
    }

    public int getRemaining() {
        return remaining;
    }

    @Override
    public String toString() {
        if (forArticleView) {
            return component.get() + " (" + needed.get() + "x)";
        } else {
            return article.getIdentifier() + " (" + needed.get() + "x)";
        }
    }
}
