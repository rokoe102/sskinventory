package de.robinkoesters.sskinventory.repository;

import de.robinkoesters.sskinventory.entity.Article;
import de.robinkoesters.sskinventory.entity.Component;
import de.robinkoesters.sskinventory.entity.ComponentAssignment;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class ComponentRepository extends Repository {


    public ComponentRepository() {
        super();
    }

    public ObservableList<ComponentAssignment> findComponentAssignmentsFor(Article article) {
        ObservableList<ComponentAssignment> list = FXCollections.observableArrayList();
        try {
            PreparedStatement stmnt = conn.prepareStatement("SELECT * FROM ASSIGNMENT a WHERE a.article = ? ORDER BY component");
            stmnt.setString(1, article.getIdentifier());
            ResultSet rs = stmnt.executeQuery();
            while (rs.next()) {
                list.add(new ComponentAssignment(article, rs.getString("component"), rs.getInt("amount")));
            }
            rs.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    public List<Component> findComponentsAssignedToArticle(Article article) {
        List<Component> list = new ArrayList<>();
        try {
            PreparedStatement stmnt = conn.prepareStatement("SELECT c.* FROM ASSIGNMENT a " +
                                                                "           JOIN COMPONENT c on a.component = c.identifier " +
                                                                "           WHERE a.article = ? ORDER BY c.amount ASC");
            stmnt.setString(1, article.getIdentifier());
            ResultSet rs = stmnt.executeQuery();
            while (rs.next()) {
                list.add(new Component(rs.getString("identifier"), 0, rs.getInt("amount")));
            }
            rs.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    public ObservableList<ComponentAssignment> findComponentAssignmentsFor(Component component) {
        ObservableList<ComponentAssignment> list = FXCollections.observableArrayList();
        try {
            PreparedStatement stmnt = conn.prepareStatement("SELECT * FROM ASSIGNMENT a WHERE a.component = ?");
            stmnt.setString(1, component.getIdentifier());
            ResultSet rs = stmnt.executeQuery();
            while (rs.next()) {
                list.add(new ComponentAssignment(rs.getString("article"), rs.getString("component"), rs.getInt("amount")));
            }
            rs.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    public ObservableList<String> findAllComponentRefs() {
        ObservableList<String> list = FXCollections.observableArrayList();
        try {
            PreparedStatement stmnt = conn.prepareStatement("SELECT * FROM COMPONENT ORDER BY identifier");
            ResultSet rs = stmnt.executeQuery();
            while (rs.next()) {
                list.add(rs.getString("identifier"));
            }
            rs.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    public Component findComponentByIdentifier(String identifier) throws SQLException {
        PreparedStatement stmnt = conn.prepareStatement("SELECT * FROM COMPONENT c where c.identifier = ?");
        stmnt.setString(1, identifier);
        ResultSet rs = stmnt.executeQuery();
        if (rs.next()) {
            return new Component(rs.getString("identifier"), rs.getInt("number"), rs.getInt("amount"));
        } else {
            return null;
        }
    }

    public ObservableList<Component> findAllComponentsForListView() throws SQLException {
        ObservableList<Component> list = FXCollections.observableArrayList();
        PreparedStatement stmnt = conn.prepareStatement("SELECT * FROM COMPONENT ORDER BY identifier");
        ResultSet rs = stmnt.executeQuery();
        while (rs.next()) {
            list.add(new Component(rs.getString("identifier"), rs.getInt("number"), rs.getInt("amount")));
        }
        rs.close();

        return list;
    }

    public ObservableList<Component> findAllComponentsForExcelExport() throws SQLException {
        ObservableList<Component> list = FXCollections.observableArrayList();
        PreparedStatement stmnt = conn.prepareStatement("SELECT * FROM COMPONENT ORDER BY amount asc");
        ResultSet rs = stmnt.executeQuery();
        while (rs.next()) {
            list.add(new Component(rs.getString("identifier"), rs.getInt("number"), rs.getInt("amount")));
        }
        rs.close();

        return list;
    }

    public ObservableList<Component> findComponentsWithFilter(String searchString) throws SQLException {
        ObservableList<Component> list = FXCollections.observableArrayList();
        PreparedStatement stmnt = conn.prepareStatement("SELECT * FROM COMPONENT c where LOWER(c.identifier) LIKE ?");
        stmnt.setString(1, "%" + searchString.toLowerCase() + "%");
        ResultSet rs = stmnt.executeQuery();
        while (rs.next()) {
            list.add(new Component(rs.getString("identifier"), rs.getInt("number"), rs.getInt("amount")));
        }
        rs.close();

        return list;
    }

    public void createNewAssignment(Article article, String component, int amount) throws SQLException {
        String update = "INSERT INTO ASSIGNMENT(article, component, amount) VALUES(?,?,?)";
        PreparedStatement stmnt = conn.prepareStatement(update);
        stmnt.setString(1, article.getIdentifier());
        stmnt.setString(2, component);
        stmnt.setInt(3, amount);
        stmnt.executeUpdate();
    }

    public void deleteAssignment(ComponentAssignment assignment) throws SQLException {
        System.out.println("deleting assignment " + assignment);
        String update = "DELETE FROM ASSIGNMENT WHERE article = ? AND component = ?";
        PreparedStatement stmnt = conn.prepareStatement(update);
        stmnt.setString(1, assignment.getArticle().getIdentifier());
        stmnt.setString(2, assignment.getComponent());
        stmnt.executeUpdate();
    }

    public void deleteComponent(Component component) throws SQLException {
        String update = "DELETE FROM COMPONENT WHERE identifier = ?";
        PreparedStatement stmnt = conn.prepareStatement(update);
        stmnt.setString(1, component.getIdentifier());
        stmnt.executeUpdate();
    }

    public Component createNewComponent(Component component) throws SQLException {
        String update = "INSERT INTO COMPONENT(identifier, number, amount) VALUES(?,?,?)";
        PreparedStatement stmnt = conn.prepareStatement(update);
        stmnt.setString(1, component.getIdentifier());
        stmnt.setInt(2, component.getNumber());
        stmnt.setInt(3, component.getAmount());
        stmnt.executeUpdate();
        return component;
    }

    public Component updateComponent(Component component, String newIdent, int newNumber, int newAmount) throws SQLException {
        String update = "UPDATE COMPONENT SET identifier = ?, number = ?, amount = ? WHERE identifier = ?";
        PreparedStatement stmnt = conn.prepareStatement(update);
        stmnt.setString(1, newIdent);
        stmnt.setInt(2, newNumber);
        stmnt.setInt(3, newAmount);
        stmnt.setString(4, component.getIdentifier());
        stmnt.executeUpdate();

        component.setIdentifier(newIdent);
        component.setNumber(newNumber);
        component.setAmount(newAmount);
        return component;
    }

    public void updateAmount(String componentIdent, int amount) throws SQLException {
        String update = "UPDATE COMPONENT SET amount = ? WHERE identifier = ?";
        PreparedStatement stmnt = conn.prepareStatement(update);
        stmnt.setInt(1, amount);
        stmnt.setString(2, componentIdent);
        stmnt.executeUpdate();
    }

    public int getAmountForComponent(ComponentAssignment assignment) throws SQLException {
        PreparedStatement stmnt = conn.prepareStatement("SELECT * FROM COMPONENT c WHERE c.identifier = ?");
        stmnt.setString(1, assignment.getComponent());
        ResultSet rs = stmnt.executeQuery();
        if (rs.next()) {
            return rs.getInt("amount");
        } else {
            return -1;
        }
    }

}


