package de.robinkoesters.sskinventory.repository;

import de.robinkoesters.sskinventory.entity.Article;
import de.robinkoesters.sskinventory.entity.Component;
import de.robinkoesters.sskinventory.entity.ComponentAssignment;
import javafx.collections.ObservableList;

import java.sql.SQLException;

public class AvailabilityRepository extends Repository {

    ComponentRepository componentRepository;

    public AvailabilityRepository() {
        super();
        this.componentRepository = new ComponentRepository();
    }

    public String getAvailabilityInfo(Article article) throws SQLException {
        StringBuilder resultString = new StringBuilder("Mit den gegebenen Komponenten können derzeit");
        boolean isAvailable = false;
        int result = 99999;
        ObservableList<ComponentAssignment> assignments = componentRepository.findComponentAssignmentsFor(article);
        for (ComponentAssignment assignment : assignments) {
            int amount = componentRepository.getAmountForComponent(assignment);
            System.out.println("found " + amount + " " + assignment.getComponent());
            System.out.println("... " + assignment.getNeeded() + " are needed");
            if (amount >= assignment.getNeeded()) {
                int current = Math.floorDiv(amount,assignment.getNeeded());
                if (current < result) {
                    result = current;
                }
                isAvailable = true;
            } else {
                isAvailable = false;
                break;
            }

        }

        resultString.append(" ").append(result).append(" ").append(article.getIdentifier()).append(" hergestellt werden.\n");
        if (isAvailable) {
            resultString.append("Es bleiben folgende Mengen übrig:\n");
            for (ComponentAssignment assignment : assignments) {
                int amount = componentRepository.getAmountForComponent(assignment);
                resultString.append(assignment.getComponent());
                resultString.append(": ");
                resultString.append(amount - (result * assignment.getNeeded()));
                resultString.append("\n");
            }
        } else {
            resultString.append("Es fehlen folgende Mengen:\n");
            for (ComponentAssignment assignment : assignments) {
                int amount = componentRepository.getAmountForComponent(assignment);
                if (amount < assignment.getNeeded()) {
                    resultString.append(assignment.getComponent());
                    resultString.append(": ");
                    resultString.append(assignment.getNeeded() - amount);
                    resultString.append("\n");
                }

            }
        }

        return resultString.toString();
    }

    public int getNumberOfAvailableArticles(Article article) throws SQLException {
        int result = 99999;
        ObservableList<ComponentAssignment> assignments = componentRepository.findComponentAssignmentsFor(article);
        for (ComponentAssignment assignment : assignments) {
            int amount = componentRepository.getAmountForComponent(assignment);
            System.out.println("found " + amount + " " + assignment.getComponent());
            System.out.println("... " + assignment.getNeeded() + " are needed");
            if (amount >= assignment.getNeeded()) {
                int current = Math.floorDiv(amount,assignment.getNeeded());
                if (current < result) {
                    result = current;
                }
            } else {
                result = 0;
                break;
            }

        }
        return result;
    }

    public String getSubmissionInfo(Article article, int built) throws SQLException {
        int buildable = getNumberOfAvailableArticles(article);
        if (buildable < built) {
            return "Es können derzeit nicht genug Artikel \"" + article + "\" gebaut werden.";
        }
        StringBuilder resultString = new StringBuilder("Es wurden ");
        resultString.append(built)
                    .append(" ")
                    .append(article)
                    .append(" gebaut.\n")
                    .append("Es bleiben folgende Mengen übrig:\n");
        ObservableList<ComponentAssignment> assignments = componentRepository.findComponentAssignmentsFor(article);
        for (ComponentAssignment assignment : assignments) {
            int currentAmount = componentRepository.getAmountForComponent(assignment);
            int newAmount = currentAmount - (assignment.getNeeded() * built);
            componentRepository.updateAmount(assignment.getComponent(), newAmount);
            resultString.append(assignment.getComponent())
                        .append(": ")
                        .append(newAmount)
                        .append("\n");
        }
        return resultString.toString();
    }
}
