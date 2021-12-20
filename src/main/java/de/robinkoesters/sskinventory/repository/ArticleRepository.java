package de.robinkoesters.sskinventory.repository;

import de.robinkoesters.sskinventory.entity.Article;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class ArticleRepository extends Repository {

    public ArticleRepository() {
        super();
    }

    public ObservableList<Article> findAllArticles() throws SQLException {
        ObservableList<Article> list = FXCollections.observableArrayList();
        PreparedStatement stmnt = conn.prepareStatement("SELECT * FROM ARTICLE a ORDER BY identifier");
        ResultSet rs = stmnt.executeQuery();
        while(rs.next()) {
            list.add(new Article(rs.getString("identifier")));
        }
        rs.close();
        return list;
    }

    public ObservableList<Article> findArticlesWithFilter(String searchString) throws SQLException {
        ObservableList<Article> list = FXCollections.observableArrayList();
        PreparedStatement stmnt = conn.prepareStatement("SELECT * FROM ARTICLE a WHERE LOWER(identifier) LIKE ? ORDER BY identifier");
        stmnt.setString(1, "%" + searchString.toLowerCase() + "%");
        ResultSet rs = stmnt.executeQuery();
        while(rs.next()) {
            list.add(new Article(rs.getString("identifier")));
        }
        rs.close();
        return list;
    }

    public void deleteArticle(Article article) throws SQLException {
        String update = "DELETE FROM ARTICLE WHERE identifier = ?";
        PreparedStatement stmnt = conn.prepareStatement(update);
        stmnt.setString(1, article.getIdentifier());
        stmnt.executeUpdate();
    }

    public Article createNewArticle(Article article) throws SQLException {
        String update = "INSERT INTO ARTICLE(identifier) VALUES(?)";
        PreparedStatement stmnt = conn.prepareStatement(update);
        stmnt.setString(1, article.getIdentifier());
        stmnt.executeUpdate();
        return article;
    }

    public Article updateArticle(Article article, String newIdent) throws SQLException {
        String update = "UPDATE ARTICLE SET identifier = ? WHERE identifier = ?";
        PreparedStatement stmnt = conn.prepareStatement(update);
        stmnt.setString(1, newIdent);
        stmnt.setString(2, article.getIdentifier());
        stmnt.executeUpdate();

        article.setIdentifier(newIdent);
        System.out.println("new entity: " + article);
        return article;
    }
}
