package poker.controllers;

import javafx.scene.Group;
import javafx.scene.paint.Color;
import javafx.scene.shape.Ellipse;

public class PokerTableManualDesign {

    private final Group tableGroup;

    public PokerTableManualDesign() {
        tableGroup = new Group();
        createTable();
    }

    private void createTable() {
        // Create the poker table shape (ellipse)
        Ellipse table = new Ellipse(400, 300, 350, 200); // x, y, width, height
        table.setFill(Color.GREEN);
        table.setStroke(Color.BROWN);
        table.setStrokeWidth(10);

        // Add the table to the group
        tableGroup.getChildren().add(table);
    }

    // Getter for the tableGroup
    public Group getTableGroup() {
        return tableGroup;
    }
}
